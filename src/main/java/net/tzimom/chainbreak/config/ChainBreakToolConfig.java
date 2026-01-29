package net.tzimom.chainbreak.config;

import java.util.Collection;

import org.bukkit.Material;

public record ChainBreakToolConfig(Collection<Material> items, Collection<Material> whitelist) {
}
