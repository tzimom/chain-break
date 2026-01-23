package net.tzimom.chainbreak.config;

import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public record LootConfig(
        Map<EntityType, Map<Integer, Double>> entities,
        Map<Structure, Map<Integer, Double>> structures) {
}
