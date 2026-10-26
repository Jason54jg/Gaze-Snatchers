package dev.gazesnatchers.entity;

import dev.gazesnatchers.entity.ai.GazeSnatcherPositionGoal;
import dev.gazesnatchers.entity.ai.GazeSnatcherStealGoal;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.util.SunSensitiveEntity;
import dev.gazesnatchers.util.WorldTime;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * Adulte : passif le jour, fixe et vole la nuit pendant le sommeil.
 * Rendu / anim : GeckoLib (geo + animations Blockbench).
 */
public class GazeSnatcherEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> SLEEPER_ID =
            SynchedEntityData.defineId(GazeSnatcherEntity.class, EntityDataSerializers.STRING);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation STEAL = RawAnimation.begin().thenPlay("gaze_steal");
    private static final RawAnimation ANGRY = RawAnimation.begin().thenPlay("angry");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private float stealProgress;
    private int interactionCooldown;
    @Nullable
    private ItemStack lastStolenItem = ItemStack.EMPTY;

    public GazeSnatcherEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).triggerableAnim("gaze_steal", STEAL).triggerableAnim("angry", ANGRY));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public void playStealAnimation() {
        this.triggerAnim("main", "gaze_steal");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ModEntities.gazeSnatcherAttributes();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEEPER_ID, "");
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new GazeSnatcherPositionGoal(this));
        this.goalSelector.addGoal(3, new GazeSnatcherStealGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.65D));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.5D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (!this.isDayPassive() && this.getRandom().nextInt(6) == 0) {
                this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getX() + (this.getRandom().nextDouble() - 0.5D) * 0.4D,
                        this.getEyeY() - 0.2D,
                        this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 0.4D,
                        0.0D, 0.01D, 0.0D);
            }
            return;
        }

        if (interactionCooldown > 0) {
            interactionCooldown--;
        }
        SunSensitiveEntity.tickSunBurn(this);

        if (!this.isDayPassive() && this.tickCount % 80 == 0) {
            this.playSound(ModSounds.GAZE_SNATCHER_AMBIENT, 0.9F, 0.75F + this.getRandom().nextFloat() * 0.35F);
        }
    }

    /** Jour : totalement passif et inoffensif. */
    public boolean isDayPassive() {
        return WorldTime.isDay(this.level());
    }

    @Nullable
    public ServerPlayer getLinkedSleeper() {
        String id = this.entityData.get(SLEEPER_ID);
        if (id.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (serverLevel.getPlayerInAnyDimension(UUID.fromString(id)) instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

    public void linkSleeper(ServerPlayer player) {
        this.entityData.set(SLEEPER_ID, player.getUUID().toString());
    }

    public void resetStealProgress() {
        this.stealProgress = 0.0F;
    }

    public void addStealProgress(float amount) {
        this.stealProgress += amount;
    }

    public float getStealProgress() {
        return stealProgress;
    }

    public void notePlayerInteraction() {
        this.interactionCooldown = 40;
        resetStealProgress();
    }

    public boolean wasRecentlyInteractedWith() {
        return interactionCooldown > 0;
    }

    public void markStolenItem(ItemStack stack) {
        this.lastStolenItem = stack.copy();
    }

    @Nullable
    public ItemStack getLastStolenItem() {
        return lastStolenItem;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (isDayPassive()) {
            this.setTarget(null);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity target) {
        if (isDayPassive()) {
            return false;
        }
        return super.doHurtTarget(level, target);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            notePlayerInteraction();
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.PHANTOM_HURT, 0.7F, 0.55F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GAZE_SNATCHER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GAZE_SNATCHER_DEATH;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level().isClientSide() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int min = dev.gazesnatchers.config.GazeSnatchersConfig.minBabies();
        int max = dev.gazesnatchers.config.GazeSnatchersConfig.maxBabies();
        int count = min + serverLevel.getRandom().nextInt(Math.max(1, max - min + 1));
        ItemStack stolenLoot = lastStolenItem != null ? lastStolenItem : ItemStack.EMPTY;
        int lootCarrier = !stolenLoot.isEmpty() ? serverLevel.getRandom().nextInt(count) : -1;
        for (int i = 0; i < count; i++) {
            BabyGazeSnatcherEntity baby = ModEntities.BABY_GAZE_SNATCHER.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            if (baby == null) {
                continue;
            }
            if (i == lootCarrier) {
                baby.inheritStolenItem(stolenLoot);
            }
            double angle = (Math.PI * 2.0D / count) * i;
            baby.setPos(
                    this.getX() + Math.cos(angle) * 0.8D,
                    this.getY() + 0.1D,
                    this.getZ() + Math.sin(angle) * 0.8D);
            baby.setOnGround(true);
            baby.setYRot(this.getYRot());
            baby.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            serverLevel.addFreshEntity(baby);
            baby.playSound(ModSounds.BABY_GAZE_SNATCHER_AMBIENT, 0.8F, 1.4F + serverLevel.getRandom().nextFloat() * 0.3F);
        }
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("StealProgress", stealProgress);
        String sleeper = this.entityData.get(SLEEPER_ID);
        if (!sleeper.isEmpty()) {
            output.putString("Sleeper", sleeper);
        }
        if (lastStolenItem != null && !lastStolenItem.isEmpty()) {
            output.store("LastStolen", ItemStack.CODEC, lastStolenItem);
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        stealProgress = input.getFloatOr("StealProgress", 0.0F);
        input.getString("Sleeper").ifPresent(s -> this.entityData.set(SLEEPER_ID, s));
        lastStolenItem = input.read("LastStolen", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isPreventingPlayerRest(ServerLevel level, net.minecraft.world.entity.player.Player player) {
        return !isDayPassive();
    }
}
