package net.tukiguti.lolmod.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.LolMod;
import net.tukiguti.lolmod.stat.mana.ManaSystem;
import net.tukiguti.lolmod.stat.mana.capability.ManaProvider;

public class PlayerEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(ManaSystem.MANA).isPresent()) {
                event.addCapability(new ResourceLocation(LolMod.MOD_ID, "mana"), new ManaProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLevelUp(PlayerXpEvent.LevelChange event) {
        Player player = event.getEntity();
        player.getCapability(ManaSystem.MANA).ifPresent(mana -> {
            mana.updateMaxMana(player);
        });
    }
}