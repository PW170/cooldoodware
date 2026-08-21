import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'r', 'utf-8') as f:
    code = f.read()

ping_logic = '''
    private static long lastPingTime = 0;
    private static int cachedPing = 0;

    private static int getPing() {
        if (C.mc.isSingleplayer()) return 0;
        long now = System.currentTimeMillis();
        if (now - lastPingTime > 10000) {
            lastPingTime = now;
            if (C.mc.getNetHandler() != null && C.p() != null) {
                net.minecraft.client.network.NetworkPlayerInfo playerInfo = C.mc.getNetHandler().getPlayerInfo(C.p().getUniqueID());
                if (playerInfo != null) {
                    cachedPing = playerInfo.getResponseTime();
                }
            }
        }
        return cachedPing;
    }
'''

code = code.replace('public static double watermarkSize = 30;', 'public static double watermarkSize = 30;\n' + ping_logic)
code = code.replace('String extraText = " | " + C.mc.getDebugFPS() + "fps";', 'String extraText = " | " + C.mc.getDebugFPS() + "fps | " + getPing() + "ms";')

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
