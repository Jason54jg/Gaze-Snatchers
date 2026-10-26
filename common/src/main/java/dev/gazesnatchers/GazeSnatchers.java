package dev.gazesnatchers;

import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.registry.ModCreativeTabs;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GazeSnatchers {
    public static final String MOD_ID = "gazesnatchers";
    public static final Logger LOGGER = LoggerFactory.getLogger("Gaze Snatchers");

    public static void init() {
        LOGGER.info("Initialisation de Gaze Snatchers…");
        GazeSnatchersConfig.load();
        ModSounds.register();
        ModEntities.register();
        ModItems.register();
        ModCreativeTabs.register();

        SleepWatcher.registerPlatformHooks();
        LOGGER.info("Gaze Snatchers prêt (MC {}).", GazeSnatchersConstants.MINECRAFT_RANGE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private GazeSnatchers() {
    }
}
