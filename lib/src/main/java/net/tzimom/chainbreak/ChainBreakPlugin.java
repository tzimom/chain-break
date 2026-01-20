package net.tzimom.chainbreak;

import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.events.BlockBreakEventHandler;
import net.tzimom.chainbreak.events.PrepareAnvilEventHandler;
import net.tzimom.chainbreak.events.PrepareGrindstoneEventHandler;
import net.tzimom.chainbreak.services.BookService;
import net.tzimom.chainbreak.services.ChainBreakService;
import net.tzimom.chainbreak.services.CustomEnchantmentService;
import net.tzimom.chainbreak.services.impl.BookServiceImpl;
import net.tzimom.chainbreak.services.impl.ChainBreakServiceImpl;
import net.tzimom.chainbreak.services.impl.CustomEnchantmentServiceImpl;

public class ChainBreakPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        var customEnchantmentService = new CustomEnchantmentServiceImpl(this);
        var bookService = new BookServiceImpl(this, customEnchantmentService);
        var chainBreakService = new ChainBreakServiceImpl(this);

        registerEventHandlers(customEnchantmentService, chainBreakService);
        registerRecipes(bookService);
    }

    private void registerEventHandlers(CustomEnchantmentService customEnchantmentService,
            ChainBreakService chainBreakService) {
        var pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PrepareAnvilEventHandler(customEnchantmentService), this);
        pluginManager.registerEvents(new PrepareGrindstoneEventHandler(customEnchantmentService), this);
        pluginManager.registerEvents(new BlockBreakEventHandler(customEnchantmentService, chainBreakService), this);
    }

    private void registerRecipes(BookService bookService) {
        var server = getServer();

        server.addRecipe(bookService.createRecipe());
    }
}
