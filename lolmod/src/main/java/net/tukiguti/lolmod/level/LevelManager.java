package net.tukiguti.lolmod.level;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.tukiguti.lolmod.mana.ManaManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LevelManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, LevelManager> INSTANCES = new ConcurrentHashMap<>();

    private int level;
    private int currentXP;
    private Player player;

    private LevelManager(Player player) {
        this.player = player;
        load();
    }

    public static LevelManager get(Player player) {
        return INSTANCES.compute(player.getUUID(), (uuid, existing) -> {
            if (existing == null) {
                return new LevelManager(player);
            }
            existing.player = player;
            return existing;
        });
    }

    public static void remove(Player player) {
        INSTANCES.remove(player.getUUID());
    }

    public void addXP(int amount) {
        currentXP += amount;
        while (currentXP >= getXPForNextLevel()) {
            levelUp();
        }
        save();
        LOGGER.info("Player {} XP updated. Current XP: {}, Level: {}", player.getName().getString(), currentXP, level);
        syncToClient();
    }

    private void levelUp() {
        currentXP -= getXPForNextLevel();
        level++;
        LOGGER.info("Player leveled up! New level: {}", level);

        // レベルアップ時にManaManagerを更新
        ManaManager manaManager = ManaManager.get(player);
        manaManager.updateMaxMana();

        // マナを全回復
        manaManager.setCurrentMana(manaManager.getMaxMana());
    }

    public int getXPForNextLevel() {
        int baseXP = LolModConfig.getBaseXPForLevelUp();
        double rate = LolModConfig.getXPIncreaseRate();
        int xpForNextLevel = (int) (baseXP * Math.pow(rate, level - 1));
        return xpForNextLevel;
    }

    public static int getXPFromSkeleton() {
        return LolModConfig.getXPFromSkeleton();
    }

    private void save() {
        PlayerDataManager.savePlayerData(player, level, currentXP);
    }

    private void load() {
        PlayerDataManager.PlayerData data = PlayerDataManager.loadPlayerData(player);
        level = data.level;
        currentXP = data.xp;
        if (level == 0) level = 1;
    }

    private void syncToClient() {
        if (player instanceof ServerPlayer serverPlayer && PacketHandler.INSTANCE != null) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncLevelDataPacket(level, currentXP));
            LOGGER.info("[SERVER] Sent sync packet to client for player: {}", player.getName().getString());
        }
    }

    public void setLevelData(int newLevel, int newXP) {
        this.level = newLevel;
        this.currentXP = newXP;
        LOGGER.info("Client received updated level data: Level {}, XP {}", level, currentXP);
        save();
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentXP() {
        return currentXP;
    }
}
