import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/render/Notifications.java', 'r', 'utf-8') as f:
    code = f.read()

code = code.replace('import com.github.scoliossis.utils.render.notifications.NotificationType;', 'import com.github.scoliossis.utils.render.notifications.NotificationType;\nimport com.github.scoliossis.modules.RegisterSubModule;')

new_field = '''public class Notifications extends Module {
    @RegisterSubModule(name = "Size", min = 0.5, max = 2.0, increment = 0.1)
    public static double size = 1.0;'''

code = code.replace('public class Notifications extends Module {', new_field)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/render/Notifications.java', 'w', 'utf-8') as f:
    f.write(code)

with codecs.open('src/main/java/com/github/scoliossis/utils/render/notifications/NotificationManager.java', 'r', 'utf-8') as f:
    code = f.read()

code = code.replace('import com.github.scoliossis.modules.impl.client.ThemeModule;', 'import com.github.scoliossis.modules.impl.client.ThemeModule;\nimport com.github.scoliossis.modules.impl.render.Notifications;')

code = code.replace('float notifHeight = PAD * 2 + FontUtil.getFontHeight(TITLE_SIZE) + FontUtil.getFontHeight(DESC_SIZE) + 4;',
                    'float effectivePad = PAD * (float)Notifications.size;\n            int effectiveTitleSize = (int)(TITLE_SIZE * Notifications.size);\n            int effectiveDescSize = (int)(DESC_SIZE * Notifications.size);\n            float effectiveWidth = NOTIF_WIDTH * (float)Notifications.size;\n            float notifHeight = effectivePad * 2 + FontUtil.getFontHeight(effectiveTitleSize) + FontUtil.getFontHeight(effectiveDescSize) + 4;')

code = code.replace('float targetX = screenWidth - NOTIF_WIDTH - 10;', 'float targetX = screenWidth - effectiveWidth - 10;')

code = code.replace('drawClayNotification(notif.getX(), notif.getY(), NOTIF_WIDTH, notifHeight, notif);',
                    'drawClayNotification(notif.getX(), notif.getY(), effectiveWidth, notifHeight, effectivePad, effectiveTitleSize, effectiveDescSize, notif);')

code = code.replace('private static void drawClayNotification(float x, float y, float w, float h, Notification notif)',
                    'private static void drawClayNotification(float x, float y, float w, float h, float effectivePad, int effectiveTitleSize, int effectiveDescSize, Notification notif)')

code = code.replace('float cursorY = y + PAD;', 'float cursorY = y + effectivePad;')
code = code.replace('FontUtil.drawString(notif.getTitle(), x + PAD, cursorY, TITLE_SIZE, accent, false);',
                    'FontUtil.drawString(notif.getTitle(), x + effectivePad, cursorY, effectiveTitleSize, accent, false);')
code = code.replace('cursorY += FontUtil.getFontHeight(TITLE_SIZE) + 4;',
                    'cursorY += FontUtil.getFontHeight(effectiveTitleSize) + 4;')
code = code.replace('FontUtil.drawString(notif.getDescription(), x + PAD, cursorY, DESC_SIZE, TEXT_MUTED, false);',
                    'FontUtil.drawString(notif.getDescription(), x + effectivePad, cursorY, effectiveDescSize, TEXT_MUTED, false);')

with codecs.open('src/main/java/com/github/scoliossis/utils/render/notifications/NotificationManager.java', 'w', 'utf-8') as f:
    f.write(code)

print('Notifications fix done')
