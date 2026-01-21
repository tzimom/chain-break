package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.service.ChainBreakService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private static final int MAX_RANGE = 8;
    private static final int STEP_INTERVAL = 4;

    private final Plugin plugin;

    public ChainBreakServiceImpl(Plugin plugin) {
        this.plugin = plugin;
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

    private void scheduleNextLayer(Material target, ItemStack tool, LivingEntity user, int remainingRange,
            Collection<Block> visitedBlocks,
            Collection<Block> previousLayer) {
        if (remainingRange <= 0)
            return;

        var currentLayer = previousLayer.stream()
                .flatMap(block -> getNeighbors(block).stream())
                .filter(block -> !visitedBlocks.contains(block))
                .filter(block -> block.getType() == target)
                .collect(Collectors.toSet());

        if (currentLayer.isEmpty())
            return;

        visitedBlocks.addAll(currentLayer);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            currentLayer.forEach(block -> {
                block.breakNaturally(tool, true, true);
                tool.damage(1, user);
            });

            scheduleNextLayer(target, tool, user, remainingRange - 1, visitedBlocks, currentLayer);
        }, STEP_INTERVAL);
    }

    @Override
    public void startChain(Block block, ItemStack tool, LivingEntity user) {
        var blockType = block.getType();

        var visitedBlocks = new ArrayList<Block>();
        visitedBlocks.add(block);

        scheduleNextLayer(blockType, tool, user, MAX_RANGE, visitedBlocks, List.of(block));
    }
}
