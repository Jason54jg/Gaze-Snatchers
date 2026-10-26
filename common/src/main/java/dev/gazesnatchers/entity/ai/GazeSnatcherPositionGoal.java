package dev.gazesnatchers.entity.ai;

import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Place l'adulte juste devant le joueur endormi et le fait le fixer.
 */
public class GazeSnatcherPositionGoal extends Goal {
    private final GazeSnatcherEntity snatcher;

    public GazeSnatcherPositionGoal(GazeSnatcherEntity snatcher) {
        this.snatcher = snatcher;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (snatcher.isDayPassive()) {
            return false;
        }
        ServerPlayer sleeper = snatcher.getLinkedSleeper();
        return sleeper != null && sleeper.isSleeping();
    }

    @Override
    public void tick() {
        ServerPlayer sleeper = snatcher.getLinkedSleeper();
        if (sleeper == null) {
            return;
        }
        snatcher.getLookControl().setLookAt(sleeper, 40.0F, 40.0F);

        double distance = snatcher.distanceToSqr(sleeper);
        int maxDist = GazeSnatchersConfig.stareDistance();
        if (distance > (double) maxDist * maxDist) {
            snatcher.getNavigation().moveTo(sleeper, 1.05D);
            return;
        }

        Vec3 forward = sleeper.getViewVector(1.0F).normalize();
        Vec3 desired = sleeper.position()
                .add(forward.scale(1.8D))
                .add(0.0D, 0.05D, 0.0D);

        if (snatcher.distanceToSqr(desired) > 0.6D) {
            snatcher.getNavigation().moveTo(desired.x, desired.y, desired.z, 1.0D);
        } else {
            snatcher.getNavigation().stop();
        }
    }
}
