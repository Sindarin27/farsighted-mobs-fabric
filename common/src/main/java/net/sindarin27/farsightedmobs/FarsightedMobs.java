package net.sindarin27.farsightedmobs;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.List;

public final class FarsightedMobs {
    public static final String MOD_ID = "farsightedmobs";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final double defaultHostileRange = 32;

    public static void init() {
        // Write common init code here.
    }
    
    public static void OnMobSpawn(Mob mob) {
        // If monster, update the follow range
        if (mob instanceof Monster monster) {
            // But only when the new value is bigger than the old
            double originalFollow = monster.getAttributeBaseValue(Attributes.FOLLOW_RANGE);
            if (originalFollow < defaultHostileRange) {
                ChangeBaseAttributeValue(monster, Attributes.FOLLOW_RANGE, defaultHostileRange);
            }
        }

        // Fix the minecraft bug that causes entities to never update their follow range by updating it once they spawn
        FixFollowRange(mob);
    }

    // Change the base value of the given attribute on the given entity to the given value
    private static void ChangeBaseAttributeValue(Mob mob, Holder<Attribute> attribute, double value) {
        AttributeInstance attributeInstance = mob.getAttribute(attribute);
        // Safety goes first
        if (attributeInstance == null) {
            LOGGER.warn("No attribute instance found for " + attribute.value().getDescriptionId());
            return;
        }
        attributeInstance.setBaseValue(value);
    }

    // Workaround for MC-145656 (https://bugs.mojang.com/browse/MC-145656)
    private static void FixFollowRange(Mob mob) {
        mob.targetSelector.getAvailableGoals().forEach(wrappedGoal -> {
            Goal goal = wrappedGoal.getGoal();
            if (goal instanceof NearestAttackableTargetGoal<?> natGoal) {
                natGoal.targetConditions = natGoal.targetConditions.range(mob.getAttributeValue(Attributes.FOLLOW_RANGE));
            }
        });
    }
}
