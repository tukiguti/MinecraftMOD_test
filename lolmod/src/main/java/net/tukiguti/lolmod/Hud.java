package net.tukiguti.lolmod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod("lolmod")
@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Hud {
    private static final ResourceLocation LEVEL_BAR_FRAME = new ResourceLocation("lolmod", "textures/bar/level_bar_frame.png");
    private static final ResourceLocation LEVEL_BAR = new ResourceLocation("lolmod", "textures/bar/level_bar.png");

    public Hud() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        //LEVEL_BAR_FRAME設定
        //画像のサイズを設定
        int maximageWidth = 100; //横幅
        int maximageHeight = 5;  //縦幅

        //画像を左下に配置
        int x = 5; //左端から5ピクセル右
        int y = height - maximageHeight - 5; // 下端から5ピクセル上

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //LEVEL_BAR_FRAMEを表示
        RenderSystem.setShaderTexture(0, LEVEL_BAR_FRAME);
        GuiComponent.blit(poseStack, x, y, 0, 0, maximageWidth, maximageHeight, maximageWidth, maximageHeight);


        //LEVEL_BAR設定
        //プレイヤーの現在の経験値を取得
        float xpProgress = mc.player.experienceProgress; //経験値の進行状況（0.0～1.0）

        //経験値に応じて画像の横幅が変化
        int imageWidth = (int) ((maximageWidth - 2) * xpProgress);

        //LEVEL_BARを表示
        RenderSystem.setShaderTexture(0, LEVEL_BAR);
        GuiComponent.blit(poseStack, x+2, y+1, 0, 0, imageWidth, maximageHeight-2, maximageWidth-1, maximageHeight-2);

        RenderSystem.disableBlend();
    }
}