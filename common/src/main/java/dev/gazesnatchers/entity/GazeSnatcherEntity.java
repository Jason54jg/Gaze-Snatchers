package dev.gazesnatchers.entity;

import dev.gazesnatchers.entity.ai.GazeSnatcherFollowOwnerGoal;
import dev.gazesnatchers.entity.ai.GazeSnatcherOwnerHurtByTargetGoal;
import dev.gazesnatchers.entity.ai.GazeSnatcherOwnerHurtTargetGoal;
import dev.gazesnatchers.entity.ai.GazeSnatcherPositionGoal;
import dev.gazesnatchers.entity.ai.GazeSnatcherStealGoal;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.util.SunSensitiveEntity;
import dev.gazesnatchers.util.WorldTime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * Adulte : passif le jour, vole la nuit ; peut être apprivoisé avec un Gaze Binding Charm.
 */
public class GazeSnatcherEntity extends Monster implements GeoEntity, OwnableEntity {
    private static final EntityDataAccessor<String> SLEEPER_ID =
            SynchedEntityData.defineId(GazeSnatcherEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_TAMED =
            SynchedEntityData.defineId(GazeSnatcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(GazeSnatcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNER =
            SynchedEntityData.defineId(GazeSnatcherEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

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
        builder.define(DATA_TAMED, false);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_OWNER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RestrictSunGoal(this) {
            @Override
            public boolean canUse() {
                return !GazeSnatcherEntity.this.isTamed() && super.canUse();
            }
        });
        // Melee before follow (wolf-like): follow must not steal MOVE/LOOK during combat
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true) {
            @Override
            public boolean canUse() {
                return GazeSnatcherEntity.this.isTamed()
                        && !GazeSnatcherEntity.this.isOrderedToSit()
                        && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return GazeSnatcherEntity.this.isTamed()
                        && !GazeSnatcherEntity.this.isOrderedToSit()
                        && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(3, new GazeSnatcherFollowOwnerGoal(this, 1.15D, 8.0F, 3.0F));
        this.goalSelector.addGoal(4, new GazeSnatcherPositionGoal(this));
        this.goalSelector.addGoal(5, new GazeSnatcherStealGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.65D) {
            @Override
            public boolean canUse() {
                return !GazeSnatcherEntity.this.isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.5D) {
            @Override
            public boolean canUse() {
                return !GazeSnatcherEntity.this.isOrderedToSit() && super.canUse();
            }
        });

        this.targetSelector.addGoal(1, new GazeSnatcherOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new GazeSnatcherOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                LivingEntity attacker = GazeSnatcherEntity.this.getLastHurtByMob();
                if (GazeSnatcherEntity.this.isTamed() && attacker != null && GazeSnatcherEntity.this.isOwnedBy(attacker)) {
                    return false;
                }
                return super.canUse();
            }
        });
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.GAZE_BINDING_CHARM) && !this.isTamed()) {
            if (!this.level().isClientSide()) {
                this.tame(player);
                stack.consume(1, player);
                this.level().broadcastEntityEvent(this, (byte) 7);
                this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 0.7F);
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTamed() && this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND && stack.isEmpty()) {
            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.getNavigation().stop();
                this.setTarget(null);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public void tame(Player player) {
        this.setTamed(true);
        this.setOwner(player);
        this.setOrderedToSit(false);
        this.entityData.set(SLEEPER_ID, "");
        this.resetStealProgress();
        this.setTarget(null);
        if (player instanceof ServerPlayer serverPlayer) {
            this.setPersistenceRequired();
        }
    }

    public boolean isTamed() {
        return this.entityData.get(DATA_TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(DATA_TAMED, tamed);
    }

    public boolean isOrderedToSit() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity != null && entity == this.getOwner();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isOwnedBy(target) && super.canAttack(target);
    }

    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target == null || target == this || target == owner) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (target instanceof GazeSnatcherEntity other && other.isTamed() && other.isOwnedBy(owner)) {
            return false;
        }
        if (target instanceof OwnableEntity ownable) {
            LivingEntity otherOwner = ownable.getOwner();
            if (otherOwner != null && otherOwner == owner) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(DATA_OWNER).orElse(null);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.entityData.set(DATA_OWNER, owner == null ? Optional.empty() : Optional.of(EntityReference.of(owner)));
    }

