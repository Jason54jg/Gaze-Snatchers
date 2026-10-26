package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    public static SoundEvent GAZE_SNATCHER_AMBIENT;
    public static SoundEvent GAZE_SNATCHER_SPAWN;
    public static SoundEvent GAZE_SNATCHER_STEAL;
    public static SoundEvent GAZE_SNATCHER_DEATH;
    public static SoundEvent BABY_GAZE_SNATCHER_AMBIENT;
    public static SoundEvent BABY_GAZE_SNATCHER_HURT;
    public static SoundEvent BABY_GAZE_SNATCHER_DEATH;

    public static void register() {
        GAZE_SNATCHER_AMBIENT = register("gaze_snatcher_ambient");
        GAZE_SNATCHER_SPAWN = register("gaze_snatcher_spawn");
        GAZE_SNATCHER_STEAL = register("gaze_snatcher_steal");
        GAZE_SNATCHER_DEATH = register("gaze_snatcher_death");
        BABY_GAZE_SNATCHER_AMBIENT = register("baby_gaze_snatcher_ambient");
        BABY_GAZE_SNATCHER_HURT = register("baby_gaze_snatcher_hurt");
        BABY_GAZE_SNATCHER_DEATH = register("baby_gaze_snatcher_death");
    }

    private static SoundEvent register(String path) {
        return RegistryHelper.registerSound(path);
    }

    private ModSounds() {
    }
}
