package net.tzimom.chainbreak.config;

import java.util.List;

import org.bukkit.enchantments.Enchantment;

public record ChainBreakEnchantmentConfig(String name, Enchantment dummy,
        List<ChainBreakEnchantmentLevelConfig> levels) {
}
