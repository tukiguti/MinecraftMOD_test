package net.tukiguti.lolmod.status;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class StatusHud extends GuiComponent {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        PlayerStatus status = new PlayerStatus(player);
        renderStatusHud(poseStack, status, width, height);
    }

    private static void renderStatusHud(PoseStack poseStack, PlayerStatus status, int width, int height) {
        int x = 5;
        int y = 5;
        int lineHeight = 10;

        drawString(poseStack, Minecraft.getInstance().font, "Player Status:", x, y, 0xFFFFFF);
        y += lineHeight + 2;

        drawString(poseStack, Minecraft.getInstance().font, "Movement Speed: " + df.format(status.getMovementSpeed()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Max Health: " + df.format(status.getMaxHealth()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Health Regen: " + df.format(status.getHealthRegen()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Mana Regen: " + df.format(status.getManaRegen()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Damage: " + df.format(status.getDamage()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Defense: " + df.format(status.getDefense()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Armor Penetration: " + df.format(status.getArmorPenetration()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Critical Chance: " + df.format(status.getCriticalChance() * 100) + "%", x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Life Steal: " + df.format(status.getLifeSteal() * 100) + "%", x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Omni Vamp: " + df.format(status.getOmniVamp() * 100) + "%", x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Tenacity: " + df.format(status.getTenacity() * 100) + "%", x, y, 0xFFFFFF);
    }
}