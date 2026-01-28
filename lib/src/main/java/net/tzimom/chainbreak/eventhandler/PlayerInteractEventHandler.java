package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.chat.ComponentSerializer;
import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class PlayerInteractEventHandler implements Listener {
    private final ConfigService configService;
    private final ChainBreakToolService toolService;
    private final ChainBreakEnchantmentService enchantmentService;

    public PlayerInteractEventHandler(ConfigService configService, ChainBreakToolService toolService,
            ChainBreakEnchantmentService enchantmentService) {
        this.configService = configService;
        this.toolService = toolService;
        this.enchantmentService = enchantmentService;
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        var tool = event.getItem();

        if (tool == null || tool.getType().isAir())
            return;

        if (!enchantmentService.hasEnchantment(tool) || !toolService.isTool(tool.getType()))
            return;

        var enabled = toolService.toggleChainBreak(tool);
        var player = event.getPlayer();

        var enchantmentName = configService.config().enchantment().name();
        var component = Component.text(enchantmentName)
                .append(Component.space())
                .append(Component.text(enabled ? "enabled" : "disabled"));
        var componentGson = GsonComponentSerializer.gson().serialize(component);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ComponentSerializer.parse(componentGson));
    }
}
