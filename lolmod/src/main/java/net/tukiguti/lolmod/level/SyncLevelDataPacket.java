package net.tukiguti.lolmod.level;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SyncLevelDataPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int level;
    private final int xp;

    public SyncLevelDataPacket(int level, int xp) {
        this.level = level;
        this.xp = xp;
    }

    public static void encode(SyncLevelDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.level);
        buf.writeInt(msg.xp);
    }

    public static SyncLevelDataPacket decode(FriendlyByteBuf buf) {
        return new SyncLevelDataPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(SyncLevelDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                LevelManager levelManager = LevelManager.get(player);
                levelManager.setLevelData(msg.level, msg.xp);
                LOGGER.info("[CLIENT] Received sync packet: Level {}, XP {} for player {}", msg.level, msg.xp, player.getName().getString());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}