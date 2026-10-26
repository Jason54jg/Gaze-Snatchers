package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Tab registration only — {@code Output} is protected on vanilla 26.x, so entries are
 * filled from Fabric / NeoForge platform hooks via {@link #TAB_KEY}.
 */
public final class ModCreativeTabs {
    public static final ResourceKey<CreativeModeTab> TAB_KEY =
            ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, GazeSnatchers.id("main"));

    public static CreativeModeTab MAIN;

    public static void register() {
        MAIN = net.minecraft.core.Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                TAB_KEY,
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.gazesnatchers.main"))
                        .icon(() -> new ItemStack(ModItems.GAZE_SHARD))
                        .build());
    }

    private ModCreativeTabs() {
    }
}
