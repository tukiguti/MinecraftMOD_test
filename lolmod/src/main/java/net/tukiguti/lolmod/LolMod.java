package net.tukiguti.lolmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tukiguti.lolmod.command.ManaInfoCommand;
import net.tukiguti.lolmod.event.ManaEvents;
import net.tukiguti.lolmod.event.PlayerEvents;
import net.tukiguti.lolmod.event.PlayerDataHandler;
import net.tukiguti.lolmod.config.LolModConfig;
import net.tukiguti.lolmod.event.EntityEvents;
import net.tukiguti.lolmod.network.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LolMod.MOD_ID)
public class LolMod {
    public static final String MOD_ID = "lolmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public LolMod() {
        LOGGER.info("Initializing LolMod");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        LolModConfig.register(modEventBus);
        LOGGER.info("LolMod config registered");
        modEventBus.addListener(this::commonSetup);

        // MODのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(this);

        // Forgeのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(ManaEvents.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        MinecraftForge.EVENT_BUS.register(PlayerDataHandler.class);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);

        LOGGER.info("LolMod initialization completed");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("LolMod common setup starting");
        // 共通のセットアップコードをここに記述
        event.enqueueWork(() -> {
            // セットアップ時に実行したいコードをここに記述
            PacketHandler.init();
            LOGGER.info("LolMod: PacketHandler initialized");
        });
        LOGGER.info("LolMod common setup completed");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ManaInfoCommand.register(event.getDispatcher());
    }
}