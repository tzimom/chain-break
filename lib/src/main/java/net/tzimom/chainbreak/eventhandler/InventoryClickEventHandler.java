package net.tzimom.chainbreak.eventhandler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class InventoryClickEventHandler implements Listener {
    private final Plugin plugin;
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    public InventoryClickEventHandler(Plugin plugin, ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.plugin = plugin;
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        var inventory = event.getInventory();

        if (!(inventory instanceof AnvilInventory anvilInventory))
            return;

        if (event.getSlotType() != SlotType.RESULT)
            return;

        var result = event.getCurrentItem();

        if (result == null || result.getType().isAir())
            return;

        var secondItem = anvilInventory.getSecondItem();

        if (secondItem == null)
            return;

        if (!chainBreakEnchantmentService.hasEnchantment(secondItem))
            return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> anvilInventory.setSecondItem(null), 1);
    }
}
