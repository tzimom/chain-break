package net.tzimom.chainbreak.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.models.CustomEnchantment;
import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class PrepareAnvilEventHandler implements Listener {
    private final CustomEnchantmentService customEnchantmentService;

    public PrepareAnvilEventHandler(CustomEnchantmentService customEnchantmentService) {
        this.customEnchantmentService = customEnchantmentService;
    }

    @EventHandler
    public void handle(PrepareAnvilEvent event) {
        var view = event.getView();
        var inventory = event.getInventory();
        var firstItem = inventory.getFirstItem();
        var secondItem = inventory.getSecondItem();

        if (firstItem == null || secondItem == null)
            return;

        if (!customEnchantmentService.hasEnchantment(secondItem, CustomEnchantment.CHAIN_BREAK))
            return;

        var level = customEnchantmentService.getEnchantmentLevel(secondItem, CustomEnchantment.CHAIN_BREAK);
        var result = firstItem.clone();

        if (!customEnchantmentService.tryEnchant(result, CustomEnchantment.CHAIN_BREAK, level)) {
            event.setResult(null);
            return;
        }

        var renameText = view.getRenameText();

        if (renameText != null && !renameText.isEmpty()) {
            var itemMeta = result.getItemMeta();
            itemMeta.displayName(Component.text(view.getRenameText()));
            result.setItemMeta(itemMeta);
        }

        view.setRepairCost(0);
        event.setResult(result);
    }
}
