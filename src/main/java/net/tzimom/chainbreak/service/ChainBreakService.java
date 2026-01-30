package net.tzimom.chainbreak.service;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakService {
    void startChain(Block block, ItemStack tool, Player player, int level);
    boolean isBlockInChainBreak(Block block);
}
