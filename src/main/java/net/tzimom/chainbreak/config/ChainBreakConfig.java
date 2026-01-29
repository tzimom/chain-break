package net.tzimom.chainbreak.config;

import java.util.Collection;

import org.bukkit.Sound;

public record ChainBreakConfig(ChainBreakEnchantmentConfig enchantment,
        Collection<ChainBreakToolConfig> tools, LootConfig loot, Collection<RecipeConfig> recipes,
        Sound toggleOnSound, Sound toggleOffSound) {
}
