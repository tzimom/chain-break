package net.tzimom.chainbreak.config;

import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public record LootConfig(Map<EntityType, Double> entities, Map<Structure, Double> structures) {
}
