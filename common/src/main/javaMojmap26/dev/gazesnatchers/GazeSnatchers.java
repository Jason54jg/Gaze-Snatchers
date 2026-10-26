package dev.gazesnatchers;

import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.registry.ModCreativeTabs;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.registry.ModMobEffects;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GazeSnatchers {
    public static final String MOD_ID = "gazesnatchers";
    public static final Logger LOGGER = LoggerFactory.getLogger("Gaze Snatchers");

    /** Fabric / loaders that allow {@code Registry.register} during mod init. */
    public static void init() {
        LOGGER.info("Initialisation de Gaze Snatchers…");
        initEarly();
        registerContent();
        LOGGER.info("Gaze Snatchers prêt (MC {}).", GazeSnatchersConstants.MINECRAFT_RANGE);
    }

    /**
     * NeoForge : config + hooks only. Content must be registered from {@code RegisterEvent}
     * via {@link #registerContent}.
     */
    public static void initEarly() {
        GazeSnatchersConfig.load();
        SleepWatcher.registerPlatformHooks();
        registerPlatformCommands();
    }

    public static void registerContent() {
        ModSounds.register();
        ModMobEffects.register();
        ModEntities.register();
        ModItems.register();
        ModItems.registerSpawnEggs();
        ModCreativeTabs.register();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static Runnable commandRegistrar = () -> {
    };

    public static void bindPlatformCommands(Runnable registrar) {
        commandRegistrar = registrar;
    }

    private static void registerPlatformCommands() {
        commandRegistrar.run();
    }

    private GazeSnatchers() {
    }
}
