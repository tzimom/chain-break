package net.tzimom.chainbreak.services;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface ChainBreakService {
    void tryStartChain(Block block, ItemStack tool, LivingEntity user);
}
