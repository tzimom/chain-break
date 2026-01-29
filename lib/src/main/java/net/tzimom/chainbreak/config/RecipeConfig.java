package net.tzimom.chainbreak.config;

import java.util.Map;

import org.bukkit.Material;

public record RecipeConfig(String[] shape, Map<Character, Material> ingredients, RecipeResultConfig result) {
}
