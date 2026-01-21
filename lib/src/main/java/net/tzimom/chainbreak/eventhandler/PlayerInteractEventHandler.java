package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class PlayerInteractEventHandler implements Listener {
    private final ChainBreakToolService chainBreakToolService;
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    public PlayerInteractEventHandler(ChainBreakToolService chainBreakToolService,
            ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakToolService = chainBreakToolService;
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        var tool = event.getItem();

        if (tool == null || tool.getType().isAir())
            return;

        if (!chainBreakEnchantmentService.hasEnchantment(tool))
            return;

        event.setCancelled(true);

        var enabled = chainBreakToolService.toggleChainBreak(tool);
        var player = event.getPlayer();

        player.sendActionBar(Component.text(enabled ? "Chain Break enabled" : "Chain Break disabled"));
    }
}
