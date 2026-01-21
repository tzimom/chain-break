package net.tzimom.chainbreak;

import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.service.RecipeService;
import net.tzimom.chainbreak.eventhandler.BlockBreakEventHandler;
import net.tzimom.chainbreak.eventhandler.InventoryClickEventHandler;
import net.tzimom.chainbreak.eventhandler.PrepareAnvilEventHandler;
import net.tzimom.chainbreak.eventhandler.PrepareGrindstoneEventHandler;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.impl.RecipeServiceImpl;
import net.tzimom.chainbreak.service.impl.ChainBreakServiceImpl;
import net.tzimom.chainbreak.service.impl.ChainBreakEnchantmentServiceImpl;

public class ChainBreakPlugin extends JavaPlugin {
    private final ChainBreakService chainBreakService = new ChainBreakServiceImpl(this);
    private final ChainBreakEnchantmentService chainBreakEnchantmentService = new ChainBreakEnchantmentServiceImpl(this);
    private final RecipeService recipeService = new RecipeServiceImpl(this, chainBreakEnchantmentService);

    @Override
    public void onEnable() {
        var server = getServer();
        var recipes = recipeService.createRecipes();

        recipes.forEach(recipe -> server.addRecipe(recipe));

        var pluginManager = server.getPluginManager();

        pluginManager.registerEvents(new PrepareAnvilEventHandler(chainBreakEnchantmentService), this);
        pluginManager.registerEvents(new InventoryClickEventHandler(this, chainBreakEnchantmentService), this);
        pluginManager.registerEvents(new PrepareGrindstoneEventHandler(chainBreakEnchantmentService), this);
        pluginManager.registerEvents(new BlockBreakEventHandler(chainBreakService, chainBreakEnchantmentService), this);
    }
}
