package net.tzimom.chainbreak;

import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.events.BlockBreakEventHandler;
import net.tzimom.chainbreak.events.PrepareAnvilEventHandler;
import net.tzimom.chainbreak.events.PrepareGrindstoneEventHandler;
import net.tzimom.chainbreak.services.RecipeService;
import net.tzimom.chainbreak.services.ChainBreakService;
import net.tzimom.chainbreak.services.CustomEnchantmentService;
import net.tzimom.chainbreak.services.impl.RecipeServiceImpl;
import net.tzimom.chainbreak.services.impl.ChainBreakServiceImpl;
import net.tzimom.chainbreak.services.impl.CustomEnchantmentServiceImpl;

public class ChainBreakPlugin extends JavaPlugin {
    private final CustomEnchantmentService customEnchantmentService = new CustomEnchantmentServiceImpl(this);
    private final RecipeService recipeService = new RecipeServiceImpl(this, customEnchantmentService);
    private final ChainBreakService chainBreakService = new ChainBreakServiceImpl(this);

    @Override
    public void onEnable() {
        var server = getServer();
        var recipes = recipeService.createRecipes();

        recipes.forEach(recipe -> server.addRecipe(recipe));

        var pluginManager = server.getPluginManager();

        pluginManager.registerEvents(new PrepareAnvilEventHandler(customEnchantmentService), this);
        pluginManager.registerEvents(new PrepareGrindstoneEventHandler(customEnchantmentService), this);
        pluginManager.registerEvents(new BlockBreakEventHandler(customEnchantmentService, chainBreakService), this);
    }
}
