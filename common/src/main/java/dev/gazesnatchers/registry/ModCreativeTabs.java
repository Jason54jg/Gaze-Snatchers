package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static CreativeModeTab MAIN;

    public static void register() {
        register(null);
    }

    public static void register(BiConsumer<Identifier, CreativeModeTab> registrar) {
        Identifier id = GazeSnatchers.id("main");
        CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("itemGroup.gazesnatchers.main"))
                .icon(() -> new ItemStack(ModItems.GAZE_SHARD))
                .displayItems((params, output) -> {
                    output.accept(ModItems.GAZE_SHARD);
                    output.accept(ModItems.GAZE_WARD_AMULET);
                    output.accept(ModItems.GAZE_DRAUGHT);
                    output.accept(ModItems.GAZE_BINDING_CHARM);
                    output.accept(ModItems.GAZE_SNATCHER_SPAWN_EGG);
                    output.accept(ModItems.BABY_GAZE_SNATCHER_SPAWN_EGG);
                })
                .build();
        if (registrar == null) {
            MAIN = net.minecraft.core.Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
        } else {
            registrar.accept(id, tab);
            MAIN = tab;
        }
    }

    private ModCreativeTabs() {
    }
}
