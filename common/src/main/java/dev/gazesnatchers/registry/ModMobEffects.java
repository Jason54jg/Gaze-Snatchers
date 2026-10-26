package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.effect.GazeClarityMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public final class ModMobEffects {
    public static Holder<MobEffect> GAZE_CLARITY;

    public static void register() {
        MobEffect effect = net.minecraft.core.Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                GazeSnatchers.id("gaze_clarity"),
                new GazeClarityMobEffect());
        GAZE_CLARITY = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    private ModMobEffects() {
    }
}
