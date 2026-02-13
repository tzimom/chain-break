package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.EnchantmentService;
import net.tzimom.chainbreak.service.ToolService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private static final String metadataKey = "chainbreak";

    private final Plugin plugin;
    private final ConfigService configService;
    private final ToolService toolService;
    private final EnchantmentService enchantmentService;

    private final NamespacedKey tagKey;

    public ChainBreakServiceImpl(Plugin plugin, ConfigService configService, ToolService toolService,
            EnchantmentService enchantmentService) {
        this.plugin = plugin;
        this.configService = configService;
        this.toolService = toolService;
        this.enchantmentService = enchantmentService;

        tagKey = new NamespacedKey(plugin, "tool.tag");
    }

    private Collection<Block> getNeighbors(Block block) {
        return List.of(
                block.getRelative(BlockFace.UP),
                block.getRelative(BlockFace.DOWN),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.WEST));
    }

    private UUID getTag(ItemStack tool) {
        var itemMeta = tool.getItemMeta();

        if (itemMeta == null)
            itemMeta = Bukkit.getItemFactory().getItemMeta(tool.getType());

        var dataContainer = itemMeta.getPersistentDataContainer();

        if (dataContainer.has(tagKey))
            return UUID.fromString(dataContainer.get(tagKey, PersistentDataType.STRING));

        var uuid = UUID.randomUUID();
        dataContainer.set(tagKey, PersistentDataType.STRING, uuid.toString());
        tool.setItemMeta(itemMeta);

        return uuid;
    }

    private Collection<Block> getNextLayer(ChainBreakInfo info, Collection<Block> visitedBlocks,
            Collection<Block> previousLayer) {
        var maxRange = info.maxRange();
        var maxRangeSquared = maxRange * maxRange;
        var root = info.root();

        return previousLayer.stream()
                .map(this::getNeighbors)
                .flatMap(Collection::stream)
                .filter(block -> !visitedBlocks.contains(block))
                .filter(block -> block.getType() == info.target())
                .filter(block -> block.getLocation().subtract(root.getLocation()).lengthSquared() <= maxRangeSquared)
                .collect(Collectors.toSet());
    }

    private void breakLayer(ChainBreakInfo info, Collection<Block> visitedBlocks, Collection<Block> layer) {
        var player = info.player();

        for (var block : layer) {
            var metadataValue = new FixedMetadataValue(plugin, true);
            var location = block.getLocation();
            var sound = block.getBlockData().getSoundGroup().getBreakSound();

            block.setMetadata(metadataKey, metadataValue);

            if (player.breakBlock(block))
                player.playSound(location, sound, SoundCategory.BLOCKS, 1f, 1f);

            block.removeMetadata(metadataKey, plugin);
        }
    }

    private void queueNext(ChainBreakInfo info, Collection<Block> visitedBlocks, Collection<Block> previousLayer) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                () -> propagate(info, visitedBlocks, previousLayer), info.stepInterval());
    }

    private void propagate(ChainBreakInfo info, Collection<Block> visitedBlocks, Collection<Block> previousLayer) {
        var tool = info.player().getInventory().getItemInMainHand();

        if (!getTag(tool).equals(info.toolTag()))
            return;

        if (!toolService.isActive(tool))
            return;

        var nextLayer = getNextLayer(info, visitedBlocks, previousLayer);

        if (nextLayer.isEmpty())
            return;

        visitedBlocks.addAll(nextLayer);

        breakLayer(info, visitedBlocks, nextLayer);
        queueNext(info, visitedBlocks, nextLayer);
    }

    @Override
    public void tryStartChainBreak(Player player, Block root, ItemStack tool) {
        if (root.hasMetadata(metadataKey))
            return;

        if (tool == null || tool.getType() == Material.AIR)
            return;

        if (!toolService.isCompatible(tool.getType(), root.getType()) || !toolService.isActive(tool))
            return;

        var target = root.getType();
        var toolTag = getTag(tool);
        var level = enchantmentService.getEnchantmentLevel(tool);
        var levelConfig = configService.config().enchantment().level(level);
        var info = new ChainBreakInfo(player, root, target, toolTag, levelConfig.maxRange(),
                levelConfig.stepInterval());

        var visitedBlocks = new ArrayList<Block>();
        visitedBlocks.add(root);

        queueNext(info, visitedBlocks, List.of(root));
    }

    private record ChainBreakInfo(Player player, Block root, Material target, UUID toolTag, int maxRange,
            int stepInterval) {
    }
}
