package net.tzimom.chainbreak.service.impl;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class ChainBreakEnchantmentServiceImpl implements ChainBreakEnchantmentService {
    private final ConfigService configService;

    private final NamespacedKey dummyEnchantmentKey;
    private final NamespacedKey enchantmentLevelKey;
    private final NamespacedKey loreLevelKey;

    public ChainBreakEnchantmentServiceImpl(Plugin plugin, ConfigService configService) {
        this.configService = configService;

        dummyEnchantmentKey = new NamespacedKey(plugin, "enchantment.dummy");
        enchantmentLevelKey = new NamespacedKey(plugin, "enchantment.chainbreak");
        loreLevelKey = new NamespacedKey(plugin, "enchantment.lore");
    }

    @Override
    public int getEnchantmentLevel(ItemStack item) {
        var dataContainer = item.getItemMeta().getPersistentDataContainer();
        return dataContainer.getOrDefault(enchantmentLevelKey, PersistentDataType.INTEGER, 0);
    }

    private Component createLoreLineComponent(int level) {
        var enchantmentConfig = configService.config().enchantment();

        var component = Component.text(enchantmentConfig.name())
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);

        if (Math.max(level, enchantmentConfig.levels().size()) <= 1)
            return component;

        return component
                .appendSpace()
                .append(Component.translatable("enchantment.level." + level)) ;
    }

    private String createLoreLine(int level) {
        var component = createLoreLineComponent(level);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    @Override
    public void enchant(ItemStack item, int level) {
        var itemMeta = item.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(enchantmentLevelKey, PersistentDataType.INTEGER, level);
        item.setItemMeta(itemMeta);

        updateItem(item);
    }

    @Override
    public void disenchant(ItemStack item) {
        enchant(item, 0);
    }

    @Override
    public void updateItem(ItemStack item) {
        var dummyEnchantment = configService.config().enchantment().dummy();

        var itemMeta = item.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();

        var loreLevel = dataContainer.getOrDefault(loreLevelKey, PersistentDataType.INTEGER, 0);
        var lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();

        if (loreLevel >= 1) {
            lore = lore.stream()
                    .filter(component -> !component.equals(createLoreLine(loreLevel)))
                    .collect(Collectors.toCollection(ArrayList::new));

            dataContainer.set(loreLevelKey, PersistentDataType.INTEGER, 0);
        }

        var hasDummy = dataContainer.getOrDefault(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false);

        if (hasDummy) {
            itemMeta.removeEnchant(dummyEnchantment);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

            dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, false);
        }

        var level = getEnchantmentLevel(item);

        if (level >= 1) {
            lore.add(createLoreLine(level));
            dataContainer.set(loreLevelKey, PersistentDataType.INTEGER, level);

            if (itemMeta.getEnchants().isEmpty() && !(itemMeta instanceof EnchantmentStorageMeta)) {
                itemMeta.addEnchant(dummyEnchantment, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                dataContainer.set(dummyEnchantmentKey, PersistentDataType.BOOLEAN, true);
            }
        }

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }
}
