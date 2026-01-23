package net.tzimom.chainbreak.eventhandler;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.tzimom.chainbreak.config.service.ConfigService;
import net.tzimom.chainbreak.service.ChainBreakEnchantmentService;

public class EntityDeathEventHandler implements Listener {
    private final ConfigService configService;
    private final ChainBreakEnchantmentService enchantmentService;

    public EntityDeathEventHandler(ConfigService configService,
            ChainBreakEnchantmentService enchantmentService) {
        this.configService = configService;
        this.enchantmentService = enchantmentService;
    }

    @EventHandler
    public void handle(EntityDeathEvent event) {
        var drops = event.getDrops();
        var entityType = event.getEntityType();
        var lootChance = configService.config().loot().entities().getOrDefault(entityType, 0d);
        var random = ThreadLocalRandom.current();

        if (random.nextDouble() > lootChance)
            return;

        var item = new ItemStack(Material.ENCHANTED_BOOK);

        enchantmentService.enchant(item);
        drops.add(item);
    }
}
