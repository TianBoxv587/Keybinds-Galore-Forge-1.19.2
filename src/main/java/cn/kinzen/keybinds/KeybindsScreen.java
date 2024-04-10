package cn.kinzen.keybinds;

import cn.kinzen.keybinds.mixins.AccessorKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;


public class KeybindsScreen extends Screen{
    int timeIn = 0;
    int slotSelected = -1;
    private InputConstants.Key conflictedKey;
    final Minecraft mc;

    public KeybindsScreen() {
        super(Component.empty());
        this.mc = Minecraft.getInstance();
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {

        int x = this.width / 2;
        int y = this.height / 2;
        int maxRadius = 88;
        double angle = mouseAngle(x, y, mouseX, mouseY);
        int segments = KeybindsManager.getConflicting(this.conflictedKey).size();
        float step = 0.017453292F;
        float degPer = 6.2831855F / (float)segments;
        this.slotSelected = -1;
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        int seg;
        boolean mouseInSector;
        float radius;
        float i;
        for(seg = 0; seg < segments; ++seg) {
            mouseInSector = (double)(degPer * (float)seg) < angle && angle < (double)(degPer * (float)(seg + 1));
            radius = Math.max(0.0F, Math.min(((float)this.timeIn + delta - (float)seg * 6.0F / (float)segments) * 44.0F, (float)maxRadius));
            if (mouseInSector) {
                radius *= 1.025F;
            }

            int gs = 64;
            if (seg % 2 == 0) {
                gs += 25;
            }

            int r = gs;
            int g = gs;
            int b = gs;
            int a = 102;
            if (seg == 0) {
                buf.vertex(x, y, 0.0).color(gs, gs, gs, a).endVertex();
            }

            if (mouseInSector) {
                this.slotSelected = seg;
                b = 255;
                g = 255;
                r = 255;
            }

            for(i = 0.0F; i < degPer + step / 2.0F; i += step) {
                float rad = i + (float)seg * degPer;
                float xp = (float)x + Mth.cos(rad) * radius;
                float yp = (float)y + Mth.sin(rad) * radius;
                if (i == 0.0F) {
                    buf.vertex(xp, yp, 0.0).color(r, g, b, a).endVertex();
                }

                buf.vertex(xp, yp, 0.0).color(r, g, b, a).endVertex();
            }
        }

        tess.end();

        for(seg = 0; seg < segments; ++seg) {
            mouseInSector = (double)(degPer * (float)seg) < angle && angle < (double)(degPer * (float)(seg + 1));
            radius = Math.max(0.0F, Math.min(((float)this.timeIn + delta - (float)seg * 6.0F / (float)segments) * 32.0F, (float)maxRadius / 1.25F));
            if (mouseInSector) {
                radius *= 1.025F;
            }

            float rad = ((float)seg + 0.5F) * degPer;
            float xp = (float)x + Mth.cos(rad) * radius;
            float yp = (float)y + Mth.sin(rad) * radius;
            MutableComponent boundKey = Component.translatable(KeybindsManager.getConflicting(this.conflictedKey).get(seg).getName()).withStyle(mouseInSector? Style.EMPTY.withUnderlined(true):Style.EMPTY);
            float xsp = xp - 4.0F;
            i = yp;
            int width = boundKey.getString().length();
            if (xsp < (float)x) {
                xsp -= (float)(width - 8);
            }
            if (yp < (float)y) {
                i = yp - 9.0F;
            }


            poseStack.pushPose();
            GuiComponent.drawCenteredString(poseStack, this.font, boundKey, (int)xsp, (int)i, 16777215);
            poseStack.popPose();
        }

    }

    public void setConflictedKey(InputConstants.Key conflictedKey) {
        this.conflictedKey = conflictedKey;
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (Mth.atan2(my - y, mx - x) + 6.283185307179586) % 6.283185307179586;
    }


    @Override
    public void tick() {
        super.tick();
        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        if (!InputConstants.isKeyDown(windowHandle, this.conflictedKey.getValue())) {
            mc.setScreen(null);
            if (slotSelected != -1) {
                KeyMapping bind = KeybindsManager.getConflicting(this.conflictedKey).get(this.slotSelected);
                ((AccessorKeyMapping) bind).setDown(true);
                ((AccessorKeyMapping) bind).setClickCount(1);
            }
        }
        timeIn++;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
