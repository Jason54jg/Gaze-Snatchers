package dev.gazesnatchers.entity;

import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.util.SunSensitiveEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Bébé : toujours agressif, rapide, craint le soleil. Peut transporter un item volé par l'adulte.
 */
public class BabyGazeSnatcherEntity extends Monster {
    @Nullable
    private ItemStack inheritedStolenItem = ItemStack.EMPTY;

    public BabyGazeSnatcherEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ModEntities.babyGazeSnatcherAttributes();
    }

    public void inheritStolenItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            this.inheritedStolenItem = stack.copy();
        }
    }

    public boolean carriesStolenLoot() {
        return inheritedStolenItem != null && !inheritedStolenItem.isEmpty();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            SunSensitiveEntity.tickSunBurn(this);
            if (this.tickCount % 45 == 0) {
                this.playSound(ModSounds.BABY_GAZE_SNATCHER_AMBIENT, 0.85F, 1.35F + this.random.nextFloat() * 0.25F);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        RandomSource random = level.random;
        if (carriesStolenLoot()) {
            boolean always = GazeSnatchersConfig.alwaysDropStolenFromCarrier();
            if (always || random.nextDouble() < GazeSnatchersConfig.rareStolenItemDropChance()) {
                this.spawnAtLocation(level, inheritedStolenItem.copy());
                inheritedStolenItem = ItemStack.EMPTY;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (carriesStolenLoot()) {
            output.store("InheritedStolen", ItemStack.CODEC, inheritedStolenItem);
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        inheritedStolenItem = input.read("InheritedStolen", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BABY_GAZE_SNATCHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BABY_GAZE_SNATCHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BABY_GAZE_SNATCHER_DEATH;
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(getHurtSound(source), 0.9F, 1.2F);
    }
}
