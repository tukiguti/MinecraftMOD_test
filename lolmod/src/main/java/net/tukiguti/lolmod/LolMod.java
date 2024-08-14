package net.tukiguti.lolmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.tukiguti.lolmod.level.LolModConfig;
import net.tukiguti.lolmod.level.EntityEvents;
import net.tukiguti.lolmod.level.PacketHandler;
import net.tukiguti.lolmod.level.PlayerDataManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;
//import net.tukiguti.lolmod.status.StatusDisplayGui;


@Mod(LolMod.MOD_ID)
public class LolMod {
    public static final String MOD_ID = "lolmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public LolMod() {
        LOGGER.info("LolMod: Initializing");

        // MOD特有のイベントバスを取得
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // コンフィグの登録
        LOGGER.info("LolMod: Registering config");
        LolModConfig.register(modEventBus);
        LOGGER.info("LolMod: Config registered");

        // MOD特有のイベントリスナーを登録
        modEventBus.addListener(this::commonSetup);

        // MODのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(this);
        // Forgeのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);

        createModDirectory();

        /*if (FMLEnvironment.dist == Dist.CLIENT) {
            LOGGER.info("LolMod: Initializing StatusDisplayGui");
            new StatusDisplayGui();
        }*/

        LOGGER.info("LolMod: Initialization completed");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.init();
            LolModConfig.loadConfig();
            PlayerDataManager.loadPlayerData(null);

            if (LolModConfig.isLoaded()) {
                LOGGER.info("Config successfully loaded during common setup");
            } else {
                LOGGER.error("Config not loaded during common setup");
            }
        });
    }
    private void createModDirectory() {
        Path modDir = FMLPaths.GAMEDIR.get().resolve("lolmod");
        try {
            Files.createDirectories(modDir);
            LOGGER.info("Created mod directory: {}", modDir);
        } catch (Exception e) {
            LOGGER.error("Failed to create mod directory: {}", modDir, e);
        }
    }
}