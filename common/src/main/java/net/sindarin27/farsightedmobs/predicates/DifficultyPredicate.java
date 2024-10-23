package net.sindarin27.farsightedmobs.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import net.sindarin27.farsightedmobs.FarsightedMobs;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record DifficultyPredicate(IntRange value) implements LootItemCondition {
    public static final MapCodec<DifficultyPredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(IntRange.CODEC.fieldOf("value").forGetter(DifficultyPredicate::value))
                    .apply(instance, DifficultyPredicate::new)
    );
    @Override
    public @NotNull LootItemConditionType getType() { return FarsightedMobs.DIFFICULTY_PREDICATE.get(); }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        Set<LootContextParam<?>> set = value.getReferencedContextParams();
        set.add(LootContextParams.ORIGIN);
        return set;
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (!lootContext.hasParam(LootContextParams.ORIGIN)) return false;
        Vec3 pos = lootContext.getParam(LootContextParams.ORIGIN);
        // DifficultyInstance:
        // "effective difficulty" is what minecraft.wiki calls "Regional difficulty"
        // "special multiplier" is what minecraft.wiki calls "Clamped regional difficulty"
        // 
        DifficultyInstance difficultyInstance = lootContext.getLevel().getCurrentDifficultyAt(BlockPos.containing(pos));
        return this.value.test(lootContext, difficultyInstance.getDifficulty().getId());
    }
}
