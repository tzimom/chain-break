package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private static final String metadataKey = "chainbreak";

    private final Plugin plugin;
    private final ConfigService configService;

    public ChainBreakServiceImpl(Plugin plugin, ConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
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

    private void scheduleNextLayer(Block root, Material target, ItemStack tool, Player player,
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

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.getInventory().getItemInMainHand() != tool)
                return;

            currentLayer.forEach(block -> {
                block.setMetadata(metadataKey, new FixedMetadataValue(plugin, true));

                player.breakBlock(block);
                player.playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);

                block.removeMetadata("chainbreak", plugin);

                var toolMeta = tool.getItemMeta();

                if (!(toolMeta instanceof Damageable damageable))
                    return;

                damageable.setDamage(damageable.getDamage() + 1);
            });

            scheduleNextLayer(root, target, tool, player, maxRange, stepInterval, visitedBlocks, currentLayer);
        }, stepInterval);
    }

    @Override
    public void startChain(Block block, ItemStack tool, Player player, int level) {
        var levelConfig = configService.config().enchantment().levels().get(level - 1);
        var blockType = block.getType();

        var visitedBlocks = new ArrayList<Block>();
        visitedBlocks.add(block);

        scheduleNextLayer(block, blockType, tool, player, levelConfig.maxRange(), levelConfig.stepInterval(),
                visitedBlocks, List.of(block));
    }

    @Override
    public boolean isBlockInChainBreak(Block block) {
        return block.hasMetadata(metadataKey);
    }
}
