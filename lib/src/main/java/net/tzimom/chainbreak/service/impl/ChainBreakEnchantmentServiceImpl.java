package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class ChainBreakEnchantmentServiceImpl implements ChainBreakEnchantmentService {
    private static final Enchantment DUMMY_ENCHANTMENT = Enchantment.LUNGE;
    private static final Component LORE_COMPONENT = Component.text("Chain Break")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false);

    private final NamespacedKey enchantmentKey;
    private final NamespacedKey dummyEnchantmentKey;

    public ChainBreakEnchantmentServiceImpl(Plugin plugin) {
        enchantmentKey = new NamespacedKey(plugin, "enchantment.chainbreak");
        dummyEnchantmentKey = new NamespacedKey(plugin, "enchantment.dummy");
    }

    @Override
    public boolean hasEnchantment(ItemStack item) {
        return item.getPersistentDataContainer()
                .getOrDefault(enchantmentKey, PersistentDataType.BOOLEAN, false);
    }

    @Override
    public boolean isEnchantable(Material itemType) {
        return Tag.ITEMS_PICKAXES.isTagged(itemType)
                || Tag.ITEMS_AXES.isTagged(itemType)
                || Tag.ITEMS_HOES.isTagged(itemType)
                || Tag.ITEMS_SHOVELS.isTagged(itemType);
    }

    @Override
    public void enchant(ItemStack item) {
        disenchant(item);

        var itemMeta = item.getItemMeta();
        var lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<Component>();

        lore.addLast(LORE_COMPONENT);
        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantmentKey, PersistentDataType.BOOLEAN, true);

        if (itemMeta.getEnchants().isEmpty()) {
            dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, true);

            itemMeta.addEnchant(DUMMY_ENCHANTMENT, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
    }

    @Override
    public void disenchant(ItemStack item) {
        if (!hasEnchantment(item))
            return;

        var itemMeta = item.getItemMeta();
        var lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<Component>();

        lore = lore.stream().filter(component -> !component.equals(LORE_COMPONENT)).toList();
        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantmentKey, PersistentDataType.BOOLEAN, false);

        if (dataContainer.getOrDefault(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false)) {
            dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false);

            itemMeta.removeEnchant(DUMMY_ENCHANTMENT);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
    }
}
