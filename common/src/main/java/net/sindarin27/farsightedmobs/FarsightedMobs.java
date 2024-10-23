package net.sindarin27.farsightedmobs;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Mob;
import net.sindarin27.farsightedmobs.predicates.entity.MonsterEntityPredicate;
import org.slf4j.Logger;

import java.util.function.Supplier;

import static net.sindarin27.farsightedmobs.AttributeUtility.FixFollowRange;

public final class FarsightedMobs {
    public static final String MOD_ID = "farsightedmobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final AttributeRulesManager attributeRulesManager = new AttributeRulesManager();
    static final boolean DEBUG = Platform.isDevelopmentEnvironment();
    
    public static void init() {
        // Write common init code here.
        LOGGER.info("Starting initialisation for {}", MOD_ID);

        // Make the attributes rules manager listen to datapack reloads with its logical namespace. 
        // For fabric: it is dependent on all built-in (vanilla) registries
        ReloadListenerRegistry.register(PackType.SERVER_DATA, 
                attributeRulesManager,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "farsightedmobs_spawn_attributes"));
        
        Registrar<MapCodec<? extends EntitySubPredicate>> entitySubPredicates = MANAGER.get().get(Registries.ENTITY_SUB_PREDICATE_TYPE);

        //RegistrySupplier<MapCodec<? extends EntitySubPredicate>> MONSTER_PREDICATE = 
                entitySubPredicates.register(
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "monster"), 
                        () -> MonsterEntityPredicate.CODEC);
        
        EntityPredicate.Builder predicateBuilder = new EntityPredicate.Builder();
        predicateBuilder.subPredicate(MonsterEntityPredicate.INSTANCE);

        LOGGER.info("Finished initialisation for {}", MOD_ID);
//        attributeRulesManager.rules.add(new AttributeRule(predicateBuilder.build(), Attributes.FOLLOW_RANGE, 32));
    }
    
    public static void OnMobSpawn(ServerLevel level, Mob mob) {
        // Handle attribute rules
        attributeRulesManager.GetRules().forEachOrdered(rule -> {
            boolean applied = rule.Apply(level, mob);
            if (DEBUG) LOGGER.info("Rule {} with priority {} on mob {} evaluated as {}", rule.identifier, rule.getPriority(), mob.getType().getDescriptionId(), applied);
        });
        
        // Fix the minecraft bug that causes entities to never update their follow range by updating it once they spawn
        FixFollowRange(mob);
    }

   
}
