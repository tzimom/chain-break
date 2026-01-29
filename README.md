# Chain Break

**Chain Break** is a Minecraft ([Spigot](https://www.spigotmc.org/)) plugin that enables players to enchant their tools with a custom enchantment which allows you to break a single block to trigger a chain reaction that destroys all adjacent blocks of the same type within some distance.

## Usage

The chain break enchantment behaves much like a regular minecraft enchantment, meaning you can use an anvil to enchant tools with chain break enchantment books, you can combine chain break enchantment books to higher levels and you can easily disenchant the tools in the grindstone. Additionally, by right clicking the air with a chain break tool in your hand, you can toggle it on or off.

## Configuration

Once the plugin is installed and has been loaded once by the server, the default configuration will be saved as `plugins/chainbreak/config.yml`. This default configuration looks like this:

```yaml
enchantment:
  name: "Chain Break"
  dummy: "minecraft:lunge"

  levels:
    - max_range: 3
      step_interval: 10

    - max_range: 6
      step_interval: 6

    - max_range: 9
      step_interval: 4

tools:
  - items: ["#minecraft:pickaxes"]
    whitelist:
      - "#minecraft:coal_ores"
      - "#minecraft:copper_ores"
      - "#minecraft:diamond_ores"
      - "#minecraft:emerald_ores"
      - "#minecraft:gold_ores"
      - "#minecraft:iron_ores"
      - "#minecraft:lapis_ores"
      - "#minecraft:redstone_ores"
      - "minecraft:nether_quartz_ore"
      - "minecraft:ancient_debris"

  - items: ["#minecraft:axes"]
    whitelist: ["#minecraft:logs"]

  - items: ["#minecraft:hoes"]
    whitelist: ["#minecraft:leaves", "#minecraft:wart_blocks"]

  - items: ["#minecraft:shovels"]
    whitelist:
      - "#minecraft:dirt"
      - "#minecraft:sand"
      - "minecraft:gravel"
      - "minecraft:soul_sand"
      - "minecraft:soul_soil"

loot:
  entities: {}
  structures: {}

recipes:
  - shape: ["aba", "cdc", "aea"]
    ingredients:
      a: "minecraft:crying_obsidian"
      b: "minecraft:diamond"
      c: "minecraft:echo_shard"
      d: "minecraft:book"
      e: "minecraft:creaking_heart"

    result:
      material: "minecraft:enchanted_book"
      chain_break_level: 1

toggle_on_sound: "minecraft:block.beacon.activate"
toggle_off_sound: "minecraft:block.beacon.deactivate"
```

In the `enchantment` section, you can configure how the chain break enchantment itself behaves. The field `name` specifies the name of the enchantment you will see in game, the field `dummy` tells the plugin, which enchantment it should enchant previously non-enchanted items with to give the tool the glowing enchantment effect. This is neccessary, since the chain break enchantment is not a real enchantment but rather a combination of persistent data and custom lore entry. The `levels` field takes a list of level configurations. The number of entries in this list determines the maximum level. For each level you have to specify the maximum range (`max_range`), meaning the maximum distance a block can have from the origin of a chain break reaction to still destroyed, as well as the `step_interval`, meaning the amount of ticks it takes to break one layer of blocks.

The `tools` section configures, which tools are enchantable with chain break and which blocks they can break. This should be pretty straight forward.

The `loot` section is empty by default. This is where you can decide how players will be able to obtain the chain break enchantment book naturally. The `entities` section controls, which entities drop the enchantment book, where as the `structures` section controls the world structures in which chests will have a chance of containing the enchantment book. Use this example for reference:

```yaml
loot:
  entities:
    "minecraft:warden":
      - level: 1
        chance: 0.7
      - level: 2
        chance: 0.2
      - level: 3
        chance: 0.1

    "minecraft:armadillo":
      - level: 1
        chance: 0.05
      - level: 2
        chance: 0.01
      - level: 3
        chance: 0.0025

    "minecraft:wandering_trader":
      - level: 1
        chance: 0.25

  structures:
    "minecraft:mansion":
      - level: 1
        chance: 0.1

    "minecraft:buried_treasure":
      - level: 1
        chance: 0.1

    "minecraft:ancient_city":
      - level: 1
        chance: 0.05

    "minecraft:igloo":
      - level: 2
        chance: 0.25
      - level: 3
        chance: 0.1
```

The `recipes` section configures the ways you can craft chain break items. Note that you can also change the amount of result items by setting the entry `result.amount`. If absent, this will default to `1`.

The fields `toggle_on_sound` and `toggle_off_sound` control which sound the player will hear when activating or deactivating their chain break tools.
