package net.tzimom.chainbreak;

import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.service.RecipeService;
import net.tzimom.chainbreak.eventhandler.BlockBreakEventHandler;
import net.tzimom.chainbreak.eventhandler.InventoryClickEventHandler;
import net.tzimom.chainbreak.eventhandler.PlayerInteractEventHandler;
import net.tzimom.chainbreak.eventhandler.PrepareAnvilEventHandler;
import net.tzimom.chainbreak.eventhandler.PrepareGrindstoneEventHandler;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.ChainBreakToolService;
import net.tzimom.chainbreak.service.impl.RecipeServiceImpl;
import net.tzimom.chainbreak.service.impl.ChainBreakServiceImpl;
import net.tzimom.chainbreak.service.impl.ChainBreakToolServiceImpl;
import net.tzimom.chainbreak.service.impl.ChainBreakEnchantmentServiceImpl;

public class ChainBreakPlugin extends JavaPlugin {
    private final ChainBreakService chainBreakService = new ChainBreakServiceImpl(this);
    private final ChainBreakEnchantmentService chainBreakEnchantmentService = new ChainBreakEnchantmentServiceImpl(this);
    private final ChainBreakToolService chainBreakToolService = new ChainBreakToolServiceImpl(this, chainBreakEnchantmentService);
    private final RecipeService recipeService = new RecipeServiceImpl(this, chainBreakEnchantmentService);

    private final PrepareAnvilEventHandler prepareAnvilEventHandler = new PrepareAnvilEventHandler(chainBreakEnchantmentService);
    private final InventoryClickEventHandler inventoryClickEventHandler = new InventoryClickEventHandler(this, chainBreakEnchantmentService);
    private final PrepareGrindstoneEventHandler prepareGrindstoneEventHandler = new PrepareGrindstoneEventHandler(chainBreakEnchantmentService);
    private final PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler(chainBreakToolService, chainBreakEnchantmentService);
    private final BlockBreakEventHandler blockBreakEventHandler = new BlockBreakEventHandler(chainBreakService, chainBreakToolService);

    @Override
    public void onEnable() {
        var server = getServer();
        var recipes = recipeService.createRecipes();

        recipes.forEach(recipe -> server.addRecipe(recipe));

        var pluginManager = server.getPluginManager();

        pluginManager.registerEvents(prepareAnvilEventHandler, this);
        pluginManager.registerEvents(inventoryClickEventHandler, this);
        pluginManager.registerEvents(prepareGrindstoneEventHandler, this);
        pluginManager.registerEvents(playerInteractEventHandler, this);
        pluginManager.registerEvents(blockBreakEventHandler, this);
    }
}
