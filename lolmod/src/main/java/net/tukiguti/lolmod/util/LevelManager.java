package net.tukiguti.lolmod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;
import net.tukiguti.lolmod.config.LolModConfig;
import net.tukiguti.lolmod.network.PacketHandler;
import net.tukiguti.lolmod.network.SyncLevelDataPacket;

public class LevelManager {
    private int level;
    private int currentXP;
    private final Player player;

    private static final String DATA_NAME = "LolModPlayerData";
    private static final String LEVEL_KEY = "level";
    private static final String XP_KEY = "xp";

    private static final int DEFAULT_BASE_XP = 100;
    private static final double DEFAULT_XP_RATE = 1.1;

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
        syncToClient();
        System.out.println("Player XP updated. Current XP: " + currentXP + ", Level: " + level);
    }

    private void levelUp() {
        currentXP -= getXPForNextLevel();
        level++;
        System.out.println("Player leveled up! New level: " + level);
    }

    public int getXPForNextLevel() {
        int baseXP = DEFAULT_BASE_XP;
        double rate = DEFAULT_XP_RATE;
        try {
            baseXP = LolModConfig.BASE_XP_FOR_LEVEL_UP.get();
            rate = LolModConfig.XP_INCREASE_RATE.get();
        } catch (IllegalStateException e) {
            System.out.println("Config not loaded, using default values for XP calculation");
        }
        return (int) (baseXP * Math.pow(rate, level - 1));
    }

    private void save() {
        if (player instanceof FakePlayer) return;
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = new CompoundTag();
        data.putInt(LEVEL_KEY, level);
        data.putInt(XP_KEY, currentXP);
        persistentData.put(DATA_NAME, data);
        System.out.println("Saved player data: Level " + level + ", XP " + currentXP);
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
        System.out.println("Loaded player data: Level " + level + ", XP " + currentXP);
    }

    private void syncToClient() {
        if (player instanceof ServerPlayer serverPlayer && PacketHandler.INSTANCE != null) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncLevelDataPacket(level, currentXP));
        }
    }

    public void setLevelData(int newLevel, int newXP) {
        this.level = newLevel;
        this.currentXP = newXP;
        System.out.println("Client received updated level data: Level " + level + ", XP " + currentXP);
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentXP() {
        return currentXP;
    }
}