package net.tukiguti.lolmod.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tukiguti.lolmod.util.LevelManager;
import net.tukiguti.lolmod.config.LolModConfig;

@Mod.EventBusSubscriber(modid = "lolmod")
public class EntityEvents {

    private static final int DEFAULT_XP_FROM_SKELETON = 10;

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof Skeleton) {
            LevelManager levelManager = LevelManager.get(player);
            int xpGain = getXPFromSkeleton();
            System.out.println("Before XP gain: Level " + levelManager.getLevel() + ", XP " + levelManager.getCurrentXP());
            levelManager.addXP(xpGain);
            System.out.println("After XP gain: Level " + levelManager.getLevel() + ", XP " + levelManager.getCurrentXP());
        }
    }

    private static int getXPFromSkeleton() {
        try {
            return LolModConfig.XP_FROM_SKELETON.get();
        } catch (IllegalStateException e) {
            System.out.println("Config not loaded yet, using default XP value for skeleton kill");
            return DEFAULT_XP_FROM_SKELETON;
        }
    }
}