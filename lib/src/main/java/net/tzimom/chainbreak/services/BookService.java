package net.tzimom.chainbreak.services;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public interface BookService {
    ItemStack createItem();
    Recipe createRecipe();
}
