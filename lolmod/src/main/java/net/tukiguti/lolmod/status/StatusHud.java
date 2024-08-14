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
    private static final double BASE_MOVEMENT_SPEED = 0.1;

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

        int movementSpeedPercent = (int)Math.round((status.getTotalMovementSpeed() / BASE_MOVEMENT_SPEED - 1) * 100);
        String movementSpeedText = String.format("Movement Speed: +%d%%", movementSpeedPercent);
        drawString(poseStack, Minecraft.getInstance().font, movementSpeedText, x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Health: " + df.format(status.getTotalMaxHealth()), x, y, 0xFFFFFF);
        y += lineHeight;

        String healthRegenText = String.format("Health Regen: %d%%", (int)Math.round(status.getTotalHealthRegen() * 100));
        drawString(poseStack, Minecraft.getInstance().font, healthRegenText, x, y, 0xFFFFFF);
        y += lineHeight;

        String damageReductionText = String.format("Defense: %d%%", (int)Math.round(status.getTotalDamageReduction() * 100));
        drawString(poseStack, Minecraft.getInstance().font, damageReductionText, x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "Mana Regen: " + df.format(status.getTotalManaRegen()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "AD: " + df.format(status.getTotalAd()), x, y, 0xFFFFFF);
        y += lineHeight;

        drawString(poseStack, Minecraft.getInstance().font, "AP: " + df.format(status.getTotalAp()), x, y, 0xFFFFFF);
        y += lineHeight;

        String armorPenetrationText = String.format("Armor Penetration: %d%%", (int)Math.round(status.getTotalArmorPenetration() * 100));
        drawString(poseStack, Minecraft.getInstance().font, armorPenetrationText, x, y, 0xFFFFFF);
        y += lineHeight;

        String criticalChanceText = String.format("Critical Chance: %d%%", (int)Math.round(status.getTotalCritical() * 100));
        drawString(poseStack, Minecraft.getInstance().font, criticalChanceText, x, y, 0xFFFFFF);
        y += lineHeight;

        String lifeStealText = String.format("Life Steal: %d%%", (int)Math.round(status.getTotalLifeSteal() * 100));
        drawString(poseStack, Minecraft.getInstance().font, lifeStealText, x, y, 0xFFFFFF);
        y += lineHeight;

        String omniVampText = String.format("Omni Vamp: %d%%", (int)(status.getTotalOmniVamp() * 100));
        drawString(poseStack, Minecraft.getInstance().font, omniVampText, x, y, 0xFFFFFF);
        y += lineHeight;

        String tenacityText = String.format("Tenacity: %d%%", (int)(status.getTotalTenacity() * 100));
        drawString(poseStack, Minecraft.getInstance().font, tenacityText, x, y, 0xFFFFFF);
    }
}