package net.tzimom.chainbreak.config.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.tzimom.chainbreak.config.ChainBreakConfig;
import net.tzimom.chainbreak.config.ChainBreakEnchantmentConfig;
import net.tzimom.chainbreak.config.ChainBreakToolConfig;
import net.tzimom.chainbreak.config.LootConfig;
import net.tzimom.chainbreak.config.service.ConfigService;

public class ConfigServiceImpl implements ConfigService {
    private final Plugin plugin;

    private ChainBreakConfig config;

    public ConfigServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    private ChainBreakConfig mapConfig(ConfigurationSection section) {
        var maxRange = section.getInt("max-range");
        var stepInterval = section.getInt("step-interval");

        var enchantmentConfig = mapEnchantmentConfig(section.getConfigurationSection("enchantment"));
        var toolConfigs = mapToolConfigs(section.getMapList("tools"));
        var lootConfig = mapLootConfig(section.getConfigurationSection("loot"));

        return new ChainBreakConfig(maxRange, stepInterval, enchantmentConfig, toolConfigs, lootConfig);
    }

    private <T extends Keyed> T mapRegistryKeyString(RegistryKey<T> registryKey, String keyString) {
        var registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        var key = NamespacedKey.fromString(keyString);

        return registry.get(key);
    }

    private ConfigurationSection mapToSection(Map<?, ?> map) {
        var section = new MemoryConfiguration();
        map.forEach((key, value) -> section.set(key.toString(), value));

        return section;
    }

    private ChainBreakEnchantmentConfig mapEnchantmentConfig(ConfigurationSection section) {
        var dummy = mapRegistryKeyString(RegistryKey.ENCHANTMENT, section.getString("dummy"));
        var name = section.getString("name");

        return new ChainBreakEnchantmentConfig(dummy, name);
    }

    private Collection<ChainBreakToolConfig> mapToolConfigs(Collection<Map<?, ?>> section) {
        return section.stream()
                .map(this::mapToSection)
                .map(this::mapToolConfig)
                .toList();
    }

    private ChainBreakToolConfig mapToolConfig(ConfigurationSection section) {
        var items = mapMaterials(section.getStringList("items"), Tag.REGISTRY_ITEMS);
        var whitelist = mapMaterials(section.getStringList("whitelist"), Tag.REGISTRY_BLOCKS);

        return new ChainBreakToolConfig(items, whitelist);
    }

    private Collection<Material> mapMaterials(List<String> keyStrings, String registry) {
        return keyStrings.stream()
                .map(keyString -> mapMaterialKey(keyString, registry))
                .flatMap(Collection::stream)
                .toList();
    }

    private Collection<Material> mapMaterialKey(String keyString, String tagRegistry) {
        if (!keyString.startsWith("#"))
            return List.of(Material.matchMaterial(keyString));

        var key = NamespacedKey.fromString(keyString.substring(1));
        var tag = Bukkit.getTag(tagRegistry, key, Material.class);

        return tag.getValues();
    }

    private LootConfig mapLootConfig(ConfigurationSection section) {
        var entities = mapBoundedLootConfig(section.getConfigurationSection("entities"), RegistryKey.ENTITY_TYPE);
        var structures = mapBoundedLootConfig(section.getConfigurationSection("structures"),
                RegistryKey.STRUCTURE);

        return new LootConfig(entities, structures);
    }

    private <T extends Keyed> Map<T, Double> mapBoundedLootConfig(
            ConfigurationSection section,
            RegistryKey<T> registryKey) {
        return section.getKeys(false).stream()
                .collect(Collectors.toMap(
                        keyString -> mapRegistryKeyString(registryKey, keyString),
                        section::getDouble));
    }

    @Override
    public void reload() {
        plugin.reloadConfig();

        var yamlConfig = plugin.getConfig();
        config = mapConfig(yamlConfig);
    }

    @Override
    public ChainBreakConfig config() {
        return config;
    }
}
