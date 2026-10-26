package dev.gazesnatchers.item;

import net.minecraft.world.item.Item;

/**
 * Charme rare : clic droit sur un Gaze Snatcher adulte sauvage pour l'apprivoiser.
 */
public class GazeBindingCharmItem extends Item {
    public GazeBindingCharmItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
