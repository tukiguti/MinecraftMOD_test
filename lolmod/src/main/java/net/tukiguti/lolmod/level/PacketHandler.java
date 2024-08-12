package net.tukiguti.lolmod.level;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tukiguti.lolmod.LolMod;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(LolMod.MOD_ID, "main"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
            registerMessages();
        }
    }

    private static void registerMessages() {
        int id = 0;
        INSTANCE.registerMessage(id++, SyncLevelDataPacket.class, SyncLevelDataPacket::encode, SyncLevelDataPacket::decode, SyncLevelDataPacket::handle);
    }
}