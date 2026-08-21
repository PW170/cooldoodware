import os
import glob

for fpath in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/**/*.java", recursive=True):
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()
        
    content = content.replace("GLUtil.startBlend()", "net.minecraft.client.renderer.GlStateManager.enableBlend(); net.minecraft.client.renderer.GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA)")
    content = content.replace("GLUtil.endBlend()", "net.minecraft.client.renderer.GlStateManager.disableBlend()")
    content = content.replace("MathHelper.deg2Rad", "(float)(Math.PI / 180.0)")
    
    if "ShaderUtil.java" in fpath:
        content = content.replace("mc.getResourceManager", "net.minecraft.client.Minecraft.getMinecraft().getResourceManager")
        
    with open(fpath, "w", encoding="utf-8") as f:
        f.write(content)

print("Fixed final issues.")
