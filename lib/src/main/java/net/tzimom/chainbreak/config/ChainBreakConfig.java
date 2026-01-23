package net.tzimom.chainbreak.config;

import java.util.Collection;

public record ChainBreakConfig(int maxRange, int stepInterval, ChainBreakEnchantmentConfig enchantment,
        Collection<ChainBreakToolConfig> tools, LootConfig loot) {
}
