package net.sindarin27.farsightedmobs.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.sindarin27.farsightedmobs.FarsightedMobs;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record MoonBrightnessPredicate(MinMaxBounds.Doubles value) implements LootItemCondition {
    public static final MapCodec<MoonBrightnessPredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(MinMaxBounds.Doubles.CODEC.fieldOf("value").forGetter(MoonBrightnessPredicate::value))
                    .apply(instance, MoonBrightnessPredicate::new)
    );
    @Override
    public @NotNull LootItemConditionType getType() { return FarsightedMobs.MOON_BRIGHTNESS_PREDICATE.get(); }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return this.value.matches(lootContext.getLevel().getMoonBrightness());
    }
}
