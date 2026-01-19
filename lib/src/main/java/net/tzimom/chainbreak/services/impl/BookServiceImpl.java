package net.tzimom.chainbreak.services.impl;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tzimom.chainbreak.services.BookService;

public class BookServiceImpl implements BookService {
    private final Plugin plugin;

    private static final String RECIPE_KEY = "book";
    private static final String BOOK_KEY = "book";

    public BookServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createItem() {
        var item = new ItemStack(Material.ENCHANTED_BOOK);

        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.lore(List.of(Component.text("Chain Break")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)));

        var itemMeta = item.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();
        var key = new NamespacedKey(plugin, BOOK_KEY);

        dataContainer.set(key, PersistentDataType.BOOLEAN, true);
        itemMeta.addEnchant(Enchantment.LUNGE, 3, true);
        item.setItemMeta(itemMeta);

        return item;
    }

    public Recipe createRecipe() {
        var key = new NamespacedKey(plugin, RECIPE_KEY);
        var item = createItem();

        return new ShapedRecipe(key, item)
                .shape("aba", "cdc", "aea")
                .setIngredient('a', Material.CRYING_OBSIDIAN)
                .setIngredient('b', Material.HEAVY_CORE)
                .setIngredient('c', Material.ECHO_SHARD)
                .setIngredient('d', Material.BOOK)
                .setIngredient('e', Material.CREAKING_HEART);
    };
}
