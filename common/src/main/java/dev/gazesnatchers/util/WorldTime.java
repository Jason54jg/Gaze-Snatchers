package dev.gazesnatchers.util;

import net.minecraft.world.level.Level;

/** Utilitaires jour/nuit (1.21.11 n'expose plus Level#isDay). */
public final class WorldTime {
    private WorldTime() {
    }

    public static boolean isDay(Level level) {
        long time = level.getDayTime() % 24000L;
        return time < 13000L;
    }

    public static boolean isNight(Level level) {
        return !isDay(level);
    }
}
