package sindarin.farsightedmobs.mixin;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FollowTargetGoal.class)
public interface FollowTargetGoalAccessor {
    @Accessor
    TargetPredicate getTargetPredicate();

    @Accessor("targetPredicate")
    void setTargetPredicate(TargetPredicate targetPredicate);
}
