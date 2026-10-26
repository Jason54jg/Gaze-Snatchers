package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.item.GazeDraughtItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

public final class ModItems {
    public static Item GAZE_SHARD;
    public static Item GAZE_WARD_AMULET;
    public static Item GAZE_DRAUGHT;
    public static Item GAZE_SNATCHER_SPAWN_EGG;
    public static Item BABY_GAZE_SNATCHER_SPAWN_EGG;

    public static void register() {
        GAZE_SHARD = register("gaze_shard", new Item(new Item.Properties()));
        GAZE_WARD_AMULET = register("gaze_ward_amulet", new Item(new Item.Properties().stacksTo(1)));
        GAZE_DRAUGHT = register("gaze_draught", new GazeDraughtItem(new Item.Properties()));
    }

    /** Appelé après {@link ModEntities#register()} — format 1.21.11 (composant entity_data). */
    public static void registerSpawnEggs() {
        GAZE_SNATCHER_SPAWN_EGG = register("gaze_snatcher_spawn_egg", spawnEgg(ModEntities.GAZE_SNATCHER));
        BABY_GAZE_SNATCHER_SPAWN_EGG = register("baby_gaze_snatcher_spawn_egg", spawnEgg(ModEntities.BABY_GAZE_SNATCHER));
    }

    private static SpawnEggItem spawnEgg(EntityType<?> type) {
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        return new SpawnEggItem(new Item.Properties().component(
                DataComponents.ENTITY_DATA,
                TypedEntityData.of(type, entityTag)));
    }

    private static <T extends Item> T register(String path, T item) {
        return (T) net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, GazeSnatchers.id(path), item);
    }

    private ModItems() {
    }
}
