package net.tukiguti.lolmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tukiguti.lolmod.level.LolModConfig;
import net.tukiguti.lolmod.level.EntityEvents;
import net.tukiguti.lolmod.level.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

        LOGGER.info("LolMod: Initialization completed");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.init();
            LolModConfig.loadConfig();

            if (LolModConfig.isLoaded()) {
                LOGGER.info("Config successfully loaded during common setup");
            } else {
                LOGGER.error("Config not loaded during common setup");
            }
        });
    }
}