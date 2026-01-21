package net.tzimom.chainbreak.service.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class ChainBreakToolServiceImpl implements ChainBreakToolService {
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    private final NamespacedKey chainBreakEnabledKey;

    public ChainBreakToolServiceImpl(Plugin plugin, ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;

        chainBreakEnabledKey = new NamespacedKey(plugin, "enchantment.chainbreak.enabled");
    }

    @Override
    public boolean canStartChainBreak(Block block, ItemStack tool) {
        if (!chainBreakEnchantmentService.hasEnchantment(tool))
            return false;

        if (!block.isPreferredTool(tool))
            return false;

        var dataContainer = tool.getPersistentDataContainer();
        return dataContainer.getOrDefault(chainBreakEnabledKey, PersistentDataType.BOOLEAN, false);
    }

	@Override
	public boolean toggleChainBreak(ItemStack tool) {
        var itemMeta = tool.getItemMeta();
        var container = itemMeta.getPersistentDataContainer();

        var enabled = container.getOrDefault(chainBreakEnabledKey, PersistentDataType.BOOLEAN, false);
        container.set(chainBreakEnabledKey, PersistentDataType.BOOLEAN, !enabled);

        tool.setItemMeta(itemMeta);
        return !enabled;
	}
}
