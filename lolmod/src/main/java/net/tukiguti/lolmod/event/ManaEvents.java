package net.tukiguti.lolmod.event;


import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.stat.mana.ManaSystem;

public class ManaEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide) {
            Player player = event.player;
            player.getCapability(ManaSystem.MANA).ifPresent(mana -> {
                if (player.tickCount % 20 == 0) {  // 1秒ごとに回復
                    int regenAmount = calculateManaRegeneration(player);
                    mana.regenerateMana(regenAmount);
                }
            });
        }
    }

    private static int calculateManaRegeneration(Player player) {
        int baseRegen = 1;  //基本回復量
        int levelRegen = player.experienceLevel / 10; //レベルに基づく追加回復量
        int itemRegen = 0; //アイテムによる追加回復量量
        return baseRegen + levelRegen + itemRegen;
    }
}