package net.tukiguti.lolmod.status;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.status.PlayerStatus;
import net.tukiguti.lolmod.LolMod;

public class StatusDisplayGui extends GuiComponent {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LolMod.MOD_ID, "textures/status_icon/AD.png");
    private static final int ICON_SIZE = 16;
    private static final int SPACING = 2;

    public StatusDisplayGui() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuiDrawPost(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            renderStatusDisplay(event.getPoseStack(), event.getMouseX(), event.getMouseY());
        }
    }

    private void renderStatusDisplay(PoseStack poseStack, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int startX = 10;
        int startY = screenHeight - 90;

        PlayerStatus playerStatus = PlayerStatus.get(minecraft.player);

        RenderSystem.setShaderTexture(0, TEXTURE);

        // Render icons and values
        renderIconAndValue(poseStack, startX, startY, 0, 0, playerStatus.getTotalAd());
        renderIconAndValue(poseStack, startX, startY + ICON_SIZE + SPACING, 0, 1, playerStatus.getTotalAp());
        renderIconAndValue(poseStack, startX, startY + (ICON_SIZE + SPACING) * 2, 0, 2, playerStatus.getTotalArmorPenetration());
        renderIconAndValue(poseStack, startX, startY + (ICON_SIZE + SPACING) * 3, 0, 3, playerStatus.getTotalCritical());
    }

    private void renderIconAndValue(PoseStack poseStack, int x, int y, int iconX, int iconY, double value) {
        // Render icon
        blit(poseStack, x, y, iconX * ICON_SIZE, iconY * ICON_SIZE, ICON_SIZE, ICON_SIZE);

        // Render value
        String valueText = String.format("%.2f", value);
        Minecraft.getInstance().font.draw(poseStack, valueText, x + ICON_SIZE + SPACING, y + 4, 0xFFFFFF);
    }
}