package net.sindarin27.farsightedmobs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class AttributeBaseValue {
    private final double value;
    private final Condition condition;

    public AttributeBaseValue(double value) {
        this.value = value;
        this.condition = Condition.ALWAYS;
    }
    
    public AttributeBaseValue(double value, Condition condition) {
        this.value = value;
        this.condition = condition;
    }

    public static MapCodec<AttributeBaseValue> MAPCODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.DOUBLE.fieldOf("value").forGetter(baseVal -> baseVal.value),
                    Condition.CODEC.optionalFieldOf("condition", Condition.ALWAYS).forGetter(baseVal -> baseVal.condition)
            ).apply(instance, AttributeBaseValue::new)
    );

    public static final Codec<AttributeBaseValue> INLINE_CODEC;
    static {
        INLINE_CODEC = Codec.DOUBLE.xmap(AttributeBaseValue::new, baseVal -> baseVal.value);
    }


    public static Codec<AttributeBaseValue> CODEC = Codec.withAlternative(MAPCODEC.codec(), INLINE_CODEC);

    public double getValue() {
        return value;
    }

    public boolean evaluateCondition(double originalBase) {
        return condition.Evaluate(originalBase, this.value);
    }

    public enum Condition implements StringRepresentable {
        ALWAYS("always"),
        GREATER_THAN_BASE("greater_than_base"),
        SMALLER_THAN_BASE("smaller_than_base");


        public static final Codec<Condition> CODEC = StringRepresentable.fromEnum(Condition::values);
        private final String name;

        Condition(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
        
        public boolean Evaluate(double original, double candidate) {
            return switch (this) {
                case ALWAYS -> true;
                case GREATER_THAN_BASE -> candidate > original;
                case SMALLER_THAN_BASE -> candidate < original;
            };
        }
    }
}
