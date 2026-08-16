package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.bridge.net.minecraft.client.model.ModelBoxBridge;
import com.github.scoliossis.bridge.net.minecraft.client.model.ModelRendererBridge;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RenderWorldEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.minecraft.TargetUtil;
import com.github.scoliossis.utils.render.Render3dUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@RegisterModule(
        name = "ESP",
        description = "I do have are a very particular set of skills. Skills I have acquired over a very long career. Skills that make me a nightmare for people like you.",
        category = Category.RENDER
)
public class ESP extends Module {
    @RegisterSubModule(name = "Health Bar")
    public static boolean healthBar = true;
    @RegisterSubModule(name = "Chams")
    public static boolean chams = false;

    @RegisterSubModule(name = "Square")
    public static boolean square = false;

    @RegisterSubModule(name = "Square Colour", parent = "Square")
    public static Color squareColour = new Color(232, 205, 205);

    @RegisterSubModule(name = "Outline")
    public static boolean outline = true;
    @RegisterSubModule(name = "Outline Mode", parent = "Outline")
    public static OutlineMode outlineMode = OutlineMode.Solid;
    public enum OutlineMode {
        Solid,
        Line,
    }
    @RegisterSubModule(name = "Outline Size", min = 0.75, max = 1.25, parent = "Outline Mode", modeParentString = "Solid")
    public static float outlineSize = 1.1f;
    @RegisterSubModule(name = "Outline Width", min = 1, max = 10, parent = "Outline Mode", modeParentString = "Line")
    public static int outlineWidth = 3;
    @RegisterSubModule(name = "Outline Colour", parent = "Outline")
    public static Color outlineColour = new Color(255, 132, 132);

    @SubscribeEvent
    public static void onRenderWorldEvent(RenderWorldEvent event) {
        for (EntityLivingBase entity : TargetUtil.getAllValidTargets(true)) {
            if (square) renderSquare(entity, event.partialTicks);
            if (healthBar) renderHealthBar(entity, event.partialTicks);
        }
    }

    // todo: cool colours !!! also this sucks ass, figure out shaders or however normal people do outlines.
    // called by com.github.scoliossis.mixins.net.minecraft.client.model.ModelPlayerMixin.render
    public static void renderOutline(ModelRenderer modelRenderer, float scale) {
        if (modelRenderer.isHidden || !modelRenderer.showModel) return;

        ModelRendererBridge modelRendererBridge = ModelRendererBridge.from(modelRenderer);
        if (!modelRendererBridge.bridge$compiled()) {
            modelRendererBridge.bridge$compileDisplayList(scale);
        }

        RenderUtil.beginRender();
        GlStateManager.disableLighting();
        GL11.glLineWidth(outlineWidth);

        // minecraft code for rendering bodies
        GlStateManager.pushMatrix();
        GlStateManager.translate(modelRenderer.offsetX, modelRenderer.offsetY, modelRenderer.offsetZ);
        GlStateManager.translate(modelRenderer.rotationPointX * scale, modelRenderer.rotationPointY * 2 * scale, modelRenderer.rotationPointZ * scale);

        if (outlineMode == OutlineMode.Solid) scale *= outlineSize;
        GlStateManager.translate(0, -modelRenderer.rotationPointY * scale, 0);

        GlStateManager.rotate(modelRenderer.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(modelRenderer.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(modelRenderer.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);

        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

        for (ModelBox box : modelRenderer.cubeList) {
            for (TexturedQuad quad : ModelBoxBridge.from(box).bridge$quadList()) {
                renderer.begin(outlineMode == OutlineMode.Line ? GL11.GL_LINE_LOOP : GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i < 4; ++i) {
                    PositionTextureVertex positiontexturevertex = quad.vertexPositions[i];
                    Render3dUtil.add3DVertexColor(positiontexturevertex.vector3D.xCoord * (double) scale, positiontexturevertex.vector3D.yCoord * (double) scale, positiontexturevertex.vector3D.zCoord * (double) scale, outlineColour);
                }

                RenderUtil.getTessalator().draw();
            }
        }

        GlStateManager.popMatrix();
        // swag

        RenderUtil.resetRender();
        GlStateManager.enableLighting();
        GL11.glLineWidth(1);
    }

    private static void renderSquare(EntityLivingBase entity, float partialTicks) {
        GL11.glPushMatrix();
        RenderUtil.glTranslate(Render3dUtil.getRelativeEntityPos(entity, partialTicks));
        Render3dUtil.rotateToPlayer(false);

        RenderUtil.drawRectOutline(-0.5, 0, 1, entity.height, 1, squareColour);

        GL11.glPopMatrix();
    }

    private static void renderHealthBar(EntityLivingBase entity, float partialTicks) {
        GL11.glPushMatrix();
        RenderUtil.glTranslate(Render3dUtil.getRelativeEntityPos(entity, partialTicks));
        Render3dUtil.rotateToPlayer(false);

        float healthPercent = Math.min(entity.getHealth() / entity.getMaxHealth(), 1);
        float extraHealthPercent = TargetUtil.getAbsorption(entity) / entity.getMaxHealth();

        Color backgroundColour = new Color(22, 22, 22);
        Color healthBarColour = RenderUtil.getProgressColour(healthPercent);
        Color absorptionColour = new Color(255, 255, 0);

        float backgroundWidth = 0.1f;
        float backgroundHeight = entity.height;

        float healthBarIndent = 0.01f;
        float healthBarWidth = backgroundWidth - healthBarIndent * 2;
        float healthBarHeight = (backgroundHeight - healthBarIndent * 2) * healthPercent;

        float absorptionBarHeight = (backgroundHeight - healthBarIndent * 2) * Math.min(extraHealthPercent, 1);
        
        RenderUtil.drawRect(entity.width, 0, backgroundWidth, backgroundHeight, backgroundColour);
        RenderUtil.drawRect(entity.width+healthBarIndent, healthBarIndent, healthBarWidth, healthBarHeight, healthBarColour);
        RenderUtil.drawRect(entity.width+healthBarIndent, backgroundHeight-healthBarIndent-absorptionBarHeight, healthBarWidth, absorptionBarHeight, absorptionColour);

        GL11.glPopMatrix();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
