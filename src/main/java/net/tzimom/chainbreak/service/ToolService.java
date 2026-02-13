package net.tzimom.chainbreak.service;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ToolService {
    boolean isTool(Material itemType);
    boolean isActive(ItemStack tool);
    boolean isCompatible(Material toolType, Material blockType);

    boolean toggleChainBreak(ItemStack tool);
}
