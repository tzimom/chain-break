package net.tzimom.chainbreak.service;

import org.bukkit.inventory.ItemStack;

public interface ChainBreakEnchantmentService {
    int getEnchantmentLevel(ItemStack item);
    default boolean hasEnchantment(ItemStack item) { return getEnchantmentLevel(item) >= 1; }

    void enchant(ItemStack item, int level);
    void disenchant(ItemStack item);

    void updateItem(ItemStack result);
}
