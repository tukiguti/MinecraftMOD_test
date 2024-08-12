package net.tukiguti.lolmod.level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DATA_FILE_NAME = "lolmod_player_data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private static String currentWorldName = "";

    public static void savePlayerData(Player player, int level, int xp) {
        String worldName = getWorldName(player.getLevel());

        if (!worldName.equals(currentWorldName)) {
            loadFromFile(worldName);
            currentWorldName = worldName;  // 保存前にcurrentWorldNameを更新
        }

        UUID playerId = player.getUUID();
        playerDataMap.put(playerId, new PlayerData(level, xp));
        LOGGER.debug("Saving player data for {} in world {}: Level {}, XP {}",
                player.getName().getString(), worldName, level, xp);
        saveToFile(worldName);
    }

    public static PlayerData loadPlayerData(Player player) {
        String worldName = getWorldName(player.getLevel());

        // 新しいワールド名に切り替えたときにのみファイルをロードする
        if (!worldName.equals(currentWorldName)) {
            loadFromFile(worldName);
            currentWorldName = worldName;  // currentWorldName を更新する
        }

        UUID playerId = player.getUUID();
        PlayerData data = playerDataMap.getOrDefault(playerId, new PlayerData(1, 0));
        LOGGER.debug("Loaded player data for {} in world {}: Level {}, XP {}",
                player.getName().getString(), worldName, data.level, data.xp);
        return data;
    }

    private static void saveToFile(String worldName) {
        Path dataFile = getDataFilePath(worldName);
        try {
            Files.createDirectories(dataFile.getParent());
            try (Writer writer = Files.newBufferedWriter(dataFile)) {
                GSON.toJson(playerDataMap, writer);
                LOGGER.info("Player data saved to file successfully for world {}: {}", worldName, dataFile);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save player data to file for world {}: {}", worldName, dataFile, e);
        }
    }

    private static void loadFromFile(String worldName) {
        currentWorldName = worldName;  // ここでワールド名を更新
        LOGGER.debug("Loading player data for world: {}", worldName);

        Path dataFile = getDataFilePath(worldName);
        playerDataMap.clear(); // Always clear the map before loading

        if (!Files.exists(dataFile)) {
            LOGGER.info("Player data file not found for world {}: {}. Starting with empty data.", worldName, dataFile);
            return;
        }

        try (Reader reader = Files.newBufferedReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>(){}.getType();
            Map<UUID, PlayerData> loadedData = GSON.fromJson(reader, type);
            if (loadedData != null) {
                playerDataMap = loadedData;
                LOGGER.info("Player data loaded from file successfully for world {}: {}", worldName, dataFile);
            } else {
                LOGGER.warn("Loaded player data is null for world {}, using empty map", worldName);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load player data from file for world {}: {}. Starting with empty data.", worldName, dataFile, e);
        }
    }

    private static String getWorldName(Level level) {
        String worldName;
        if (level.isClientSide()) {
            worldName = "client";
        } else if (level instanceof ServerLevel serverLevel) {
            worldName = serverLevel.getServer().getWorldData().getLevelName();
        } else {
            worldName = "unknown";
        }
        LOGGER.debug("World name determined as: {}", worldName);
        return worldName;
    }

    private static Path getDataFilePath(String worldName) {
        return FMLPaths.GAMEDIR.get()
                .resolve("lolmod")
                .resolve("worlds")
                .resolve(worldName)
                .resolve(DATA_FILE_NAME);
    }

    public static class PlayerData {
        public int level;
        public int xp;

        public PlayerData(int level, int xp) {
            this.level = level;
            this.xp = xp;
        }
    }
}