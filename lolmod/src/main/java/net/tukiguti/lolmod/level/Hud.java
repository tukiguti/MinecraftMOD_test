package net.tukiguti.lolmod.level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tukiguti.lolmod.mana.ManaManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lolmod")
@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Hud {
    private static final Logger LOGGER = LogManager.getLogger();
    private static long lastErrorTime = 0;
    private static final long ERROR_COOLDOWN = 5000; // 5秒
    private static final ResourceLocation LEVEL_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation LEVEL_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");
    private static final ResourceLocation MANA_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation MANA_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");
    private static final float FONT_SCALE = 0.7f;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        renderLevelBar(guiGraphics, player, width, height);
        renderManaBar(guiGraphics, player, width, height);
    }

    private static void renderLevelBar(GuiGraphics guiGraphics, LocalPlayer player, int width, int height) {
        int maxImageWidth = 80;
        int maxImageHeight = 5;
        int x = 5;
        int y = height - maxImageHeight - 5;

        try {
            LevelManager levelManager = LevelManager.get(player);
            int currentXP = levelManager.getCurrentXP();
            int xpForNextLevel = levelManager.getXPForNextLevel();
            int currentLevel = levelManager.getLevel();

            guiGraphics.blit(LEVEL_BAR_FRAME, x, y, 0, 0, maxImageWidth, maxImageHeight, maxImageWidth, maxImageHeight);

            if (xpForNextLevel > 0) {
                float progress = (float) currentXP / xpForNextLevel;
                int imageWidth = (int) ((maxImageWidth - 2) * progress);

                guiGraphics.blit(LEVEL_BAR, x + 1, y + 1, 0, 0, imageWidth, maxImageHeight - 2, maxImageWidth - 2, maxImageHeight - 2);
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);

            String levelText = "Level: " + currentLevel + " XP: " + currentXP + "/" + xpForNextLevel;
            guiGraphics.drawString(Minecraft.getInstance().font, levelText, (int)((x + 3) / FONT_SCALE), (int)((y - 8) / FONT_SCALE), 0xFFFFFF);

            guiGraphics.pose().popPose();
        } catch (Exception e) {
            LOGGER.error("Error rendering level HUD", e);
        }
    }

    private static void renderManaBar(GuiGraphics guiGraphics, LocalPlayer player, int width, int height) {
        int maxImageWidth = 80;
        int maxImageHeight = 5;
        int x = 5;
        int y = height - maxImageHeight * 2 - 18;

        try {
            ManaManager manaManager = ManaManager.get(player);
            int currentMana = manaManager.getCurrentMana();
            int maxMana = manaManager.getMaxMana();
            if (maxMana <= 0) {
                return;
            }

            guiGraphics.blit(MANA_BAR_FRAME, x, y, 0, 0, maxImageWidth, maxImageHeight, maxImageWidth, maxImageHeight);

            float progress = (float) currentMana / maxMana;
            int imageWidth = (int) ((maxImageWidth - 2) * progress);

            guiGraphics.blit(MANA_BAR, x + 1, y + 1, 0, 0, imageWidth, maxImageHeight - 2, maxImageWidth - 2, maxImageHeight - 2);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);

            String manaText = "Mana: " + currentMana + "/" + maxMana;
            guiGraphics.drawString(Minecraft.getInstance().font, manaText, (int)((x + 14) / FONT_SCALE), (int)((y - 8) / FONT_SCALE), 0x00FFFF);

            guiGraphics.pose().popPose();
        } catch (Exception e) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastErrorTime > ERROR_COOLDOWN) {
                LOGGER.error("Error rendering mana HUD", e);
                lastErrorTime = currentTime;
            }
        }
    }
}
