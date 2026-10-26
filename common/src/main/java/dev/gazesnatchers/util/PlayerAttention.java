package dev.gazesnatchers.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Détermine si le joueur « prête attention » au mob (regard, interaction récente).
 */
public final class PlayerAttention {
    /** cos(22°) — cône de vision approximatif. */
    private static final double LOOK_DOT_THRESHOLD = 0.925D;

    private PlayerAttention() {
    }

    public static boolean isLookingAt(ServerPlayer player, Entity target) {
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 toTarget = target.getEyePosition().subtract(player.getEyePosition()).normalize();
        return look.dot(toTarget) >= LOOK_DOT_THRESHOLD;
    }

    public static boolean hasGazeWardAmulet(ServerPlayer player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            var stack = player.getInventory().getItem(slot);
            if (dev.gazesnatchers.registry.ModItems.GAZE_WARD_AMULET == stack.getItem()) {
                return true;
            }
        }
        return false;
    }
}
