package net.tzimom.chainbreak.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
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

    private boolean isChainBreakEnabled(ItemStack tool) {
        var dataContainer = tool.getPersistentDataContainer();

        return chainBreakEnchantmentService.hasEnchantment(tool)
                && dataContainer.getOrDefault(chainBreakEnabledKey, PersistentDataType.BOOLEAN, false);
    }

    private boolean isChainBreakCompatible(Material blockType) {
        return Stream.of(Tag.LOGS, Tag.LEAVES, Tag.COAL_ORES, Tag.COPPER_ORES, Tag.DIAMOND_ORES, Tag.EMERALD_ORES,
                Tag.GOLD_ORES, Tag.IRON_ORES, Tag.LAPIS_ORES, Tag.REDSTONE_ORES)
                .anyMatch(tag -> tag.isTagged(blockType))
                || blockType == Material.NETHER_QUARTZ_ORE || blockType == Material.ANCIENT_DEBRIS;
    }

    @Override
    public boolean canStartChainBreak(Block block, ItemStack tool) {
        return isChainBreakEnabled(tool) && block.isPreferredTool(tool) && isChainBreakCompatible(block.getType());
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
