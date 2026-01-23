package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.Component;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class PlayerInteractEventHandler implements Listener {
    private final ConfigService configService;
    private final ChainBreakToolService chainBreakToolService;
    private final ChainBreakEnchantmentService enchantmentService;

    public PlayerInteractEventHandler(ConfigService configService, ChainBreakToolService chainBreakToolService,
            ChainBreakEnchantmentService enchantmentService) {
        this.configService = configService;
        this.chainBreakToolService = chainBreakToolService;
        this.enchantmentService = enchantmentService;
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        var tool = event.getItem();

        if (tool == null || tool.getType().isAir())
            return;

        if (!enchantmentService.hasEnchantment(tool) || !chainBreakToolService.isTool(tool.getType()))
            return;

        var enabled = chainBreakToolService.toggleChainBreak(tool);
        var player = event.getPlayer();

        var enchantmentName = configService.config().enchantment().name();

        player.sendActionBar(Component.text(enchantmentName)
                .append(Component.space())
                .append(Component.text(enabled ? "enabled" : "disabled")));
    }
}
