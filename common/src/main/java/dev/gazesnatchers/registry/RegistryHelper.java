package dev.gazesnatchers.registry;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

final class RegistryHelper {
    private RegistryHelper() {
    }

    static SoundEvent registerSound(String path) {
        Identifier id = GazeSnatchers.id(path);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
