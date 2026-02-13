package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;

import net.tzimom.chainbreak.service.EnchantmentService;

public class PrepareGrindstoneEventHandler implements Listener {
    private final EnchantmentService enchantmentService;

    public PrepareGrindstoneEventHandler(EnchantmentService enchantmentService) {
		this.enchantmentService = enchantmentService;
	}

	@EventHandler
    public void handle(PrepareGrindstoneEvent event) {
        var result = event.getResult();

        if (result == null)
            return;

        enchantmentService.disenchant(result);
    }
}
