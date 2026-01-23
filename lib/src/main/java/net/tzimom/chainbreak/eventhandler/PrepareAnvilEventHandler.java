package net.tzimom.chainbreak.eventhandler;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class PrepareAnvilEventHandler implements Listener {
    private final ConfigService configService;
    private final ChainBreakEnchantmentService enchantmentService;
    private final ChainBreakToolService toolService;

    public PrepareAnvilEventHandler(ConfigService configService, ChainBreakEnchantmentService enchantmentService,
            ChainBreakToolService toolService) {
        this.configService = configService;
        this.enchantmentService = enchantmentService;
        this.toolService = toolService;
    }

    @EventHandler
    public void handle(PrepareAnvilEvent event) {
        var inventory = event.getInventory();
        var firstItem = inventory.getFirstItem();
        var secondItem = inventory.getSecondItem();

        if (firstItem == null || secondItem == null)
            return;

        var view = event.getView();
        var renameText = view.getRenameText();

        var firstItemType = firstItem.getType();
        var secondItemType = secondItem.getType();

        var firstLevel = enchantmentService.getEnchantmentLevel(firstItem);
        var secondLevel = enchantmentService.getEnchantmentLevel(secondItem);

        var firstIsTool = toolService.isTool(firstItemType);
        var secondIsTool = toolService.isTool(secondItemType);
        var firstIsBook = firstItemType == Material.ENCHANTED_BOOK;
        var secondIsBook = secondItemType == Material.ENCHANTED_BOOK;

        if (firstLevel < 1 && secondLevel < 1)
            return;

        if (firstIsBook && secondIsTool)
            return;

        if (firstIsTool && secondIsTool) {
            if (firstItemType != secondItemType)
                return;
        } else if (!secondIsBook || !(firstIsTool || firstIsBook))
            return;

        var result = cloneAndRename(firstItem, renameText);

        mergeDurabilites(result, secondItem);
        mergeEnchantments(result, secondItem);
        enchantmentService.updateItem(result);

        event.setResult(result);
        return;
    }

    private ItemStack cloneAndRename(ItemStack item, String renameText) {
        var result = item.clone();

        if (renameText == null || renameText.isEmpty())
            return result;

        var itemMeta = result.getItemMeta();

        itemMeta.displayName(Component.text(renameText));
        result.setItemMeta(itemMeta);

        return result;
    }

    private void mergeDurabilites(ItemStack result, ItemStack other) {
        var resultMeta = result.getItemMeta();
        var otherMeta = other.getItemMeta();

        if (!(result instanceof Damageable resultDamageable && otherMeta instanceof Damageable otherDamageable))
            return;

        resultDamageable.setDamage(Math.max(resultDamageable.getDamage() - otherDamageable.getDamage(), 0));
        result.setItemMeta(resultMeta);
    }

    private void mergeEnchantments(ItemStack result, ItemStack other) {
        var resultMeta = result.getItemMeta();

        if (other.getItemMeta() instanceof EnchantmentStorageMeta bookMeta)
            bookMeta.getStoredEnchants().forEach((enchantment, level) -> resultMeta.addEnchant(enchantment, level, false));
        else
            other.getEnchantments().forEach((enchantment, level) -> resultMeta.addEnchant(enchantment, level, false));

        result.setItemMeta(resultMeta);

        var firstLevel = enchantmentService.getEnchantmentLevel(result);
        var secondLevel = enchantmentService.getEnchantmentLevel(other);
        var combinedLevel = combineLevels(firstLevel, secondLevel);

        enchantmentService.enchant(result, combinedLevel);
    }

    private int combineLevels(int firstLevel, int secondLevel) {
        var maxLevel = configService.config().enchantment().levels().size();
        var combinedLevel = firstLevel == secondLevel ? firstLevel + 1 : Math.max(firstLevel, secondLevel);

        return Math.min(combinedLevel, maxLevel);
    }
}
