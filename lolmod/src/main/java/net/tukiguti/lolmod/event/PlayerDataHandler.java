package net.tukiguti.lolmod.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.LolMod;
import net.tukiguti.lolmod.stat.mana.ManaSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerDataHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event) {
        Player player = event.getEntity();
        player.getCapability(ManaSystem.MANA).ifPresent(mana -> {
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData = persistentData.getCompound(LolMod.MOD_ID);
            modData.put("mana", mana.serializeNBT());
            persistentData.put(LolMod.MOD_ID, modData);
            LOGGER.debug("Saved mana data for player: {}, Mana: {}/{}", player.getName().getString(), mana.getMana(), mana.getMaxMana());
        });
    }

    @SubscribeEvent
    public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
        Player player = event.getEntity();
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(LolMod.MOD_ID)) {
            CompoundTag modData = persistentData.getCompound(LolMod.MOD_ID);
            if (modData.contains("mana")) {
                player.getCapability(ManaSystem.MANA).ifPresent(mana -> {
                    mana.deserializeNBT(modData.getCompound("mana"));
                    LOGGER.debug("Loaded mana data for player: {}, Mana: {}/{}", player.getName().getString(), mana.getMana(), mana.getMaxMana());
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if(!event.isWasDeath()) {
            event.getOriginal().getCapability(ManaSystem.MANA).ifPresent(oldMana -> {
                event.getEntity().getCapability(ManaSystem.MANA).ifPresent(newMana -> {
                    newMana.deserializeNBT(oldMana.serializeNBT());
                });
            });
        }
    }
}
