package net.tzimom.chainbreak.service.impl;

import java.util.Collection;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.service.RecipeService;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class RecipeServiceImpl implements RecipeService {
    private final Plugin plugin;
    private final ConfigService configService;
    private final ChainBreakEnchantmentService enchantmentService;

    public RecipeServiceImpl(Plugin plugin, ConfigService configService, ChainBreakEnchantmentService enchantmentService) {
        this.plugin = plugin;
        this.configService = configService;
        this.enchantmentService = enchantmentService;
    }

    @Override
    public Collection<Recipe> createRecipes() {
        return configService.config().recipes().stream().<Recipe>map(recipeConfig -> {
            var resultConfig = recipeConfig.result();
            var resultMaterial = resultConfig.material();
            var resultAmount = resultConfig.amount();
            var resultChainBreakLevel = resultConfig.chainBreakLevel();
            var recipeKey = new NamespacedKey(plugin, resultMaterial.toString() + "." + resultChainBreakLevel);

            var result = new ItemStack(resultMaterial, resultAmount);
            enchantmentService.enchant(result, resultChainBreakLevel);

            var recipe = new ShapedRecipe(recipeKey, result);
            recipe.shape(recipeConfig.shape());
            recipeConfig.ingredients().forEach(recipe::setIngredient);

            return recipe;
        }).toList();
    }
}
