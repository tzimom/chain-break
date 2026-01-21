package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class PrepareAnvilEventHandler implements Listener {
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    public PrepareAnvilEventHandler(ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;
    }

    @EventHandler
    public void handle(PrepareAnvilEvent event) {
        var view = event.getView();
        var inventory = event.getInventory();
        var firstItem = inventory.getFirstItem();
        var secondItem = inventory.getSecondItem();

        if (firstItem == null || secondItem == null)
            return;

        if (!chainBreakEnchantmentService.hasEnchantment(secondItem))
            return;

        var result = firstItem.clone();
        var resultType = result.getType();

        if (!chainBreakEnchantmentService.isEnchantable(resultType)) {
            event.setResult(null);
            return;
        }

        chainBreakEnchantmentService.enchant(result);

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
