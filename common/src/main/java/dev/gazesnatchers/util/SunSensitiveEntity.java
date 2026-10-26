package dev.gazesnatchers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Comportement undead : brûle en plein soleil ou sous forte lumière.
 */
public final class SunSensitiveEntity {
    private SunSensitiveEntity() {
    }

    public static void tickSunBurn(Mob mob) {
        if (mob.level().isClientSide()) {
            return;
        }
        if (!shouldBurn(mob)) {
            return;
        }
        ItemStack helmet = mob.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        if (!helmet.isEmpty() && helmet.isDamageableItem()) {
            helmet.hurtAndBreak(1, mob, net.minecraft.world.entity.EquipmentSlot.HEAD);
            return;
        }
        mob.igniteForSeconds(8.0F);
    }

    public static boolean shouldBurn(LivingEntity entity) {
        Level level = entity.level();
        if (!WorldTime.isDay(level)) {
            return false;
        }
        BlockPos pos = entity.blockPosition();
        if (level.isRainingAt(pos) || level.isRainingAt(pos.above())) {
            return false;
        }
        float brightness = entity.getLightLevelDependentMagicValue();
        if (brightness <= 0.5F) {
            return false;
        }
        BlockState above = level.getBlockState(pos.above());
        if (above.is(BlockTags.LEAVES) || above.is(Blocks.COBWEB)) {
            return false;
        }
        int sky = level.getBrightness(LightLayer.SKY, pos);
        int block = level.getBrightness(LightLayer.BLOCK, pos);
        return sky > 11 && block == 0;
    }
}
