package net.tukiguti.lolmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;
import net.tukiguti.lolmod.config.LolModConfig;
import net.tukiguti.lolmod.network.PacketHandler;
import net.tukiguti.lolmod.network.SyncLevelDataPacket;
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

    //private static final int DEFAULT_BASE_XP = 100;
    //private static final double DEFAULT_XP_RATE = 1.1;

    private LevelManager(Player player) {
        this.player = player;
        load();
    }

    public static LevelManager get(Player player) {
        return new LevelManager(player);
    }

    public void addXP(int amount) {
        currentXP += amount;
        while (currentXP >= getXPForNextLevel()) {
            levelUp();
        }
        save();
        LOGGER.info("Player XP updated. Current XP: {}, Level: {}", currentXP, level);
    }

    private void levelUp() {
        currentXP -= getXPForNextLevel();
        level++;
        LOGGER.info("Player leveled up! New level: {}", level);
    }

    /*public int getXPForNextLevel() {
        int baseXP = DEFAULT_BASE_XP;
        double rate = DEFAULT_XP_RATE;
        try {
            baseXP = LolModConfig.BASE_XP_FOR_LEVEL_UP.get();
            rate = LolModConfig.XP_INCREASE_RATE.get();
        } catch (IllegalStateException e) {
            LOGGER.warn("Config not loaded, using default values for XP calculation");
        }
        return (int) (baseXP * Math.pow(rate, level - 1));
    }*/
    public int getXPForNextLevel() {
        if (!LolModConfig.isLoaded()) {
            LOGGER.warn("Config not loaded, using default values for XP calculation");
            return 100; // デフォルト値
        }
        int baseXP = LolModConfig.BASE_XP_FOR_LEVEL_UP.get();
        double rate = LolModConfig.XP_INCREASE_RATE.get();
        return (int) (baseXP * Math.pow(rate, level - 1));
    }

    private void save() {
        if (player instanceof FakePlayer) return;
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = new CompoundTag();
        data.putInt(LEVEL_KEY, level);
        data.putInt(XP_KEY, currentXP);
        persistentData.put(DATA_NAME, data);
        LOGGER.debug("Saved player data: Level {}, XP {}", level, currentXP);
    }

    private void load() {
        if (player instanceof FakePlayer) {
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
        if (level == 0) level = 1;
        LOGGER.debug("Loaded player data: Level {}, XP {}", level, currentXP);
    }

    private void syncToClient() {
        if (player instanceof ServerPlayer serverPlayer && PacketHandler.INSTANCE != null) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncLevelDataPacket(level, currentXP));
        }
    }

    public void setLevelData(int newLevel, int newXP) {
        this.level = newLevel;
        this.currentXP = newXP;
        LOGGER.info("Client received updated level data: Level {}, XP {}", level, currentXP);
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentXP() {
        return currentXP;
    }
}