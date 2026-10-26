package dev.gazesnatchers.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Marque et identifie les items volés par un Gaze Snatcher. */
public final class StolenItemHelper {
    private StolenItemHelper() {
    }

    public static ItemStack markStolen(ItemStack stack) {
        ItemStack copy = stack.copy();
        Component label = Component.translatable("item.gazesnatchers.stolen_item_label", copy.getHoverName());
        copy.set(DataComponents.CUSTOM_NAME, label);
        return copy;
    }

    public static boolean isMarkedStolen(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name == null) {
            return false;
        }
        String key = "item.gazesnatchers.stolen_item_label";
        return name.getContents().toString().contains("stolen_item") || name.getString().contains("volé")
                || name.getString().contains("Stolen");
    }
}
