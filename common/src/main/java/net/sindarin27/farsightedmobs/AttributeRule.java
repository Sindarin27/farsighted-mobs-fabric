package net.sindarin27.farsightedmobs;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.slf4j.Logger;

import java.util.Optional;

public class AttributeRule {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final EntityPredicate predicate;
    private final Holder<Attribute> attribute;
    private final Optional<AttributeBaseValue> attributeBase;
    private final Optional<AttributeModifier> attributeModifier;
    private final int priority;
    public ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(FarsightedMobs.MOD_ID, "unnamed");

    public boolean Apply(ServerLevel level, Mob mob) {
        if (predicate.matches(level, mob.position(), mob)) {
            attributeBase.ifPresent(base -> 
            {
                if (base.evaluateCondition(mob.getAttributeValue(attribute))) {
                    if (FarsightedMobs.DEBUG) LOGGER.info("Base value condition applied for rule {}", identifier);
                    AttributeUtility.ChangeBaseAttributeValue(mob, attribute, base.getValue());
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

    public AttributeRule(int priority, EntityPredicate predicate, Holder<Attribute> attribute, Optional<AttributeBaseValue> baseValue, Optional<AttributeModifier> modifierValue) {
        this.priority = priority;
        this.predicate = predicate;
        this.attribute = attribute;
        this.attributeBase = baseValue;
        this.attributeModifier = modifierValue;
        if (!(attributeBase.isPresent() || attributeModifier.isPresent()))
            throw new IllegalArgumentException("Attribute base and modifier can't both be empty");
    }

    public static MapCodec<AttributeRule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(rule -> rule.priority),
                    EntityPredicate.CODEC.fieldOf("predicate").forGetter(rule -> rule.predicate),
                    Attribute.CODEC.fieldOf("attribute").forGetter(rule -> rule.attribute),
                    AttributeBaseValue.CODEC.optionalFieldOf("base").forGetter(rule -> rule.attributeBase),
                    AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(rule -> rule.attributeModifier)
            ).apply(instance, AttributeRule::new)
    );

    public int getPriority() {
        return priority;
    }
    
}

