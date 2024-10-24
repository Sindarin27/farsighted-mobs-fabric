package net.sindarin27.farsightedmobs.predicates.origin;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.sindarin27.farsightedmobs.FarsightedMobs;
import net.sindarin27.farsightedmobs.WorldUtility;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record LocalDifficultyPredicate(MinMaxBounds.Doubles value) implements LootItemCondition {
    public static final MapCodec<LocalDifficultyPredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(MinMaxBounds.Doubles.CODEC.fieldOf("value").forGetter(LocalDifficultyPredicate::value))
                    .apply(instance, LocalDifficultyPredicate::new)
    );
    @Override
    public @NotNull LootItemConditionType getType() { return FarsightedMobs.LOCAL_DIFFICULTY_PREDICATE.get(); }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (!lootContext.hasParam(LootContextParams.THIS_ENTITY)) return false;
        BlockPos pos = lootContext.getParam(LootContextParams.THIS_ENTITY).blockPosition();
        // DifficultyInstance:
        // "effective difficulty" is what minecraft.wiki calls "Regional difficulty"
        // "special multiplier" is what minecraft.wiki calls "Clamped regional difficulty"
        DifficultyInstance difficultyInstance = WorldUtility.getCurrentDifficultySafely(lootContext.getLevel(), pos);
        return this.value.matches(difficultyInstance.getEffectiveDifficulty());
    }
}
