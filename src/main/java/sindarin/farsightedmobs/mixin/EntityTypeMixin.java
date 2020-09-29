package sindarin.farsightedmobs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sindarin.farsightedmobs.FarsightedMobs;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {
    @Inject(method="create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;", at = @At("RETURN"), cancellable = true)
    public void onSpawn(World world, CallbackInfoReturnable<@Nullable Entity> cir) {
        Entity e = cir.getReturnValue();
        e = FarsightedMobs.upgradeEntity(e);
        cir.setReturnValue(e);
    }
}
