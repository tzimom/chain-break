package net.tzimom.chainbreak.service.impl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.EnchantmentService;
import net.tzimom.chainbreak.service.ToolService;

public class ToolServiceImpl implements ToolService {
    private final ConfigService configService;
    private final EnchantmentService enchantmentService;

    private final NamespacedKey chainBreakActiveKey;

    public ToolServiceImpl(Plugin plugin, ConfigService configService,
            EnchantmentService enchantmentService) {
        this.configService = configService;
        this.enchantmentService = enchantmentService;

        chainBreakActiveKey = new NamespacedKey(plugin, "tool.active");
    }

    @Override
    public boolean isTool(Material itemType) {
        return configService.config().tools().stream()
                .anyMatch(toolConfig -> toolConfig.items().contains(itemType));
    }

    @Override
    public boolean isActive(ItemStack tool) {
        if (tool == null || !tool.hasItemMeta())
            return false;

        var dataContainer = tool.getItemMeta().getPersistentDataContainer();

        return enchantmentService.hasEnchantment(tool)
                && dataContainer.getOrDefault(chainBreakActiveKey, PersistentDataType.BOOLEAN, false);
    }

    @Override
    public boolean isCompatible(Material toolType, Material blockType) {
        var toolConfigs = configService.config().tools();

        return toolConfigs.stream()
                .filter(toolConfig -> toolConfig.items().contains(toolType))
                .anyMatch(toolConfig -> toolConfig.whitelist().contains(blockType));
    }

    @Override
    public boolean toggleChainBreak(ItemStack tool) {
        var enabled = isActive(tool);

        var itemMeta = tool.getItemMeta();
        var container = itemMeta.getPersistentDataContainer();

        container.set(chainBreakActiveKey, PersistentDataType.BOOLEAN, !enabled);
        tool.setItemMeta(itemMeta);

        return !enabled;
    }
}
