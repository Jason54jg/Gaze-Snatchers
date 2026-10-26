package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.effect.GazeClarityMobEffect;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class ModMobEffects {
    public static Holder<MobEffect> GAZE_CLARITY;

    public static void register() {
        register(null);
    }

    public static void register(BiConsumer<Identifier, MobEffect> registrar) {
        Identifier id = GazeSnatchers.id("gaze_clarity");
        MobEffect effect = new GazeClarityMobEffect();
        if (registrar == null) {
            effect = net.minecraft.core.Registry.register(BuiltInRegistries.MOB_EFFECT, id, effect);
        } else {
            registrar.accept(id, effect);
        }
        GAZE_CLARITY = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    private ModMobEffects() {
    }
}
