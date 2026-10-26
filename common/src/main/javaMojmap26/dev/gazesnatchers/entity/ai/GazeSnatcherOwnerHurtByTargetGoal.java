package dev.gazesnatchers.entity.ai;

import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

/** Attaque qui a blessé le propriétaire (même si le proprio est en train de mourir). */
public class GazeSnatcherOwnerHurtByTargetGoal extends TargetGoal {
    private final GazeSnatcherEntity snatcher;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public GazeSnatcherOwnerHurtByTargetGoal(GazeSnatcherEntity snatcher) {
        super(snatcher, false);
        this.snatcher = snatcher;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!snatcher.isTamed() || snatcher.isOrderedToSit()) {
            return false;
        }
        LivingEntity owner = snatcher.getOwner();
        if (owner == null) {
            return false;
        }
        ownerLastHurtBy = owner.getLastHurtByMob();
        int t = owner.getLastHurtByMobTimestamp();
        return t != timestamp
                && canAttack(ownerLastHurtBy, TargetingConditions.DEFAULT)
                && snatcher.wantsToAttack(ownerLastHurtBy, owner);
    }

    @Override
    public void start() {
        this.mob.setTarget(ownerLastHurtBy);
        LivingEntity owner = snatcher.getOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
