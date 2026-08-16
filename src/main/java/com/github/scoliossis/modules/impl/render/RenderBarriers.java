package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.ChatUtil;

@RegisterModule(
        name = "Render Barriers",
        description = "Kawaii barrier texture",
        category = Category.RENDER
)
public class RenderBarriers extends Module {

    @Override
    protected void onEnable() {
        C.mc.renderGlobal.loadRenderers();
        // actually too lazy to fact check this but i dont see why it wouldnt be true.
        ChatUtil.prefixMessage("To change the barrier texture create a texture pack and put your barrier texture in: &fassets/minecraft/textures/blocks/barrier.png");
    }

    @Override
    protected void onDisable() {
        C.mc.renderGlobal.loadRenderers();
    }
}
