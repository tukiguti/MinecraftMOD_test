package net.tukiguti.lolmod.level;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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
public class Hud extends GuiComponent {
    private static final Logger LOGGER = LogManager.getLogger();
    private static long lastErrorTime = 0;
    private static final long ERROR_COOLDOWN = 5000; // 5秒
    private static final ResourceLocation LEVEL_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation LEVEL_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");
    private static final ResourceLocation MANA_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation MANA_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        renderLevelBar(poseStack, player, width, height);
        renderManaBar(poseStack, player, width, height);
    }

    private static void renderLevelBar(PoseStack poseStack, LocalPlayer player, int width, int height) {
        int maxImageWidth = 100;
        int maxImageHeight = 5;
        int x = 5;
        int y = height - maxImageHeight - 5;

        try {
            LevelManager levelManager = LevelManager.get(player);
            int currentXP = levelManager.getCurrentXP();
            int xpForNextLevel = levelManager.getXPForNextLevel();
            int currentLevel = levelManager.getLevel();

            RenderSystem.setShaderTexture(0, LEVEL_BAR_FRAME);
            blit(poseStack, x, y, 0, 0, maxImageWidth, maxImageHeight, maxImageWidth, maxImageHeight);

            if (xpForNextLevel > 0) {
                float progress = (float) currentXP / xpForNextLevel;
                int imageWidth = (int) ((maxImageWidth - 2) * progress);

                RenderSystem.setShaderTexture(0, LEVEL_BAR);
                blit(poseStack, x + 1, y + 1, 0, 0, imageWidth, maxImageHeight - 2, maxImageWidth - 2, maxImageHeight - 2);
            }

            String levelText = "Level: " + currentLevel + " XP: " + currentXP + "/" + xpForNextLevel;
            Minecraft.getInstance().font.draw(poseStack, levelText, x + 3, y - 10, 0xFFFFFF);
        } catch (Exception e) {
            LOGGER.error("Error rendering level HUD", e);
        }
    }

    private static void renderManaBar(PoseStack poseStack, LocalPlayer player, int width, int height) {
        int maxImageWidth = 100;
        int maxImageHeight = 5;
        int x = 5;
        int y = height - maxImageHeight * 2 - 25;

        try {
            ManaManager manaManager = ManaManager.get(player);
            int currentMana = manaManager.getCurrentMana();
            int maxMana = manaManager.getMaxMana();

            RenderSystem.setShaderTexture(0, MANA_BAR_FRAME);
            blit(poseStack, x, y, 0, 0, maxImageWidth, maxImageHeight, maxImageWidth, maxImageHeight);

            float progress = (float) currentMana / maxMana;
            int imageWidth = (int) ((maxImageWidth - 2) * progress);

            RenderSystem.setShaderTexture(0, MANA_BAR);
            blit(poseStack, x + 1, y + 1, 0, 0, imageWidth, maxImageHeight - 2, maxImageWidth - 2, maxImageHeight - 2);

            String manaText = "Mana: " + currentMana + "/" + maxMana;
            Minecraft.getInstance().font.draw(poseStack, manaText, x + 3, y - 10, 0x00FFFF);
        } catch (Exception e) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastErrorTime > ERROR_COOLDOWN) {
                LOGGER.error("Error rendering mana HUD", e);
                lastErrorTime = currentTime;
            }
        }
    }
}