package net.tzimom.chainbreak.service;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakToolService {
    boolean canStartChainBreak(Block block, ItemStack tool);
    boolean toggleChainBreak(ItemStack tool);
}
