package dev.gazesnatchers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Place une entité sur la surface solide (évite le spawn au bas d'un bloc / dans la neige).
 */
public final class GroundSpawn {
    public static void placeOnSurface(ServerLevel level, Entity entity, double x, double z, double hintY) {
        BlockPos column = BlockPos.containing(x, hintY, z);
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ());
        if (surfaceY <= level.getMinY()) {
            surfaceY = column.getY();
        }
        entity.setPos(x, surfaceY, z);
    }

    private GroundSpawn() {
    }
}
