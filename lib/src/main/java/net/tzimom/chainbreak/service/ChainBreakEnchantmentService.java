package net.tzimom.chainbreak.service;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakEnchantmentService {
    boolean isEnchantable(Material material);
    boolean hasEnchantment(ItemStack item);

    void enchant(ItemStack item);
    void disenchant(ItemStack item);
}
