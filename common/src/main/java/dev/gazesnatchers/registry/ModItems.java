package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static Item GAZE_SHARD;
    public static Item GAZE_WARD_AMULET;

    public static void register() {
        GAZE_SHARD = register("gaze_shard", new Item(new Item.Properties()));
        GAZE_WARD_AMULET = register("gaze_ward_amulet", new Item(new Item.Properties().stacksTo(1)));
    }

    private static Item register(String path, Item item) {
        return net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, GazeSnatchers.id(path), item);
    }

    private ModItems() {
    }
}
