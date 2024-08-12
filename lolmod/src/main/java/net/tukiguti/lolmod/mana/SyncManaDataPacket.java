package net.tukiguti.lolmod.mana;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SyncManaDataPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int currentMana;
    private final int maxMana;

    public SyncManaDataPacket(int currentMana, int maxMana) {
        this.currentMana = currentMana;
        this.maxMana = maxMana;
    }

    public static void encode(SyncManaDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.currentMana);
        buf.writeInt(msg.maxMana);
    }

    public static SyncManaDataPacket decode(FriendlyByteBuf buf) {
        return new SyncManaDataPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(SyncManaDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ManaManager manaManager = ManaManager.get(mc.player);
                manaManager.setCurrentMana(msg.currentMana);
                manaManager.updateMaxMana();
                LOGGER.info("[CLIENT] Received mana sync packet: Current Mana {}, Max Mana {} for player {}", msg.currentMana, msg.maxMana, mc.player.getName().getString());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}