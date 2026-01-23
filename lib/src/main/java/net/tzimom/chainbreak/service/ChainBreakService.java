package net.tzimom.chainbreak.service;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakService {
    void startChain(Block block, ItemStack tool, LivingEntity user, int level);
}
