package sindarin.farsightedmobs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
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
