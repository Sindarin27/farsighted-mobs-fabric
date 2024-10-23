package net.sindarin27.farsightedmobs.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.sindarin27.farsightedmobs.FarsightedMobs;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record MoonPhasePredicate(IntRange value) implements LootItemCondition {
    public static final MapCodec<MoonPhasePredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(IntRange.CODEC.fieldOf("value").forGetter(MoonPhasePredicate::value))
                    .apply(instance, MoonPhasePredicate::new)
    );
    @Override
    public @NotNull LootItemConditionType getType() { return FarsightedMobs.MOON_PHASE_PREDICATE.get(); }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return value.getReferencedContextParams();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return this.value.test(lootContext, lootContext.getLevel().getMoonPhase());
    }
}
