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
import net.tukiguti.lolmod.level.LevelManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lolmod")
@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Hud extends GuiComponent{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LEVEL_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation LEVEL_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        //画像のサイズを設定
        int maxImageWidth = 100; //横幅
        int maxImageHeight = 5;  //縦幅

        //画像を左下に配置
        int x = 5; //左端から5ピクセル右
        int y = height - maxImageHeight - 5; // 下端から5ピクセル上

        try {
            //LevelManagerからプレイヤーの現在の状態を取得
            LevelManager levelManager = LevelManager.get(player);
            int currentXP = levelManager.getCurrentXP();
            int xpForNextLevel = levelManager.getXPForNextLevel();
            int currentLevel = levelManager.getLevel();

            //LEVEL_BAR_FRAMEを表示
            RenderSystem.setShaderTexture(0, LEVEL_BAR_FRAME);
            blit(poseStack, x, y, 0, 0, maxImageWidth, maxImageHeight, maxImageWidth, maxImageHeight);


            //LEVEL_BARの横幅が変化
            if (xpForNextLevel > 0) {
                float progress = (float) currentXP / xpForNextLevel;
                int imageWidth = (int) ((maxImageWidth - 2) * progress);

                //LEVEL_BARを表示
                RenderSystem.setShaderTexture(0, LEVEL_BAR);
                blit(poseStack, x + 2, y + 1, 0, 0, imageWidth, maxImageHeight - 2, maxImageWidth - 1, maxImageHeight - 2);
            }
            // レベル表示
            String levelText = "Level: " + currentLevel + " XP: " + currentXP + "/" + xpForNextLevel;
            mc.font.draw(poseStack, levelText, x + 3, y - 10, 0xFFFFFF);
        } catch (Exception e) {
            LOGGER.error("Error rendering HUD", e);
        }
    }
}