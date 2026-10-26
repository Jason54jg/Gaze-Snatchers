package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

public final class ModEntities {
    public static EntityType<GazeSnatcherEntity> GAZE_SNATCHER;
    public static EntityType<BabyGazeSnatcherEntity> BABY_GAZE_SNATCHER;

    public static void register() {
        GAZE_SNATCHER = register(
                "gaze_snatcher",
                EntityType.Builder.of(GazeSnatcherEntity::new, MobCategory.MONSTER)
                        .sized(0.7F, 2.75F)
                        .eyeHeight(2.45F)
                        .clientTrackingRange(10)
                        .updateInterval(3));

        BABY_GAZE_SNATCHER = register(
                "baby_gaze_snatcher",
                EntityType.Builder.of(BabyGazeSnatcherEntity::new, MobCategory.MONSTER)
                        .sized(0.55F, 0.55F)
                        .eyeHeight(0.45F)
                        .clientTrackingRange(8)
                        .updateInterval(2));
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        Identifier id = GazeSnatchers.id(path);
        ResourceKey<EntityType<?>> key = ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, id);
        return net.minecraft.core.Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(key));
    }

    public static AttributeSupplier.Builder gazeSnatcherAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    public static AttributeSupplier.Builder babyGazeSnatcherAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    private ModEntities() {
    }
}
