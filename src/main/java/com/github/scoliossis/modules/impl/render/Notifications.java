package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RenderTickEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.render.notifications.NotificationManager;
import com.github.scoliossis.utils.render.notifications.NotificationType;

@RegisterModule(
        name = "Notifications",
        description = "Provides Notifications functionality for the client.",
        category = Category.RENDER
)
public class Notifications extends Module {

    @SubscribeEvent
    public static void onRender2D(RenderTickEvent event) {
        NotificationManager.render();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        // Post a test notification when disabled? Since it's disabled, it won't render anyway!
        // So we just clear or nothing.
    }
}
