package dev.gazesnatchers.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Effet positif : le joueur reste « vigilant » face au Voleur de Regard.
 */
public class GazeClarityMobEffect extends MobEffect {
    public GazeClarityMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9B6DFF);
    }
}
