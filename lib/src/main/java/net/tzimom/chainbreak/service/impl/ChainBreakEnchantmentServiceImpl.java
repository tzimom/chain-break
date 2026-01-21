package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tzimom.chainbreak.config.service.ChainBreakConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class ChainBreakEnchantmentServiceImpl implements ChainBreakEnchantmentService {
    private final ChainBreakConfigService chainBreakConfigService;

    private final NamespacedKey enchantmentKey;
    private final NamespacedKey dummyEnchantmentKey;

    public ChainBreakEnchantmentServiceImpl(Plugin plugin, ChainBreakConfigService chainBreakConfigService) {
        this.chainBreakConfigService = chainBreakConfigService;

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
        return chainBreakConfigService.config().tools().stream()
                .anyMatch(tool -> tool.items().contains(itemType));
    }

    private Component createLoreComponent() {
        return Component.text(chainBreakConfigService.config().enchantment().name())
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void enchant(ItemStack item) {
        disenchant(item);

        var itemMeta = item.getItemMeta();
        var lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<Component>();

        lore.addLast(createLoreComponent());
        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantmentKey, PersistentDataType.BOOLEAN, true);

        if (itemMeta.getEnchants().isEmpty()) {
            dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, true);

            itemMeta.addEnchant(chainBreakConfigService.config().enchantment().dummy(), 1, true);
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

        lore = lore.stream().filter(component -> !component.equals(createLoreComponent())).toList();
        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantmentKey, PersistentDataType.BOOLEAN, false);

        if (dataContainer.getOrDefault(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false)) {
            dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false);

            itemMeta.removeEnchant(chainBreakConfigService.config().enchantment().dummy());
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
    }
}
