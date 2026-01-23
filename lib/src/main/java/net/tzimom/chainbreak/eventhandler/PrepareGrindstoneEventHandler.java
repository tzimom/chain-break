package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;

import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class PrepareGrindstoneEventHandler implements Listener {
    private final ChainBreakEnchantmentService enchantmentService;

    public PrepareGrindstoneEventHandler(ChainBreakEnchantmentService enchantmentService) {
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
