package dev.gazesnatchers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.sleep.SleepWatcher;
import dev.gazesnatchers.util.GroundSpawn;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

/**
 * Commandes opérateur pour tests (spawn, simulation sommeil, reload config).
 */
public final class GazeSnatchersCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("gazesnatchers")
                        .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> spawn(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "type")))))
                        .then(Commands.literal("trigger_sleep")
                                .executes(ctx -> triggerSleep(ctx.getSource())))
                        .then(Commands.literal("reload_config")
                                .executes(ctx -> reloadConfig(ctx.getSource()))));
    }

    private static int spawn(CommandSourceStack source, String type) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Réservé à un joueur."));
            return 0;
        }
        ServerLevel level = player.level();
        Vec3 look = player.getEyePosition().add(player.getViewVector(1.0F).scale(2.0D));

        return switch (type.toLowerCase()) {
            case "adult", "gaze_snatcher" -> spawnEntity(source, level, look, ModEntities.GAZE_SNATCHER.create(level, EntitySpawnReason.COMMAND));
            case "baby", "baby_gaze_snatcher" -> spawnEntity(source, level, look, ModEntities.BABY_GAZE_SNATCHER.create(level, EntitySpawnReason.COMMAND));
            default -> {
                source.sendFailure(Component.literal("Types : adult, baby"));
                yield 0;
            }
        };
    }

    private static int spawnEntity(CommandSourceStack source, ServerLevel level, Vec3 pos, net.minecraft.world.entity.Entity entity) {
        if (entity == null) {
            source.sendFailure(Component.literal("Échec de création de l'entité."));
            return 0;
        }
        GroundSpawn.placeOnSurface(level, entity, pos.x, pos.z, pos.y);
        BlockPos at = entity.blockPosition();
        if (entity instanceof GazeSnatcherEntity snatcher) {
            snatcher.finalizeSpawn(level, level.getCurrentDifficultyAt(at), EntitySpawnReason.COMMAND, null);
        } else if (entity instanceof BabyGazeSnatcherEntity baby) {
            baby.finalizeSpawn(level, level.getCurrentDifficultyAt(at), EntitySpawnReason.COMMAND, null);
        }
        level.addFreshEntity(entity);
        source.sendSuccess(() -> Component.literal("Entité spawnée."), true);
        return 1;
    }

    private static int triggerSleep(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Réservé à un joueur."));
            return 0;
        }
        SleepWatcher.onPlayerStartSleeping(player, player.blockPosition());
        source.sendSuccess(() -> Component.literal("Tentative de spawn sommeil (comme au coucher)."), true);
        return 1;
    }

    private static int reloadConfig(CommandSourceStack source) {
        GazeSnatchersConfig.load();
        source.sendSuccess(() -> Component.literal("Config gazesnatchers.json rechargée."), true);
        return 1;
    }

    private GazeSnatchersCommands() {
    }
}
