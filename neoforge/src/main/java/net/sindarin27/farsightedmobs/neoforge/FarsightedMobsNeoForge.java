package net.sindarin27.farsightedmobs.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.sindarin27.farsightedmobs.FarsightedMobs;

@Mod(FarsightedMobs.MOD_ID)
@EventBusSubscriber(modid = FarsightedMobs.MOD_ID)
public final class FarsightedMobsNeoForge {
    public FarsightedMobsNeoForge() {
        // Run our common setup.
        FarsightedMobs.init();
    }

    @SubscribeEvent
    public static void OnFinalizeSpawnEvent(FinalizeSpawnEvent event) {
        FarsightedMobs.OnMobSpawn(event.getEntity());
    }
}
