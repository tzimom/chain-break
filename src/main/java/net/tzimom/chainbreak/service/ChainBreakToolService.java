package net.tzimom.chainbreak.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakToolService {
    boolean canStartChainBreak(Block block, ItemStack tool);

    boolean isTool(Material itemType);
    boolean toggleChainBreak(ItemStack tool);
}
