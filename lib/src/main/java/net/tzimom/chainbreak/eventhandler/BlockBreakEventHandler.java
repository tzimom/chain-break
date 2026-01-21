package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class BlockBreakEventHandler implements Listener {
    private final ChainBreakService chainBreakService;
    private final ChainBreakToolService chainBreakToolService;

    public BlockBreakEventHandler(ChainBreakService chainBreakService,
            ChainBreakToolService chainBreakToolService) {
        this.chainBreakService = chainBreakService;
        this.chainBreakToolService = chainBreakToolService;
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        var player = event.getPlayer();
        var block = event.getBlock();
        var inventory = player.getInventory();
        var tool = inventory.getItemInMainHand();

        if (!chainBreakToolService.canStartChainBreak(block, tool))
            return;

        chainBreakService.startChain(block, tool, player);
    }
}
