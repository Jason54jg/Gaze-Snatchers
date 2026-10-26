package dev.gazesnatchers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.gazesnatchers.GazeSnatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration JSON partagée (multi-joueur : valeurs lues côté serveur).
 */
public final class GazeSnatchersConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;
    private static Data data = new Data();

    public static void load() {
        configPath = java.nio.file.Path.of("config", GazeSnatchers.MOD_ID + ".json");
        if (Files.exists(configPath)) {
            try {
                data = GSON.fromJson(Files.readString(configPath), Data.class);
                if (data == null) {
                    data = new Data();
                }
            } catch (IOException exception) {
                GazeSnatchers.LOGGER.error("Impossible de lire la config, défauts utilisés.", exception);
                data = new Data();
            }
        } else {
            save();
        }
    }

    public static void save() {
        if (configPath == null) {
            configPath = java.nio.file.Path.of("config", GazeSnatchers.MOD_ID + ".json");
        }
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(data));
        } catch (IOException exception) {
            GazeSnatchers.LOGGER.error("Impossible d'écrire la config.", exception);
        }
    }

    public static double spawnChance() {
        return data.spawnChance;
    }

    public static int ticksBeforeSteal() {
        return data.ticksBeforeSteal;
    }

    public static int minBabies() {
        return data.minBabiesOnSplit;
    }

    public static int maxBabies() {
        return data.maxBabiesOnSplit;
    }

    public static float amuletStealReduction() {
        return data.amuletStealReduction;
    }

    public static double rareStolenItemDropChance() {
        return data.rareStolenItemDropChance;
    }

    public static boolean despawnAfterSteal() {
        return data.despawnAfterSteal;
    }

    public static boolean alwaysDropStolenFromCarrier() {
        return data.alwaysDropStolenFromCarrier;
    }

    public static int stareDistance() {
        return data.stareDistance;
    }

    /** Valeurs par défaut — modifiables dans config/gazesnatchers.json */
    public static final class Data {
        public double spawnChance = 0.72;
        /** ~8 s à 20 TPS sans attention du joueur avant vol. */
        public int ticksBeforeSteal = 160;
        public int minBabiesOnSplit = 2;
        public int maxBabiesOnSplit = 4;
        /** Réduction multiplicative du « progrès de vol » si l'amulette est portée. */
        public float amuletStealReduction = 0.55F;
        /** Chance qu'un bébé droppe un item « marqué volé » (loot bonus). */
        public double rareStolenItemDropChance = 0.04;
        public boolean despawnAfterSteal = true;
        /** Si true, le bébé porteur droppe toujours l'item volé (sinon : rareStolenItemDropChance). */
        public boolean alwaysDropStolenFromCarrier = false;
        /** Distance max pour rester devant le joueur endormi. */
        public int stareDistance = 12;
    }

    private GazeSnatchersConfig() {
    }
}
