import os

coolware_dir = "e:/999/coolware/src/main/java/com/github/scoliossis/modules/impl"

# 1. Chest ESP
chest_esp = """package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RenderWorldEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.RenderGlobal;
import com.github.scoliossis.utils.tenacity.render.ColorUtil;
import java.awt.Color;

@RegisterModule(
        name = "Chest ESP",
        description = "Draws an outline around chests.",
        category = Category.RENDER
)
public class ChestESP extends Module {

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldEvent event) {
        for (TileEntity entity : C.w().loadedTileEntityList) {
            if (entity instanceof TileEntityChest || entity instanceof TileEntityEnderChest) {
                double x = entity.getPos().getX() - C.mc.getRenderManager().viewerPosX;
                double y = entity.getPos().getY() - C.mc.getRenderManager().viewerPosY;
                double z = entity.getPos().getZ() - C.mc.getRenderManager().viewerPosZ;

                AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                
                Color color = entity instanceof TileEntityEnderChest ? new Color(200, 0, 255) : new Color(255, 170, 0);

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glLineWidth(1.5f);
                
                ColorUtil.resetColor();
                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
                RenderGlobal.drawSelectionBoundingBox(bb);
                
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
"""
with open(os.path.join(coolware_dir, "render/ChestESP.java"), "w") as f:
    f.write(chest_esp)


# 2. TargetHUD patch: Replace the "Tenacity" mode block in TargetHUD.java
targethud_file = os.path.join(coolware_dir, "render/TargetHUD.java")
with open(targethud_file, "r") as f:
    targethud_content = f.read()

tenacity_mode_start = targethud_content.find('if (mode == TargetHUDMode.Tenacity) {')
tenacity_mode_end = targethud_content.find('return new double[]{cardW, cardH};', tenacity_mode_start)

if tenacity_mode_start != -1 and tenacity_mode_end != -1:
    new_tenacity_mode = """if (mode == TargetHUDMode.Tenacity) {
                    float cardW = Math.max(155f, FontUtil.getStringWidth(target.getName(), 22) + 75f);
                    float cardH = 50f;
                    
                    Color[] theme = com.github.scoliossis.modules.impl.client.ThemeModule.getThemeColours();
                    Color color1 = theme[0];
                    Color color2 = theme[1];
                    Color color3 = theme.length > 2 ? theme[2] : theme[0];
                    Color color4 = theme.length > 3 ? theme[3] : theme[1];
                    
                    com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawGradientRound(
                        0, 0, cardW, cardH, 6,
                        color1, color4, color2, color3
                    );
                    
                    float size = 38f;
                    if (target instanceof net.minecraft.client.entity.AbstractClientPlayer) {
                        com.github.scoliossis.utils.tenacity.render.StencilUtil.initStencilToWrite();
                        com.github.scoliossis.utils.render.RenderUtil.drawRoundedRect(10, (cardH / 2f) - (size / 2f), size, size, size/2, Color.WHITE);
                        com.github.scoliossis.utils.tenacity.render.StencilUtil.readStencilBuffer(1);
                        com.github.scoliossis.utils.tenacity.render.TRenderUtil.resetColor();
                        com.github.scoliossis.utils.tenacity.render.TRenderUtil.setAlphaLimit(0);
                        
                        net.minecraft.client.renderer.GlStateManager.color(1,1,1,1);
                        com.github.scoliossis.utils.render.RenderUtil.drawFace(10, (cardH / 2f) - (size / 2f), 8, 8, 8, 8, size, size, 64, 64, (net.minecraft.client.entity.AbstractClientPlayer) target);
                        
                        com.github.scoliossis.utils.tenacity.render.StencilUtil.uninitStencilBuffer();
                    } else {
                        FontUtil.drawStringWithShadow("?", 30, 25 - FontUtil.getFontHeight(32) / 2f, 32, Color.WHITE.getRGB());
                    }
                    
                    FontUtil.drawString(target.getName(), 10 + size + ((cardW - (10 + size)) / 2f) - (FontUtil.getStringWidth(target.getName(), 22) / 2f), 10, 22, Color.WHITE.getRGB());
                    
                    float hp = target.getHealth() + target.getAbsorptionAmount();
                    float maxHp = target.getMaxHealth() + target.getAbsorptionAmount();
                    float healthPercentage = hp / maxHp;
                    float healthBarWidth = cardW - (size + 30);
                    
                    // animated hp
                    healthAnimation.animate((double)(healthBarWidth * healthPercentage), 18);
                    
                    com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(20 + size, 25, healthBarWidth, 4, 2, new Color(0,0,0,80));
                    com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(20 + size, 25, (float)healthAnimation.getOutput(), 4, 2, Color.WHITE);
                    
                    String healthText = String.format("%.0f%% - %dm", healthPercentage * 100, Math.round(C.mc.thePlayer.getDistanceToEntity(target)));
                    FontUtil.drawString(healthText, 10 + size + ((cardW - (10 + size)) / 2f) - (FontUtil.getStringWidth(healthText, 18) / 2f), 35, 18, Color.WHITE.getRGB());
                    
                    """
    targethud_content = targethud_content[:tenacity_mode_start] + new_tenacity_mode + targethud_content[tenacity_mode_end:]
    with open(targethud_file, "w") as f:
        f.write(targethud_content)

print("TargetHUD and ChestESP done.")
