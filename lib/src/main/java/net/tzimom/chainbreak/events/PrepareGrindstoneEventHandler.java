package net.tzimom.chainbreak.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;

import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class PrepareGrindstoneEventHandler implements Listener {
    private final CustomEnchantmentService customEnchantmentService;

    public PrepareGrindstoneEventHandler(CustomEnchantmentService customEnchantmentService) {
		this.customEnchantmentService = customEnchantmentService;
	}

	@EventHandler
    public void handle(PrepareGrindstoneEvent event) {
        var result = event.getResult();

        if (result == null)
            return;

        customEnchantmentService.disenchant(result);
    }
}
