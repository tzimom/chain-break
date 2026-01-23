package net.tzimom.chainbreak.service.impl;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.service.RecipeService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class RecipeServiceImpl implements RecipeService {
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    private final NamespacedKey chainBreakBookRecipeKey;

    public RecipeServiceImpl(Plugin plugin, ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;

        chainBreakBookRecipeKey = new NamespacedKey(plugin, "book");
    }

    @Override
    public Collection<Recipe> createRecipes() {
        var chainBreakBook = new ItemStack(Material.ENCHANTED_BOOK);
        chainBreakEnchantmentService.enchant(chainBreakBook, 1);

        return List.of(new ShapedRecipe(chainBreakBookRecipeKey, chainBreakBook)
                .shape("aba", "cdc", "aea")
                .setIngredient('a', Material.CRYING_OBSIDIAN)
                .setIngredient('b', Material.DIAMOND)
                .setIngredient('c', Material.ECHO_SHARD)
                .setIngredient('d', Material.BOOK)
                .setIngredient('e', Material.CREAKING_HEART));
    }
}
