package dev.gazesnatchers.item;

import dev.gazesnatchers.registry.ModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

/** Boisson utilitaire — accorde {@link ModMobEffects#GAZE_CLARITY} (vision / vigilance). */
public class GazeDraughtItem extends DescribedItem {
    private static final int EFFECT_TICKS = 3600;

    public GazeDraughtItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            player.addEffect(new MobEffectInstance(ModMobEffects.GAZE_CLARITY, EFFECT_TICKS, 0));
        }
        stack.consume(1, entity);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }
}