    public void tryTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        if (owner == null || !(this.level() instanceof ServerLevel)) {
            return;
        }
        for (int i = 0; i < 12; i++) {
            int dx = this.random.nextInt(7) - 3;
            int dz = this.random.nextInt(7) - 3;
            BlockPos pos = owner.blockPosition().offset(dx, 0, dz);
            if (canTeleportTo(pos)) {
                this.teleportTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                this.getNavigation().stop();
                return;
            }
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockState below = this.level().getBlockState(pos.below());
        return below.blocksMotion()
                && this.level().noCollision(this, this.getBoundingBox().move(
                pos.getX() + 0.5D - this.getX(),
                pos.getY() - this.getY(),
                pos.getZ() + 0.5D - this.getZ()));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            for (int i = 0; i < 12; i++) {
                this.level().addParticle(
                        ParticleTypes.HEART,
                        this.getRandomX(1.0D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D),
                        0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (!this.isTamed() && !this.isDayPassive() && this.random.nextInt(6) == 0) {
                this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getX() + (this.random.nextDouble() - 0.5D) * 0.4D,
                        this.getEyeY() - 0.2D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D,
                        0.0D, 0.01D, 0.0D);
            }
            return;
        }

        if (interactionCooldown > 0) {
            interactionCooldown--;
        }
        if (!this.isTamed()) {
            SunSensitiveEntity.tickSunBurn(this);
        }

        if (!this.isTamed() && !this.isDayPassive() && this.tickCount % 80 == 0) {
            this.playSound(ModSounds.GAZE_SNATCHER_AMBIENT, 0.9F, 0.75F + this.random.nextFloat() * 0.35F);
        }
    }

    /** Jour : passif sauf si apprivoisé (compagnon actif). */
    public boolean isDayPassive() {
        return !this.isTamed() && WorldTime.isDay(this.level());
    }

    @Nullable
    public ServerPlayer getLinkedSleeper() {
        if (this.isTamed()) {
            return null;
        }
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
        if (this.isTamed()) {
            return;
        }
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
        if (this.isOrderedToSit()) {
            this.setTarget(null);
            this.getNavigation().stop();
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        if (isDayPassive()) {
            return false;
        }
        return super.doHurtTarget(level, target);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && !this.isOwnedBy(player)) {
            notePlayerInteraction();
        }
        if (this.isTamed() && !this.level().isClientSide()) {
            this.setOrderedToSit(false);
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
        if (this.isTamed() || this.level().isClientSide() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int min = dev.gazesnatchers.config.GazeSnatchersConfig.minBabies();
        int max = dev.gazesnatchers.config.GazeSnatchersConfig.maxBabies();
        int count = min + serverLevel.random.nextInt(Math.max(1, max - min + 1));
        ItemStack stolenLoot = lastStolenItem != null ? lastStolenItem : ItemStack.EMPTY;
        int lootCarrier = !stolenLoot.isEmpty() ? serverLevel.random.nextInt(count) : -1;
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
            baby.playSound(ModSounds.BABY_GAZE_SNATCHER_AMBIENT, 0.8F, 1.4F + serverLevel.random.nextFloat() * 0.3F);
        }
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("StealProgress", stealProgress);
        output.putBoolean("Tamed", this.isTamed());
        output.putBoolean("Sitting", this.isOrderedToSit());
        String sleeper = this.entityData.get(SLEEPER_ID);
        if (!sleeper.isEmpty()) {
            output.putString("Sleeper", sleeper);
        }
        EntityReference.store(this.getOwnerReference(), output, "Owner");
        if (lastStolenItem != null && !lastStolenItem.isEmpty()) {
            output.store("LastStolen", ItemStack.CODEC, lastStolenItem);
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        stealProgress = input.getFloatOr("StealProgress", 0.0F);
        this.setTamed(input.getBooleanOr("Tamed", false));
        this.setOrderedToSit(input.getBooleanOr("Sitting", false));
        input.getString("Sleeper").ifPresent(s -> this.entityData.set(SLEEPER_ID, s));
        EntityReference<LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "Owner", this.level());
        this.entityData.set(DATA_OWNER, owner == null ? Optional.empty() : Optional.of(owner));
        lastStolenItem = input.read("LastStolen", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isPreventingPlayerRest(ServerLevel level, Player player) {
        if (this.isTamed()) {
            return false;
        }
        return !isDayPassive();
    }
}
