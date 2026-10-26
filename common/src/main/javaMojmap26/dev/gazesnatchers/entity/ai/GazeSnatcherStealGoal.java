package dev.gazesnatchers.entity.ai;

import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.util.WorldTime;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.util.PlayerAttention;
import dev.gazesnatchers.util.StolenItemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

/**
 * Vol d'un item aléatoire si le joueur ne prête pas attention assez longtemps.
 */
public class GazeSnatcherStealGoal extends Goal {
    private final GazeSnatcherEntity snatcher;

    public GazeSnatcherStealGoal(GazeSnatcherEntity snatcher) {
        this.snatcher = snatcher;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (snatcher.isTamed() || snatcher.isDayPassive()) {
            return false;
        }
        ServerPlayer sleeper = snatcher.getLinkedSleeper();
        return sleeper != null && (sleeper.isSleeping() || WorldTime.isNight(snatcher.level()));
    }

    @Override
    public void tick() {
        ServerPlayer sleeper = snatcher.getLinkedSleeper();
        if (sleeper == null || !(snatcher.level() instanceof ServerLevel level)) {
            return;
        }

        if (PlayerAttention.isLookingAt(sleeper, snatcher) || snatcher.wasRecentlyInteractedWith()) {
            snatcher.resetStealProgress();
            return;
        }

        float progressRate = 1.0F;
        if (PlayerAttention.hasGazeProtection(sleeper)) {
            progressRate *= GazeSnatchersConfig.amuletStealReduction();
        }

        snatcher.addStealProgress(progressRate);
        if (snatcher.getStealProgress() < GazeSnatchersConfig.ticksBeforeSteal()) {
            return;
        }

        if (tryStealItem(sleeper, level.getRandom())) {
            snatcher.playStealAnimation();
            snatcher.playSound(ModSounds.GAZE_SNATCHER_STEAL, 1.2F, 0.85F + level.getRandom().nextFloat() * 0.2F);
            if (GazeSnatchersConfig.despawnAfterSteal()) {
                snatcher.discard();
            }
        } else {
            snatcher.resetStealProgress();
        }
    }

    private boolean tryStealItem(ServerPlayer player, RandomSource random) {
        var inventory = player.getInventory();
        int attempts = 0;
        while (attempts < 32) {
            attempts++;
            int slot = random.nextInt(inventory.getContainerSize());
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack stolen = StolenItemHelper.markStolen(stack.split(1));
            snatcher.markStolenItem(stolen);
            return true;
        }
        return false;
    }
}
