package net.tzimom.chainbreak.eventhandler;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.inventory.ItemStack;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class LootGenerateEventHandler implements Listener {
    private final ConfigService configService;
    private final ChainBreakEnchantmentService enchantmentService;

    public LootGenerateEventHandler(ConfigService configService, ChainBreakEnchantmentService enchantmentService) {
        this.configService = configService;
        this.enchantmentService = enchantmentService;
    }

    @EventHandler
    public void handle(LootGenerateEvent event) {
        var inventoryHolder = event.getInventoryHolder();

        if (!(inventoryHolder instanceof Container container))
            return;

        var loot = event.getLoot();
        var block = container.getBlock();
        var chunk = container.getChunk();

        var lootChance = configService.config().loot().structures().entrySet().stream()
                .filter(entry -> chunk.getStructures(entry.getKey()).stream()
                        .map(GeneratedStructure::getPieces)
                        .flatMap(Collection::stream)
                        .anyMatch(piece -> piece.getBoundingBox().contains(block.getBoundingBox())))
                .map(Entry::getValue).findAny().orElse(0d);

        var random = ThreadLocalRandom.current();

        if (random.nextDouble() > lootChance)
            return;

        var item = new ItemStack(Material.ENCHANTED_BOOK);

        enchantmentService.enchant(item);
        loot.add(item);
    }
}
