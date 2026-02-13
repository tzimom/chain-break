package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.tzimom.chainbreak.service.ChainBreakService;

public class BlockBreakEventHandler implements Listener {
    private final ChainBreakService chainBreakService;

    public BlockBreakEventHandler(ChainBreakService chainBreakService) {
        this.chainBreakService = chainBreakService;
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        var player = event.getPlayer();
        var block = event.getBlock();
        var inventory = player.getInventory();
        var tool = inventory.getItemInMainHand();

        chainBreakService.tryStartChainBreak(player, block, tool);
    }
}
