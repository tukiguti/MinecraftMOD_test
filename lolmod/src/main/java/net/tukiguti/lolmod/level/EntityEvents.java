package net.tukiguti.lolmod.level;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tukiguti.lolmod.level.LevelManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "lolmod")
public class EntityEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof Skeleton) {
            LevelManager levelManager = LevelManager.get(player);
            int xpGain = LevelManager.getXPFromSkeleton();
            LOGGER.info("Before XP gain: Level {}, XP {}", levelManager.getLevel(), levelManager.getCurrentXP());
            levelManager.addXP(xpGain);
            LOGGER.info("After XP gain: Level {}, XP {}", levelManager.getLevel(), levelManager.getCurrentXP());
        }
    }
}