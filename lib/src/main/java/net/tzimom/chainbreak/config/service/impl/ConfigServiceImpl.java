package net.tzimom.chainbreak.config.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.Plugin;

import net.tzimom.chainbreak.config.ChainBreakConfig;
import net.tzimom.chainbreak.config.ChainBreakEnchantmentConfig;
import net.tzimom.chainbreak.config.ChainBreakEnchantmentLevelConfig;
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
        var enchantmentConfig = mapEnchantmentConfig(section.getConfigurationSection("enchantment"));
        var lootConfig = mapLootConfig(section.getConfigurationSection("loot"));

        var toolConfigs = section.getMapList("tools").stream()
                .map(this::sectionFromMap)
                .map(this::mapToolConfig)
                .toList();

        return new ChainBreakConfig(enchantmentConfig, toolConfigs, lootConfig);
    }

    private <T extends Keyed> T mapRegistryKeyString(Registry<T> registry, String keyString) {
        var key = NamespacedKey.fromString(keyString);
        return registry.get(key);
    }

    private ConfigurationSection sectionFromMap(Map<?, ?> map) {
        var section = new MemoryConfiguration();
        map.forEach((key, value) -> section.set(key.toString(), value));

        return section;
    }

    private ChainBreakEnchantmentConfig mapEnchantmentConfig(ConfigurationSection section) {
        var name = section.getString("name");
        var dummy = mapRegistryKeyString(Registry.ENCHANTMENT, section.getString("dummy"));
        var levels = section.getMapList("levels").stream()
                .map(this::sectionFromMap)
                .map(this::mapEnchantmentLevelConfig)
                .toList();

        return new ChainBreakEnchantmentConfig(name, dummy, levels);
    }

    private ChainBreakEnchantmentLevelConfig mapEnchantmentLevelConfig(ConfigurationSection section) {
        var maxRange = section.getInt("max-range");
        var stepInterval = section.getInt("step-interval");

        return new ChainBreakEnchantmentLevelConfig(maxRange, stepInterval);
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
        var entities = mapBoundedLootConfig(section.getConfigurationSection("entities"), Registry.ENTITY_TYPE);
        var structures = mapBoundedLootConfig(section.getConfigurationSection("structures"), Registry.STRUCTURE);

        return new LootConfig(entities, structures);
    }

    private <T extends Keyed> Map<T, Map<Integer, Double>> mapBoundedLootConfig(
            ConfigurationSection section,
            Registry<T> registryKey) {
        return section.getKeys(false).stream().collect(Collectors.toMap(
                keyString -> mapRegistryKeyString(registryKey, keyString),
                keyString -> section.getMapList(keyString).stream()
                        .map(this::sectionFromMap)
                        .collect(Collectors.toMap(
                                levelSection -> levelSection.getInt("level"),
                                levelSection -> levelSection.getDouble("chance")))));
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
