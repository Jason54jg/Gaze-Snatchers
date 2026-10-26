package dev.gazesnatchers.registry;

import java.util.function.BiConsumer;
import net.minecraft.resources.Identifier;
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
        register(null);
    }

    /** NeoForge : passer {@code helper::register} depuis {@code RegisterEvent}. */
    public static void register(BiConsumer<Identifier, SoundEvent> registrar) {
        if (registrar == null) {
            GAZE_SNATCHER_AMBIENT = RegistryHelper.registerSound("gaze_snatcher_ambient");
            GAZE_SNATCHER_SPAWN = RegistryHelper.registerSound("gaze_snatcher_spawn");
            GAZE_SNATCHER_STEAL = RegistryHelper.registerSound("gaze_snatcher_steal");
            GAZE_SNATCHER_DEATH = RegistryHelper.registerSound("gaze_snatcher_death");
            BABY_GAZE_SNATCHER_AMBIENT = RegistryHelper.registerSound("baby_gaze_snatcher_ambient");
            BABY_GAZE_SNATCHER_HURT = RegistryHelper.registerSound("baby_gaze_snatcher_hurt");
            BABY_GAZE_SNATCHER_DEATH = RegistryHelper.registerSound("baby_gaze_snatcher_death");
            return;
        }
        GAZE_SNATCHER_AMBIENT = RegistryHelper.registerSound("gaze_snatcher_ambient", registrar);
        GAZE_SNATCHER_SPAWN = RegistryHelper.registerSound("gaze_snatcher_spawn", registrar);
        GAZE_SNATCHER_STEAL = RegistryHelper.registerSound("gaze_snatcher_steal", registrar);
        GAZE_SNATCHER_DEATH = RegistryHelper.registerSound("gaze_snatcher_death", registrar);
        BABY_GAZE_SNATCHER_AMBIENT = RegistryHelper.registerSound("baby_gaze_snatcher_ambient", registrar);
        BABY_GAZE_SNATCHER_HURT = RegistryHelper.registerSound("baby_gaze_snatcher_hurt", registrar);
        BABY_GAZE_SNATCHER_DEATH = RegistryHelper.registerSound("baby_gaze_snatcher_death", registrar);
    }

    private ModSounds() {
    }
}
