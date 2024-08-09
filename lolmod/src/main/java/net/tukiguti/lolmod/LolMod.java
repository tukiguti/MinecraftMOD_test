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

@Mod(LolMod.MOD_ID)
public class LolMod {
    public static final String MOD_ID = "lolmod";

    public LolMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LolModConfig.register();
        modEventBus.addListener(this::commonSetup);

        // MODのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(this);

        // Forgeのイベントバスに登録
        MinecraftForge.EVENT_BUS.register(ManaEvents.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        MinecraftForge.EVENT_BUS.register(PlayerDataHandler.class);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 共通のセットアップコードをここに記述
        event.enqueueWork(() -> {
            // セットアップ時に実行したいコードをここに記述
            PacketHandler.init();
            System.out.println("LolMod: PacketHandler initialized");
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ManaInfoCommand.register(event.getDispatcher());
    }
}