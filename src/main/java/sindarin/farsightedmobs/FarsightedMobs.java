package sindarin.farsightedmobs;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import sindarin.farsightedmobs.config.ModConfig;
import sindarin.farsightedmobs.mixin.FollowTargetGoalAccessor;
import sindarin.farsightedmobs.mixin.MobEntityAccessor;

public class FarsightedMobs implements ModInitializer {
    public static ModConfig CONFIG = new ModConfig();
    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static Entity upgradeEntity(Entity e) {
        if (e instanceof LivingEntity) {
            Identifier type = EntityType.getId(e.getType());
            LivingEntity living = (LivingEntity)e;
            if (FarsightedMobs.CONFIG.followRanges.containsKey(type.toString())) {
                int range = FarsightedMobs.CONFIG.followRanges.get(type.toString());
                living.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(range);
                FixFollowRange(living);
                return living;
            }
        }
        return e;
    }

    public static void FixFollowRange(LivingEntity livingEntity) {
        if (livingEntity instanceof MobEntity) {
            ((MobEntityAccessor) livingEntity).getTargetSelector().getGoals().forEach(prioritizedGoal -> {
                Goal goal = prioritizedGoal.getGoal();
                if (goal instanceof FollowTargetGoal) {
                    FollowTargetGoalAccessor followTargetGoal = (FollowTargetGoalAccessor)  goal;
                    followTargetGoal.setTargetPredicate(followTargetGoal.getTargetPredicate()
                            .setBaseMaxDistance(livingEntity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)));
                }
            });
        }
    }
}
