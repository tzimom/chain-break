package net.tzimom.chainbreak;

import java.util.Collection;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.service.RecipeService;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.config.service.impl.ConfigServiceImpl;
import net.tzimom.chainbreak.eventhandler.BlockBreakEventHandler;
import net.tzimom.chainbreak.eventhandler.EntityDeathEventHandler;
import net.tzimom.chainbreak.eventhandler.InventoryClickEventHandler;
import net.tzimom.chainbreak.eventhandler.LootGenerateEventHandler;
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
    private final ConfigService configService = new ConfigServiceImpl(this);

    private final ChainBreakService chainBreakService = new ChainBreakServiceImpl(this, configService);
    private final ChainBreakEnchantmentService enchantmentService = new ChainBreakEnchantmentServiceImpl(this, configService);
    private final ChainBreakToolService toolService = new ChainBreakToolServiceImpl(this, configService, enchantmentService);
    private final RecipeService recipeService = new RecipeServiceImpl(this, configService, enchantmentService);

    private final Collection<Listener> eventHandlers = List.of(
        new PrepareAnvilEventHandler(configService, enchantmentService, toolService),
        new InventoryClickEventHandler(this, enchantmentService),
        new PrepareGrindstoneEventHandler(enchantmentService),
        new PlayerInteractEventHandler(configService, toolService, enchantmentService),
        new BlockBreakEventHandler(chainBreakService, enchantmentService, toolService),
        new EntityDeathEventHandler(configService, enchantmentService),
        new LootGenerateEventHandler(configService, enchantmentService));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configService.reload();

        var server = getServer();
        var recipes = recipeService.createRecipes();
        recipes.forEach(recipe -> server.addRecipe(recipe));

        var pluginManager = server.getPluginManager();
        eventHandlers.forEach(handler -> pluginManager.registerEvents(handler, this));
    }
}
