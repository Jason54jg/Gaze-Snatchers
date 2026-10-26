package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.item.DescribedItem;
import dev.gazesnatchers.item.GazeBindingCharmItem;
import dev.gazesnatchers.item.GazeDraughtItem;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

public final class ModItems {
    public static Item GAZE_SHARD;
    public static Item GAZE_WARD_AMULET;
    public static Item GAZE_DRAUGHT;
    public static Item GAZE_BINDING_CHARM;
    public static Item GAZE_SNATCHER_SPAWN_EGG;
    public static Item BABY_GAZE_SNATCHER_SPAWN_EGG;

    public static void register() {
        register(null);
    }

    public static void register(BiConsumer<Identifier, Item> registrar) {
        GAZE_SHARD = register("gaze_shard", DescribedItem::new, new Item.Properties(), registrar);
        GAZE_WARD_AMULET = register("gaze_ward_amulet", DescribedItem::new, new Item.Properties().stacksTo(1), registrar);
        GAZE_DRAUGHT = register("gaze_draught", GazeDraughtItem::new, new Item.Properties(), registrar);
        GAZE_BINDING_CHARM = register(
                "gaze_binding_charm",
                GazeBindingCharmItem::new,
                new Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
                registrar);
    }

    /** Appelé après {@link ModEntities#register()} — format 26.x (composant entity_data + setId). */
    public static void registerSpawnEggs() {
        registerSpawnEggs(null);
    }

    public static void registerSpawnEggs(BiConsumer<Identifier, Item> registrar) {
        GAZE_SNATCHER_SPAWN_EGG = register("gaze_snatcher_spawn_egg",
                props -> spawnEgg(ModEntities.GAZE_SNATCHER, props),
                new Item.Properties(),
                registrar);
        BABY_GAZE_SNATCHER_SPAWN_EGG = register("baby_gaze_snatcher_spawn_egg",
                props -> spawnEgg(ModEntities.BABY_GAZE_SNATCHER, props),
                new Item.Properties(),
                registrar);
    }

    private static SpawnEggItem spawnEgg(EntityType<?> type, Item.Properties props) {
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        return new SpawnEggItem(props.component(
                DataComponents.ENTITY_DATA,
                TypedEntityData.of(type, entityTag)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> T register(
            String path,
            Function<Item.Properties, T> factory,
            Item.Properties props,
            BiConsumer<Identifier, Item> registrar) {
        Identifier id = GazeSnatchers.id(path);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        T item = factory.apply(props.setId(key));
        if (registrar == null) {
            return (T) net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, key, item);
        }
        registrar.accept(id, item);
        return item;
    }

    private ModItems() {
    }
}
