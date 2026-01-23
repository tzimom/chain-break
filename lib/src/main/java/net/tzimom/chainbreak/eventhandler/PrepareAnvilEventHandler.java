package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class PrepareAnvilEventHandler implements Listener {
    private final ChainBreakEnchantmentService enchantmentService;

    public PrepareAnvilEventHandler(ChainBreakEnchantmentService enchantmentService) {
        this.enchantmentService = enchantmentService;
    }

    @EventHandler
    public void handle(PrepareAnvilEvent event) {
        var view = event.getView();
        var inventory = event.getInventory();
        var firstItem = inventory.getFirstItem();
        var secondItem = inventory.getSecondItem();

        if (firstItem == null || secondItem == null)
            return;

        if (enchantmentService.hasEnchantment(firstItem)
                && !enchantmentService.hasEnchantment(secondItem)) {
            if (secondItem.getEnchantments().isEmpty()) {
                if (!(secondItem.getItemMeta() instanceof EnchantmentStorageMeta bookMeta))
                    return;

                if (bookMeta.getStoredEnchants().isEmpty())
                    return;
            }

            var result = event.getResult();

            if (result == null)
                return;

            result = result.clone();
            enchantmentService.clearDummyEnchantment(result);

            event.setResult(result);
            return;
        }

        if (!enchantmentService.hasEnchantment(secondItem))
            return;

        var result = firstItem.clone();
        var resultType = result.getType();

        if (!enchantmentService.isEnchantable(resultType)) {
            event.setResult(null);
            return;
        }

        enchantmentService.enchant(result);

        var renameText = view.getRenameText();

        if (renameText != null && !renameText.isEmpty()) {
            var itemMeta = result.getItemMeta();
            itemMeta.displayName(Component.text(renameText));
            result.setItemMeta(itemMeta);
        }

        view.setRepairCost(0);
        event.setResult(result);
    }
}
