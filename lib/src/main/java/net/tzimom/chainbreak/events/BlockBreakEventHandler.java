package net.tzimom.chainbreak.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.tzimom.chainbreak.models.CustomEnchantment;
import net.tzimom.chainbreak.services.ChainBreakService;
import net.tzimom.chainbreak.services.CustomEnchantmentService;

public class BlockBreakEventHandler implements Listener {
    private final CustomEnchantmentService customEnchantmentService;
    private final ChainBreakService chainBreakService;

    public BlockBreakEventHandler(CustomEnchantmentService customEnchantmentService,
            ChainBreakService chainBreakService) {
        this.customEnchantmentService = customEnchantmentService;
        this.chainBreakService = chainBreakService;
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var item = inventory.getItemInMainHand();

        if (!customEnchantmentService.hasEnchantment(item, CustomEnchantment.CHAIN_BREAK))
            return;

        var block = event.getBlock();

        chainBreakService.tryStartChain(block, item, player);
    }
}
