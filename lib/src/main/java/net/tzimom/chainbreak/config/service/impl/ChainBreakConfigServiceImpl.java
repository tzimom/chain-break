package net.tzimom.chainbreak.config.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
import net.tzimom.chainbreak.config.service.ChainBreakConfigService;

public class ChainBreakConfigServiceImpl implements ChainBreakConfigService {
    private final Plugin plugin;

    private ChainBreakConfig config;

    public ChainBreakConfigServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    private ChainBreakConfig mapConfig(ConfigurationSection section) {
        var maxRange = section.getInt("max-range");
        var stepInterval = section.getInt("step-interval");

        var enchantmentConfig = mapEnchantmentConfig(section.getConfigurationSection("enchantment"));
        var toolConfigs = mapToolConfigs(section.getMapList("tools"));

        return new ChainBreakConfig(maxRange, stepInterval, enchantmentConfig, toolConfigs);
    }

    private ChainBreakEnchantmentConfig mapEnchantmentConfig(ConfigurationSection section) {
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        var dummyKey = NamespacedKey.fromString(section.getString("dummy"));
        var dummy = registry.get(dummyKey);

        var name = section.getString("name");

        return new ChainBreakEnchantmentConfig(dummy, name);
    }

    private Collection<ChainBreakToolConfig> mapToolConfigs(Collection<Map<?, ?>> section) {
        return section.stream()
                .map(this::mapToSection)
                .map(this::mapToolConfig)
                .toList();
    }

    private ConfigurationSection mapToSection(Map<?, ?> map) {
        var section = new MemoryConfiguration();

        for (var entry : map.entrySet()) {
            section.set(entry.getKey().toString(), entry.getValue());
        }

        return section;
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

    private Collection<Material> mapMaterialKey(String keyString, String registry) {
        if (!keyString.startsWith("#"))
            return List.of(Material.matchMaterial(keyString));

        var key = NamespacedKey.fromString(keyString.substring(1));
        var tag = Bukkit.getTag(registry, key, Material.class);

        return tag.getValues();
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
