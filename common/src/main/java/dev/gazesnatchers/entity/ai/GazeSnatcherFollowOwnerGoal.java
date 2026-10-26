package dev.gazesnatchers.entity.ai;

import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

import java.util.EnumSet;

/** Suit le propriétaire ; se téléporte s'il est trop loin. */
public class GazeSnatcherFollowOwnerGoal extends Goal {
    private final GazeSnatcherEntity snatcher;
    private final double speed;
    private final float startDistance;
    private final float stopDistance;
    private LivingEntity owner;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public GazeSnatcherFollowOwnerGoal(GazeSnatcherEntity snatcher, double speed, float startDistance, float stopDistance) {
        this.snatcher = snatcher;
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!snatcher.isTamed() || snatcher.isOrderedToSit()) {
            return false;
        }
        LivingEntity combatTarget = snatcher.getTarget();
        if (combatTarget != null && combatTarget.isAlive()) {
            return false;
        }
        LivingEntity owner = snatcher.getOwner();
        // Dead / spectator owner: stay put and keep fighting last attacker instead of pathing to corpse
        if (owner == null || !owner.isAlive() || owner.isSpectator()) {
            return false;
        }
        if (snatcher.distanceToSqr(owner) < (double) startDistance * startDistance) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (snatcher.isOrderedToSit() || owner == null || !owner.isAlive()) {
            return false;
        }
        LivingEntity combatTarget = snatcher.getTarget();
        if (combatTarget != null && combatTarget.isAlive()) {
            return false;
        }
        PathNavigation nav = snatcher.getNavigation();
        return !nav.isDone() || snatcher.distanceToSqr(owner) > (double) stopDistance * stopDistance;
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = snatcher.getPathfindingMalus(PathType.WATER);
        snatcher.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        owner = null;
        snatcher.getNavigation().stop();
        snatcher.setPathfindingMalus(PathType.WATER, oldWaterCost);
    }

    @Override
    public void tick() {
        if (owner == null) {
            return;
        }
        snatcher.getLookControl().setLookAt(owner, 10.0F, snatcher.getMaxHeadXRot());
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!snatcher.isLeashed() && !snatcher.isPassenger()) {
                if (snatcher.distanceToSqr(owner) >= 144.0D) {
                    snatcher.tryTeleportToOwner();
                } else {
                    snatcher.getNavigation().moveTo(owner, speed);
                }
            }
        }
    }
}
