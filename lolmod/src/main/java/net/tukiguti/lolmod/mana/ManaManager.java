package net.tukiguti.lolmod.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.tukiguti.lolmod.level.PacketHandler;
import net.tukiguti.lolmod.level.LevelManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManaManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MANA_TAG = "currentMana";
    private static final String MAX_MANA_TAG = "maxMana";

    private int currentMana;
    private int maxMana;
    private final Player player;

    public ManaManager(Player player) {
        this.player = player;
        updateMaxMana();
        this.currentMana = this.maxMana;
        load();
    }

    // 静的なgetメソッドを追加
    public static ManaManager get(Player player) {
        return player.getCapability(ModCapabilities.MANA)
                .orElseGet(() -> {
                    ManaManager newManager = new ManaManager(player);
                    player.getCapability(ModCapabilities.MANA).ifPresent(cap -> cap = newManager);
                    return newManager;
                });
    }

    public void updateMaxMana() {
        LevelManager levelManager = LevelManager.get(player);
        this.maxMana = 100 + (levelManager.getLevel() * 10);
        this.currentMana = Math.min(this.currentMana, this.maxMana);
        save();
        syncToClient();
        //LOGGER.debug("Max mana updated for player {}. New max mana: {}", player.getName().getString(), maxMana);
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setCurrentMana(int mana) {
        this.currentMana = Math.min(mana, maxMana);
        save();
        syncToClient();
    }

    public void addMana(int amount) {
        this.currentMana = Math.min(this.currentMana + amount, maxMana);
        save();
        syncToClient();
    }

    public boolean useMana(int amount) {
        if (this.currentMana >= amount) {
            this.currentMana -= amount;
            save();
            syncToClient();
            return true;
        }
        return false;
    }

    public void setSyncedMana(int currentMana, int maxMana) {
        this.maxMana = maxMana;
        this.currentMana = Math.min(currentMana, maxMana);
    }

    private void save() {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = new CompoundTag();
        data.putInt(MANA_TAG, currentMana);
        data.putInt(MAX_MANA_TAG, maxMana);
        persistentData.put("LolModManaData", data);
        LOGGER.debug("Saved mana data for player {}: Current Mana {}, Max Mana {}", player.getName().getString(), currentMana, maxMana);
    }

    private void load() {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains("LolModManaData")) {
            CompoundTag data = persistentData.getCompound("LolModManaData");
            currentMana = data.getInt(MANA_TAG);
            maxMana = data.getInt(MAX_MANA_TAG);
        } else {
            currentMana = maxMana;
        }
        //LOGGER.debug("Loaded mana data for player {}: Current Mana {}, Max Mana {}", player.getName().getString(), currentMana, maxMana);
    }

    private void syncToClient() {
        if (player instanceof ServerPlayer serverPlayer && PacketHandler.INSTANCE != null) {
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncManaDataPacket(currentMana, maxMana)
            );
        }
    }
}
