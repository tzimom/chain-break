package net.tzimom.chainbreak.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakToolService {
    boolean isTool(Material itemType);
    boolean isChainBreakEnabled(ItemStack tool);
    boolean canStartChainBreak(Block block, ItemStack tool);

    boolean toggleChainBreak(ItemStack tool);
}
