package net.tzimom.chainbreak.service.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;
import net.tzimom.chainbreak.service.ChainBreakToolService;

public class ChainBreakToolServiceImpl implements ChainBreakToolService {
    private final ConfigService configService;
    private final ChainBreakEnchantmentService chainBreakEnchantmentService;

    private final NamespacedKey chainBreakEnabledKey;

    public ChainBreakToolServiceImpl(Plugin plugin, ConfigService configService,
            ChainBreakEnchantmentService chainBreakEnchantmentService) {
        this.configService = configService;
        this.chainBreakEnchantmentService = chainBreakEnchantmentService;

        chainBreakEnabledKey = new NamespacedKey(plugin, "enchantment.chainbreak.enabled");
    }

    private boolean isChainBreakEnabled(ItemStack tool) {
        if (tool == null || !tool.hasItemMeta())
            return false;

        var dataContainer = tool.getItemMeta().getPersistentDataContainer();

        return chainBreakEnchantmentService.hasEnchantment(tool)
                && dataContainer.getOrDefault(chainBreakEnabledKey, PersistentDataType.BOOLEAN, false);
    }

    private boolean isChainBreakCompatible(Material blockType, Material toolType) {
        var toolConfigs = configService.config().tools();

        return toolConfigs.stream()
                .filter(toolConfig -> toolConfig.items().contains(toolType))
                .anyMatch(toolConfig -> toolConfig.whitelist().contains(blockType));
    }

    @Override
    public boolean isTool(Material itemType) {
        return configService.config().tools().stream()
                .anyMatch(toolConfig -> toolConfig.items().contains(itemType));
    }

    @Override
    public boolean canStartChainBreak(Block block, ItemStack tool) {
        return isChainBreakEnabled(tool) && isChainBreakCompatible(block.getType(), tool.getType());
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
