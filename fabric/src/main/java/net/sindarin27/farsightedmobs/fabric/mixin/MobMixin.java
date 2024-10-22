package net.sindarin27.farsightedmobs.fabric.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.sindarin27.farsightedmobs.FarsightedMobs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class)
public class MobMixin {
    @Inject(method = "finalizeSpawn", at=@At("RETURN"))
    private void finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        FarsightedMobs.OnMobSpawn((Mob)(Object)this); // Such an ugly cast, but it's required. "this" does refer to the Mob object, but the compiler gets confused due to the mixin class.
    }
}
