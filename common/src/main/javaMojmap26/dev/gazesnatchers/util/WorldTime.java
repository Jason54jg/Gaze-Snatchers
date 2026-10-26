package dev.gazesnatchers.util;

import net.minecraft.world.level.Level;

/** Utilitaires jour/nuit (26.x : Level#getOverworldClockTime). */
public final class WorldTime {
    private WorldTime() {
    }

    public static boolean isDay(Level level) {
        long time = level.getOverworldClockTime() % 24000L;
        return time < 13000L;
    }

    public static boolean isNight(Level level) {
        return !isDay(level);
    }
}
