package net.tzimom.chainbreak.config;

import java.util.Collection;

public record ChainBreakConfig(ChainBreakEnchantmentConfig enchantment,
        Collection<ChainBreakToolConfig> tools, LootConfig loot, Collection<RecipeConfig> recipes) {
}
