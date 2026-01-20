package net.tzimom.chainbreak.services;

import org.bukkit.inventory.ItemStack;

import net.tzimom.chainbreak.models.CustomEnchantment;

public interface CustomEnchantmentService {
    int getEnchantmentLevel(ItemStack item, CustomEnchantment enchantment);

    default boolean hasEnchantment(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantmentLevel(item, enchantment) > 0;
    }

    boolean canEnchant(ItemStack item, CustomEnchantment enchantment, int level);

    default boolean canEnchant(ItemStack item, CustomEnchantment enchantment) {
        return canEnchant(item, enchantment, 1);
    }

    boolean tryEnchant(ItemStack item, CustomEnchantment enchantment, int level);

    default boolean tryEnchant(ItemStack item, CustomEnchantment enchantment) {
        return tryEnchant(item, enchantment, 1);
    }

    void enchant(ItemStack item, CustomEnchantment enchantment, int level);

    default void enchant(ItemStack item, CustomEnchantment enchantment) {
        enchant(item, enchantment, 1);
    }

    void disenchant(ItemStack item, CustomEnchantment enchantment);
    void disenchant(ItemStack item);
}
