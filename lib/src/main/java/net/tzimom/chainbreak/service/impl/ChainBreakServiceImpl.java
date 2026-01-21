package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ChainBreakConfigService;
import net.tzimom.chainbreak.service.ChainBreakService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private final Plugin plugin;
    private final ChainBreakConfigService chainBreakConfigService;

    public ChainBreakServiceImpl(Plugin plugin, ChainBreakConfigService chainBreakConfigService) {
        this.plugin = plugin;
        this.chainBreakConfigService = chainBreakConfigService;
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
            Collection<Block> visitedBlocks, Collection<Block> previousLayer) {
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
        }, chainBreakConfigService.config().stepInterval());
    }

    @Override
    public void startChain(Block block, ItemStack tool, LivingEntity user) {
        var blockType = block.getType();

        var visitedBlocks = new ArrayList<Block>();
        visitedBlocks.add(block);

        scheduleNextLayer(blockType, tool, user, chainBreakConfigService.config().maxRange(), visitedBlocks, List.of(block));
    }
}
