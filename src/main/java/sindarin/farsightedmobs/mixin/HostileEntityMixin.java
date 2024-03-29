package sindarin.farsightedmobs.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sindarin.farsightedmobs.FarsightedMobs;

@Mixin(value= HostileEntity.class)
public class HostileEntityMixin {
    @Inject(method="createHostileAttributes", at=@At(value="RETURN"))
    private static void createAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> ci) {
        int range = FarsightedMobs.CONFIG.hostileDefaultRange;
        ci.getReturnValue().add(EntityAttributes.GENERIC_FOLLOW_RANGE, range);
    }
}
