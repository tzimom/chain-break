package net.tzimom.chainbreak;

import org.bukkit.plugin.java.JavaPlugin;

import net.tzimom.chainbreak.services.BookService;
import net.tzimom.chainbreak.services.impl.BookServiceImpl;

public class ChainBreakPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BookService bookService = new BookServiceImpl(this);

        getServer().addRecipe(bookService.createRecipe());
    }
}
