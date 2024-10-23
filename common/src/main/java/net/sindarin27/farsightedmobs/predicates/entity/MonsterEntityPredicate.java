package net.sindarin27.farsightedmobs.predicates.entity;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MonsterEntityPredicate implements EntitySubPredicate {
    public static final MonsterEntityPredicate INSTANCE = new MonsterEntityPredicate();
    public static final MapCodec<MonsterEntityPredicate> CODEC = MapCodec.unit(INSTANCE);
    
    private MonsterEntityPredicate() {}
    
    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
        return entity instanceof Monster;
    }
}
