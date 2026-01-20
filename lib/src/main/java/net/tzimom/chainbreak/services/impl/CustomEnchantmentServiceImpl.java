package net.tzimom.chainbreak.services.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.tzimom.chainbreak.models.CustomEnchantment;
import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class CustomEnchantmentServiceImpl implements CustomEnchantmentService {
    private final Plugin plugin;

    public CustomEnchantmentServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getEnchantmentLevel(ItemStack item, CustomEnchantment enchantment) {
        return item.getPersistentDataContainer()
                .getOrDefault(enchantment.getKey(plugin), PersistentDataType.INTEGER, 0);
    }

    @Override
    public boolean hasEnchantment(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantmentLevel(item, enchantment) > 0;
    }

    @Override
    public boolean tryEnchant(ItemStack item, CustomEnchantment enchantment, int level) {
        if (!canEnchant(item, enchantment, level))
            return false;

        enchant(item, enchantment, level);
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack item, CustomEnchantment enchantment, int level) {
        return enchantment.canEnchant(item.getType(), level)
                && getEnchantmentLevel(item, enchantment) < level;
    }

    private Component createLoreComponent(CustomEnchantment enchantment, int level) {
        var component = Component.text(enchantment.displayName());

        if (Math.max(enchantment.maxLevel(), level) > 1)
            component = component
                    .append(Component.space())
                    .append(Component.translatable("enchantment.level." + level));

        return component.color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void enchant(ItemStack item, CustomEnchantment enchantment, int level) {
        disenchant(item, enchantment);

        var itemMeta = item.getItemMeta();
        var lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<Component>();

        lore.addLast(createLoreComponent(enchantment, level));
        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantment.getKey(plugin), PersistentDataType.INTEGER, level);

        if (itemMeta.getEnchants().isEmpty()) {
            itemMeta.addEnchant(enchantment.dummyEnchantment(), 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
    }

    @Override
    public void disenchant(ItemStack item, CustomEnchantment enchantment) {
        if (!hasEnchantment(item, enchantment))
            return;

        var level = getEnchantmentLevel(item, enchantment);
        var itemMeta = item.getItemMeta();
        var lore = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<Component>();

        lore = lore.stream()
                .filter(component -> !component.equals(createLoreComponent(enchantment, level)))
                .toList();

        itemMeta.lore(lore);

        var dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(enchantment.getKey(plugin), PersistentDataType.INTEGER, 0);

        itemMeta.removeEnchant(enchantment.dummyEnchantment());

        if (!Arrays.stream(CustomEnchantment.values())
                .filter(other -> other != enchantment)
                .anyMatch(other -> hasEnchantment(item, other))) {
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
    }

	@Override
	public void disenchant(ItemStack item) {
        Arrays.stream(CustomEnchantment.values())
            .forEach(enchantment -> disenchant(item, enchantment));
	}
}
