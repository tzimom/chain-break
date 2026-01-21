package net.tzimom.chainbreak.service.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ChainBreakConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class ChainBreakToolServiceImpl implements ChainBreakToolService {
    private final ChainBreakConfigService chainBreakConfigService;
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    private final NamespacedKey chainBreakEnabledKey;

    public ChainBreakToolServiceImpl(Plugin plugin, ChainBreakConfigService chainBreakConfigService,
            ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.chainBreakConfigService = chainBreakConfigService;
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;

        chainBreakEnabledKey = new NamespacedKey(plugin, "enchantment.chainbreak.enabled");
    }

    private boolean isChainBreakEnabled(ItemStack tool) {
        var dataContainer = tool.getPersistentDataContainer();

        return chainBreakEnchantmentService.hasEnchantment(tool)
                && dataContainer.getOrDefault(chainBreakEnabledKey, PersistentDataType.BOOLEAN, false);
    }

    private boolean isChainBreakCompatible(Block block, ItemStack tool) {
        var toolConfigs = chainBreakConfigService.config().tools();

        return block.isPreferredTool(tool) && toolConfigs.stream()
                .filter(toolConfig -> toolConfig.items().contains(tool.getType()))
                .anyMatch(toolConfig -> toolConfig.whitelist().contains(block.getType()));
    }

    @Override
    public boolean canStartChainBreak(Block block, ItemStack tool) {
        return isChainBreakEnabled(tool) && isChainBreakCompatible(block, tool);
    }

    @Override
    public boolean toggleChainBreak(ItemStack tool) {
        var enabled = isChainBreakEnabled(tool);

        var itemMeta = tool.getItemMeta();
        var container = itemMeta.getPersistentDataContainer();

        container.set(chainBreakEnabledKey, PersistentDataType.BOOLEAN, !enabled);
        tool.setItemMeta(itemMeta);

        return !enabled;
    }
}
