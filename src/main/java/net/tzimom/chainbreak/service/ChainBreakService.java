package net.tzimom.chainbreak.service;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakService {
    void tryStartChainBreak(Player player, Block root, ItemStack tool);
}
