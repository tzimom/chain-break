package net.tzimom.chainbreak.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class BlockBreakEventHandler implements Listener {
    private final ChainBreakService chainBreakService;
    private final ChainBreakEnchantmentService enchantmentService;
    private final ChainBreakToolService toolService;

    public BlockBreakEventHandler(
            ChainBreakService chainBreakService,
            ChainBreakEnchantmentService enchantmentService,
            ChainBreakToolService toolService) {
        this.chainBreakService = chainBreakService;
        this.enchantmentService = enchantmentService;
        this.toolService = toolService;
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        var player = event.getPlayer();
        var block = event.getBlock();
        var inventory = player.getInventory();
        var tool = inventory.getItemInMainHand();

        if (!toolService.canStartChainBreak(block, tool))
            return;

        var level = enchantmentService.getEnchantmentLevel(tool);
        chainBreakService.startChain(block, tool, player, level);
    }
}
