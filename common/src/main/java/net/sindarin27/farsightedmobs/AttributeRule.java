package net.sindarin27.farsightedmobs;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

import java.util.Optional;

public class AttributeRule {
    private static final Logger LOGGER = LogUtils.getLogger();
    // Yes, it's called a *loot item* condition. Yes, it's far more flexible than that. 
    // Really it's just a *condition with context*.
    private final LootItemCondition condition;
    private final Holder<Attribute> attribute;
    private final Optional<AttributeBaseValue> attributeBase;
    private final Optional<AttributeModifier> attributeModifier;
    private final int priority;
    public ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(FarsightedMobs.MOD_ID, "unnamed");
    public static final LootContextParamSet SPAWN_CONDITION = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .build();
    
    public boolean Apply(ServerLevel level, Mob mob) {
        // For fun, get the closest player to allow datapack users to do magic
        Player closestPlayer = level.getNearestPlayer(mob, 2048);
        
        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob) // "this" is the mob being spawned
                .withParameter(LootContextParams.ORIGIN, mob.position()) // origin is the mob's spawn position
                ;
        if (closestPlayer != null) {
            // If a player is in range, add their luck and add them as the "attacking" entity
            paramsBuilder
                    .withLuck(closestPlayer.getLuck())
                    .withParameter(LootContextParams.ATTACKING_ENTITY, closestPlayer);
        }
        LootParams params = paramsBuilder.create(SPAWN_CONDITION);
        
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        if (condition.test(context)) {
            attributeBase.ifPresent(base -> 
            {
                double newBaseValue = base.getValue(context);
                if (base.getCondition().Evaluate(mob.getAttributeValue(attribute), newBaseValue)) {
                    if (FarsightedMobs.DEBUG) LOGGER.info("Base value condition applied for rule {}", identifier);
                    AttributeUtility.ChangeBaseAttributeValue(mob, attribute, newBaseValue);
                }
                else {
                    if (FarsightedMobs.DEBUG) LOGGER.info("Base value condition did not apply for rule {}", identifier);
                }
            });
            attributeModifier.ifPresent(modifier -> AttributeUtility.AddAttributeModifier(mob, attribute, modifier));
            return true;
        }
        return false;
    }

    public AttributeRule(int priority, LootItemCondition condition, Holder<Attribute> attribute, Optional<AttributeBaseValue> baseValue, Optional<AttributeModifier> modifierValue) {
        this.priority = priority;
        this.condition = condition;
        this.attribute = attribute;
        this.attributeBase = baseValue;
        this.attributeModifier = modifierValue;
        if (!(attributeBase.isPresent() || attributeModifier.isPresent()))
            throw new IllegalArgumentException("Attribute base and modifier can't both be empty");
    }

    public static MapCodec<AttributeRule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(rule -> rule.priority),
                    // Mojang generally uses the DIRECT_CODEC instead of the normal CODEC (which returns a Holder)
                    LootItemCondition.DIRECT_CODEC.fieldOf("condition").forGetter(rule -> rule.condition),
                    Attribute.CODEC.fieldOf("attribute").forGetter(rule -> rule.attribute),
                    AttributeBaseValue.CODEC.optionalFieldOf("base").forGetter(rule -> rule.attributeBase),
                    AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(rule -> rule.attributeModifier)
            ).apply(instance, AttributeRule::new)
    );

    public int getPriority() {
        return priority;
    }
    
}

