package net.tzimom.chainbreak.services.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.models.CustomEnchantment;
import net.tzimom.chainbreak.services.BookService;
import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class BookServiceImpl implements BookService {
    private final CustomEnchantmentService customEnchantmentService;

    private final NamespacedKey recipeKey;

    public BookServiceImpl(Plugin plugin, CustomEnchantmentService customEnchantmentService) {
        this.customEnchantmentService = customEnchantmentService;

        recipeKey = new NamespacedKey(plugin, "book");
    }

    public ItemStack createItem() {
        var item = new ItemStack(Material.ENCHANTED_BOOK);
        customEnchantmentService.enchant(item, CustomEnchantment.CHAIN_BREAK);
        return item;
    }

    public Recipe createRecipe() {
        return new ShapedRecipe(recipeKey, createItem())
                .shape("aba", "cdc", "aea")
                .setIngredient('a', Material.CRYING_OBSIDIAN)
                .setIngredient('b', Material.HEAVY_CORE)
                .setIngredient('c', Material.ECHO_SHARD)
                .setIngredient('d', Material.BOOK)
                .setIngredient('e', Material.CREAKING_HEART);
    }
}
