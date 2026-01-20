package net.tzimom.chainbreak.models;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

public enum CustomEnchantment {
    CHAIN_BREAK("chainbreak", "Chainbreak", Enchantment.LUNGE, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE,
            Material.NETHERITE_HOE);

    private final String key;
    private final String displayName;
    private final int maxLevel;
    private final Enchantment dummyEnchantment;
    private final Collection<Material> allowedTools;

    CustomEnchantment(String key, String displayName, int maxLevel, Enchantment dummyEnchantment,
            Collection<Material> allowedTools) {
        this.key = key;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.dummyEnchantment = dummyEnchantment;
        this.allowedTools = allowedTools;
    }

    CustomEnchantment(String key, String displayName, Enchantment dummyEnchantment, Collection<Material> allowedTools) {
        this(key, displayName, 1, dummyEnchantment, allowedTools);
    }

    CustomEnchantment(String key, String displayName, int maxLevel, Enchantment dummyEnchantment,
            Material... allowedTools) {
        this(key, displayName, maxLevel, dummyEnchantment, Set.of(allowedTools));
    }

    CustomEnchantment(String key, String displayName, Enchantment dummyEnchantment, Material... allowedTools) {
        this(key, displayName, 1, dummyEnchantment, allowedTools);
    }

    public NamespacedKey getKey(Plugin plugin) {
        return new NamespacedKey(plugin, "enchantment." + key);
    }

    public String displayName() {
        return displayName;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public Enchantment dummyEnchantment() {
        return dummyEnchantment;
    }

    public boolean canEnchant(Material tool) {
        return allowedTools.contains(tool);
    }

    public boolean canEnchant(int level) {
        return level <= maxLevel;
    }

    public boolean canEnchant(Material tool, int level) {
        return canEnchant(tool) && canEnchant(level);
    }
}
