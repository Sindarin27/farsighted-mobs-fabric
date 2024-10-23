package net.sindarin27.farsightedmobs;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.sindarin27.farsightedmobs.predicates.entity.MonsterEntityPredicate;
import org.apache.commons.codec.language.bm.Rule;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.sindarin27.farsightedmobs.AttributeUtility.ChangeBaseAttributeValue;
import static net.sindarin27.farsightedmobs.AttributeUtility.FixFollowRange;

public final class FarsightedMobs {
    public static final String MOD_ID = "farsightedmobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final double defaultHostileRange = 32;
    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final AttributeRulesManager attributeRulesManager = new AttributeRulesManager();
    
    public static void init() {
        // Write common init code here.


        // Make the attributes rules manager listen to datapack reloads with its logical namespace. 
        // For fabric: it is dependent on all built-in (vanilla) registries
        ReloadListenerRegistry.register(PackType.SERVER_DATA, 
                attributeRulesManager,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "farsightedmobs_spawn_attributes"));
        
        Registrar<MapCodec<? extends EntitySubPredicate>> entitySubPredicates = MANAGER.get().get(Registries.ENTITY_SUB_PREDICATE_TYPE);

        RegistrySupplier<MapCodec<? extends EntitySubPredicate>> MONSTER_PREDICATE = 
                entitySubPredicates.register(
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "monster"), 
                        () -> MonsterEntityPredicate.CODEC);
        
        EntityPredicate.Builder predicateBuilder = new EntityPredicate.Builder();
        predicateBuilder.subPredicate(MonsterEntityPredicate.INSTANCE);
//        attributeRulesManager.rules.add(new AttributeRule(predicateBuilder.build(), Attributes.FOLLOW_RANGE, 32));
    }
    
    public static void OnMobSpawn(ServerLevel level, Mob mob) {
        // If monster, update the follow range
//        if (mob instanceof Monster monster) {
//            // But only when the new value is bigger than the old
//            double originalFollow = monster.getAttributeBaseValue(Attributes.FOLLOW_RANGE);
//            if (originalFollow < defaultHostileRange) {
//                ChangeBaseAttributeValue(monster, Attributes.FOLLOW_RANGE, defaultHostileRange);
//            }
//        }
        
        ;
        attributeRulesManager.GetRules().forEachOrdered(rule -> rule.Apply(level, mob));
        
        // Fix the minecraft bug that causes entities to never update their follow range by updating it once they spawn
        FixFollowRange(mob);
    }

   
}
