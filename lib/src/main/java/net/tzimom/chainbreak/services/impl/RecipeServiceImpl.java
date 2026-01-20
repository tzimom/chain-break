package net.tzimom.chainbreak.services.impl;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.models.CustomEnchantment;
import net.tzimom.chainbreak.services.RecipeService;
import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class RecipeServiceImpl implements RecipeService {
    private final CustomEnchantmentService customEnchantmentService;

    private final NamespacedKey chainBreakBookRecipeKey;

    public RecipeServiceImpl(Plugin plugin, CustomEnchantmentService customEnchantmentService) {
        this.customEnchantmentService = customEnchantmentService;

        chainBreakBookRecipeKey = new NamespacedKey(plugin, "book");
    }

    @Override
    public Collection<Recipe> createRecipes() {
        var chainBreakBook = new ItemStack(Material.ENCHANTED_BOOK);
        customEnchantmentService.enchant(chainBreakBook, CustomEnchantment.CHAIN_BREAK);

        return List.of(new ShapedRecipe(chainBreakBookRecipeKey, chainBreakBook)
                .shape("aba", "cdc", "aea")
                .setIngredient('a', Material.CRYING_OBSIDIAN)
                .setIngredient('b', Material.HEAVY_CORE)
                .setIngredient('c', Material.ECHO_SHARD)
                .setIngredient('d', Material.BOOK)
                .setIngredient('e', Material.CREAKING_HEART));
    }
}
