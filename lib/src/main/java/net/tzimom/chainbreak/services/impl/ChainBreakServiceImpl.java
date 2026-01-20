package net.tzimom.chainbreak.services.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.services.ChainBreakService;

public class ChainBreakServiceImpl implements ChainBreakService {
    private static final int MAX_RANGE = 8;
    private static final int STEP_INTERVAL = 4;

    private final Plugin plugin;

    public ChainBreakServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean canStartChain(Material blockType, Material toolType) {
        return switch (toolType) {
            case NETHERITE_PICKAXE -> switch (blockType) {
                case COAL_ORE, IRON_ORE, GOLD_ORE, REDSTONE_ORE, LAPIS_ORE, DIAMOND_ORE, EMERALD_ORE, COPPER_ORE,
                        DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_REDSTONE_ORE,
                        DEEPSLATE_LAPIS_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_COPPER_ORE, NETHER_QUARTZ_ORE,
                        NETHER_GOLD_ORE, ANCIENT_DEBRIS ->
                    true;
                default -> false;
            };
            case NETHERITE_AXE -> switch (blockType) {
                case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, MANGROVE_LOG, CHERRY_LOG,
                        PALE_OAK_LOG, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG,
                        STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_LOG, STRIPPED_CHERRY_LOG,
                        STRIPPED_PALE_OAK_LOG ->
                    true;
                default -> false;
            };
            case NETHERITE_HOE -> switch (blockType) {
                case OAK_LEAVES, SPRUCE_LEAVES, BIRCH_LEAVES, JUNGLE_LEAVES, ACACIA_LEAVES, DARK_OAK_LEAVES,
                        MANGROVE_LEAVES, CHERRY_LEAVES, PALE_OAK_LEAVES ->
                    true;
                default -> false;
            };
            default -> false;
        };
    }

    private Set<Block> getNeighbors(Block block) {
        return Set.of(
                block.getRelative(BlockFace.UP),
                block.getRelative(BlockFace.DOWN),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.WEST));
    }

    private void scheduleNextLayer(ItemStack tool, Material target, LivingEntity user, int remainingRange,
            Set<Block> visitedBlocks,
            Set<Block> previousLayer) {
        if (remainingRange <= 0)
            return;

        var currentLayer = visitedBlocks.stream()
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

            scheduleNextLayer(tool, target, user, remainingRange - 1, visitedBlocks, currentLayer);
        }, STEP_INTERVAL);
    }

    @Override
    public void tryStartChain(Block block, ItemStack tool, LivingEntity user) {
        var blockType = block.getType();
        var toolType = tool.getType();

        if (!canStartChain(blockType, toolType))
            return;

        var visitedBlocks = new HashSet<Block>();
        visitedBlocks.add(block);

        scheduleNextLayer(tool, blockType, user, MAX_RANGE, visitedBlocks, Set.of(block));
    }
}
