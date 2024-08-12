package net.tukiguti.lolmod.level;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        LevelManager levelManager = LevelManager.get(player);
        LOGGER.info("Player {} logged in. Level: {}, XP: {}",
                player.getName().getString(), levelManager.getLevel(), levelManager.getCurrentXP());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        LevelManager levelManager = LevelManager.get(player);
        levelManager.setLevelData(levelManager.getLevel(), levelManager.getCurrentXP());
        LOGGER.info("Player {} logged out. Final Level: {}, XP: {}",
                player.getName().getString(), levelManager.getLevel(), levelManager.getCurrentXP());
    }
}