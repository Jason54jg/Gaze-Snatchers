package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static CreativeModeTab MAIN;

    public static void register() {
        MAIN = net.minecraft.core.Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                GazeSnatchers.id("main"),
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.gazesnatchers.main"))
                        .icon(() -> new ItemStack(ModItems.GAZE_SHARD))
                        .displayItems((params, output) -> {
                            output.accept(ModItems.GAZE_SHARD);
                            output.accept(ModItems.GAZE_WARD_AMULET);
                        })
                        .build());
    }

    private ModCreativeTabs() {
    }
}
