package net.tukiguti.lolmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.tukiguti.lolmod.util.LevelManager;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class SyncLevelDataPacket {
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
            // クライアント側で実行
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                LevelManager levelManager = LevelManager.get(mc.player);
                levelManager.setLevelData(msg.level, msg.xp);
                System.out.println("Client received sync packet: Level " + msg.level + ", XP " + msg.xp);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}