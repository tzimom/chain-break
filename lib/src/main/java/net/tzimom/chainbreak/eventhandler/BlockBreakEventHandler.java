package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class BlockBreakEventHandler implements Listener {
    private final ChainBreakService chainBreakService;
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    public BlockBreakEventHandler(ChainBreakService chainBreakService,
            ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakService = chainBreakService;
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var item = inventory.getItemInMainHand();

        if (!chainBreakEnchantmentService.hasEnchantment(item))
            return;

        var block = event.getBlock();

        chainBreakService.tryStartChain(block, item, player);
    }
}
