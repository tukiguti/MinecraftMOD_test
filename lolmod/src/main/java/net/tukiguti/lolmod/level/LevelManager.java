package net.tukiguti.lolmod.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;
import net.tukiguti.lolmod.level.LolModConfig;
import net.tukiguti.lolmod.level.PacketHandler;
import net.tukiguti.lolmod.level.SyncLevelDataPacket;
import net.tukiguti.lolmod.mana.ManaManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private int level;
    private int currentXP;
    private final Player player;

    private static final String DATA_NAME = "LolModPlayerData";
    private static final String LEVEL_KEY = "level";
    private static final String XP_KEY = "xp";

    private static LevelManager instance;

    //private static final int DEFAULT_BASE_XP = 100;
    //private static final double DEFAULT_XP_RATE = 1.1;
    //private static final int DEFAULT_XP_FROM_SKELETON = 10;

    private LevelManager(Player player) {
        this.player = player;
        load();
    }

    public static LevelManager get(Player player) {
        if (instance == null) {
            instance = new LevelManager(player);
        }
        return instance;
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
        /*if (player instanceof FakePlayer) return;
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = new CompoundTag();
        data.putInt(LEVEL_KEY, level);
        data.putInt(XP_KEY, currentXP);
        persistentData.put(DATA_NAME, data);
        LOGGER.debug("Saved player data: Level {}, XP {}", level, currentXP);*/
        PlayerDataManager.savePlayerData(player, level, currentXP);
    }

    private void load() {
        /*if (player instanceof FakePlayer) {
            level = 1;
            currentXP = 0;
            return;
        }
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(DATA_NAME)) {
            CompoundTag data = persistentData.getCompound(DATA_NAME);
            level = data.getInt(LEVEL_KEY);
            currentXP = data.getInt(XP_KEY);
        } else {
            level = 1;
            currentXP = 0;
        }
        if (level == 0) level = 1;*/
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