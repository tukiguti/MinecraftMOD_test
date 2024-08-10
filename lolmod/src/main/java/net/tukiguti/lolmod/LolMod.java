package net.tukiguti.lolmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tukiguti.lolmod.config.LolModConfig;
import net.tukiguti.lolmod.event.EntityEvents;
import net.tukiguti.lolmod.network.PacketHandler;
import net.tukiguti.lolmod.command.DebugCommand;
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
        LOGGER.info("LolMod common setup starting");
        // 共通のセットアップコードをここに記述
        event.enqueueWork(() -> {
            LolModConfig.loadConfig();
            LOGGER.info("LolMod: Config initialized");
            PacketHandler.init();
            LOGGER.info("LolMod: PacketHandler initialized");
        });
        LOGGER.info("LolMod common setup completed");
    }
}