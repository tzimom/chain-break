package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private static final String metadataKey = "chainbreak";

    private final Plugin plugin;
    private final ConfigService configService;
    private final ChainBreakToolService toolService;

    private final NamespacedKey tagKey;

    public ChainBreakServiceImpl(Plugin plugin, ConfigService configService, ChainBreakToolService toolService) {
        this.plugin = plugin;
        this.configService = configService;
        this.toolService = toolService;

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

    private void breakLayer(Block root, Material target, UUID toolTag, Player player,
            int maxRange, int stepInterval, Collection<Block> visitedBlocks, Collection<Block> previousLayer) {
        var maxRangeSquared = maxRange * maxRange;
        var currentLayer = previousLayer.stream()
                .flatMap(block -> getNeighbors(block).stream())
                .filter(block -> !visitedBlocks.contains(block))
                .filter(block -> block.getType() == target)
                .filter(block -> block.getLocation().subtract(root.getLocation()).lengthSquared() <= maxRangeSquared)
                .collect(Collectors.toSet());

        if (currentLayer.isEmpty())
            return;

        visitedBlocks.addAll(currentLayer);

        var tool = player.getInventory().getItemInMainHand();

        if (!getTag(tool).equals(toolTag))
            return;

        if (!toolService.isChainBreakEnabled(tool))
            return;

        currentLayer.forEach(block -> {
            block.setMetadata(metadataKey, new FixedMetadataValue(plugin, true));
            player.playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
            player.breakBlock(block);
            block.removeMetadata(metadataKey, plugin);
        });

        scheduleNextLayer(root, target, toolTag, player, maxRange, stepInterval, visitedBlocks, currentLayer);
    }

    private void scheduleNextLayer(Block root, Material target, UUID toolTag, Player player, int maxRange,
            int stepInterval, Collection<Block> visitedBlocks, Collection<Block> previousLayer) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            breakLayer(root, target, toolTag, player, maxRange, stepInterval, visitedBlocks, previousLayer);
        }, stepInterval);
    }

    @Override
    public void startChain(Block block, ItemStack tool, Player player, int level) {
        var levelConfig = configService.config().enchantment().levels().get(level - 1);
        var blockType = block.getType();
        var toolTag = getTag(tool);

        var visitedBlocks = new ArrayList<Block>();
        visitedBlocks.add(block);

        scheduleNextLayer(block, blockType, toolTag, player, levelConfig.maxRange(), levelConfig.stepInterval(),
                visitedBlocks, List.of(block));
    }

    @Override
    public boolean isBlockInChainBreak(Block block) {
        return block.hasMetadata(metadataKey);
    }
}
