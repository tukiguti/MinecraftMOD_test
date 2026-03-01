package net.tukiguti.lolmod.status;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class StatusHud {
    private static final double BASE_MOVEMENT_SPEED = 0.1;
    private static final ResourceLocation STATUS_LIST_TEXTURE = new ResourceLocation("lolmod", "textures/status_icon/status_list.png");
    private static final int TEXTURE_WIDTH = 52;
    private static final int TEXTURE_HEIGHT = 39;
    private static final float FONT_SCALE = 0.6f;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int height = mc.getWindow().getGuiScaledHeight();

        PlayerStatus status = PlayerStatus.get(player);
        status.updateStats();
        renderStatusHud(guiGraphics, status, height);
    }

    private static void renderStatusHud(GuiGraphics guiGraphics, PlayerStatus status, int height) {
        int x = 90; // 左端からの距離
        int y = height - TEXTURE_HEIGHT; // 下端からの距離
        guiGraphics.blit(STATUS_LIST_TEXTURE, x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);

        // ステータステキストを描画
        int textX = x + 20;
        int textY = y + 5;

        //x+右へ　y+下へ
        int adText = (int)status.getTotalAd();
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(adText),
                (int)((textX - 4) / FONT_SCALE), (int)((textY - 1) / FONT_SCALE), 0xFFFFFF);

        int armorPenetrationText = (int)(status.getTotalArmorPenetration());
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(armorPenetrationText),
                (int)((textX - 4) / FONT_SCALE), (int)((textY + 8) / FONT_SCALE), 0xFFFFFF);

        int criticalText = (int)(status.getTotalCritical());
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(criticalText),
                (int)((textX - 4) / FONT_SCALE), (int)((textY + 17) / FONT_SCALE), 0xFFFFFF);

        int maxHealthText = (int)(status.getTotalMaxHealth());
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(maxHealthText),
                (int)((textX - 4) / FONT_SCALE), (int)((textY + 26) / FONT_SCALE), 0xFFFFFF);


        int apText = (int)status.getTotalAp();
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(apText),
                (int)((textX + 22) / FONT_SCALE), (int)((textY - 1) / FONT_SCALE), 0xFFFFFF);

        String damageReductionText = String.format("%d%%", (int)Math.round(status.getTotalDamageReduction() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, damageReductionText,
                (int)((textX + 22) / FONT_SCALE), (int)((textY + 8) / FONT_SCALE), 0xFFFFFF);

        int cdText = (int)status.getTotalCd();
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(cdText),
                (int)((textX + 22) / FONT_SCALE), (int)((textY + 17) / FONT_SCALE), 0xFFFFFF);

        String movementSpeedText = String.format("%d%%", (int)Math.round((status.getTotalMovementSpeed() / BASE_MOVEMENT_SPEED - 1) * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, movementSpeedText,
                (int)((textX + 22) / FONT_SCALE), (int)((textY + 26) / FONT_SCALE), 0xFFFFFF);

        guiGraphics.pose().popPose();
    }

    /*private static void renderStatusText(GuiGraphics guiGraphics, PlayerStatus status, int x, int y) {
        int lineHeight = 10;

        guiGraphics.drawString(Minecraft.getInstance().font, "Player Status:", x, y, 0xFFFFFF);
        y += lineHeight + 2;

        int movementSpeedPercent = (int)Math.round((status.getTotalMovementSpeed() / BASE_MOVEMENT_SPEED - 1) * 100);
        String movementSpeedText = String.format("Movement Speed: +%d%%", movementSpeedPercent);
        guiGraphics.drawString(Minecraft.getInstance().font, movementSpeedText, x, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(Minecraft.getInstance().font, "Health: " + (int)status.getTotalMaxHealth(), x, y, 0xFFFFFF);
        y += lineHeight;

        String healthRegenText = String.format("Health Regen: %d%%", (int)Math.round(status.getTotalHealthRegen() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, healthRegenText, x, y, 0xFFFFFF);
        y += lineHeight;

        String damageReductionText = String.format("Defense: %d%%", (int)Math.round(status.getTotalDamageReduction() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, damageReductionText, x, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(Minecraft.getInstance().font, "Mana Regen: " + status.getTotalManaRegen(), x, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(Minecraft.getInstance().font, "AD: " + status.getTotalAd(), x, y, 0xFFFFFF);
        y += lineHeight;

        guiGraphics.drawString(Minecraft.getInstance().font, "AP: " + status.getTotalAp(), x, y, 0xFFFFFF);
        y += lineHeight;

        String armorPenetrationText = String.format("Armor Penetration: %d%%", (int)Math.round(status.getTotalArmorPenetration() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, armorPenetrationText, x, y, 0xFFFFFF);
        y += lineHeight;

        String criticalChanceText = String.format("Critical Chance: %d%%", (int)Math.round(status.getTotalCritical() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, criticalChanceText, x, y, 0xFFFFFF);
        y += lineHeight;

        String lifeStealText = String.format("Life Steal: %d%%", (int)Math.round(status.getTotalLifeSteal() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, lifeStealText, x, y, 0xFFFFFF);
        y += lineHeight;

        String omniVampText = String.format("Omni Vamp: %d%%", (int)(status.getTotalOmniVamp() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, omniVampText, x, y, 0xFFFFFF);
        y += lineHeight;

        String tenacityText = String.format("CD: %d%%", (int)(status.getTotalCd() * 100));
        guiGraphics.drawString(Minecraft.getInstance().font, tenacityText, x, y, 0xFFFFFF);
    }*/
}
