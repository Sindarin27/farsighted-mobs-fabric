package sindarin.farsightedmobs;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import sindarin.farsightedmobs.config.ModConfig;

import java.util.HashMap;

public class FarsightedMobs implements ModInitializer {
    public static ModConfig CONFIG = new ModConfig();
    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static Entity upgradeEntity(Entity e) {
        if (e instanceof LivingEntity) {
            Identifier type = EntityType.getId(e.getType());
            LivingEntity living = (LivingEntity)e;
            if (FarsightedMobs.CONFIG.followRanges.containsKey(type.toString())) {
                int range = FarsightedMobs.CONFIG.followRanges.get(type.toString());
                living.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(range);
                return living;
            }
        }
        return e;
    }
}
