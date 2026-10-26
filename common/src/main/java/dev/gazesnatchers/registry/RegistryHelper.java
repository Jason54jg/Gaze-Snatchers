package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import java.util.function.BiConsumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

final class RegistryHelper {
    private RegistryHelper() {
    }

    static SoundEvent registerSound(String path) {
        return registerSound(path, (id, sound) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, sound));
    }

    static SoundEvent registerSound(String path, BiConsumer<Identifier, SoundEvent> registrar) {
        Identifier id = GazeSnatchers.id(path);
        SoundEvent sound = SoundEvent.createVariableRangeEvent(id);
        registrar.accept(id, sound);
        return sound;
    }
}
