package net.sindarin27.farsightedmobs;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class AttributeChanger {
    private final Holder<Attribute> attribute;
    private final Optional<AttributeBaseValue> attributeBase;
    private final Optional<AttributeModifier> attributeModifier;

    public static MapCodec<AttributeChanger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Attribute.CODEC.fieldOf("key").forGetter(changer -> changer.attribute),
                    AttributeBaseValue.CODEC.optionalFieldOf("base").forGetter(changer -> changer.attributeBase),
                    AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(changer -> changer.attributeModifier)
            ).apply(instance, AttributeChanger::new)
    );

    public AttributeChanger(Holder<Attribute> attribute, Optional<AttributeBaseValue> attributeBase, Optional<AttributeModifier> attributeModifier) {
        this.attribute = attribute;
        this.attributeBase = attributeBase;
        this.attributeModifier = attributeModifier;
    }

    public void Apply(Mob mob, LootContext context) {
        attributeBase.ifPresent(base ->
        {
            double newBaseValue = base.getValue(context);
            if (base.getCondition().Evaluate(mob.getAttributeValue(attribute), newBaseValue)) {
                AttributeUtility.ChangeBaseAttributeValue(mob, attribute, newBaseValue);
            }
        });
        attributeModifier.ifPresent(modifier -> AttributeUtility.AddAttributeModifier(mob, attribute, modifier));
    }
}
