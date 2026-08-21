import re

with open('src/main/java/com/github/scoliossis/modules/impl/movement/Scaffold.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = re.sub(
    r'if\s*\(\s*shouldTelly\(\)\s*&&\s*C\.p\(\)\.onGround\s*&&\s*\(tellyBlockPlaced\s*\|\|\s*tellyMode\.getTellyForwardTicks\(\)\s*==\s*0\)\s*\)\s*\{\s*tellyTicksCounter\s*=\s*0;\s*tellyPlaceDelayCounter\s*=\s*0;\s*\}\s*tellyTicksCounter\+\+;\s*if\s*\(\s*tellyTicksCounter\s*<=\s*tellyMode\.getTellyTicks\(\)\s*\)\s*return;',
    '''boolean isKeepYTelly = (keepY == KeepYMode.Telly || keepY == KeepYMode.ExtraTelly);
        if ((shouldTelly() || isKeepYTelly) && C.p().onGround && (tellyBlockPlaced || tellyMode.getTellyForwardTicks() == 0 || isKeepYTelly)) {
            tellyTicksCounter = 0;
            tellyPlaceDelayCounter = 0;
        }

        if (shouldTelly() || isKeepYTelly) {
            tellyTicksCounter++;
            if (tellyTicksCounter <= tellyMode.getTellyTicks()) return;
        }''',
    code,
    flags=re.MULTILINE | re.DOTALL
)

with open('src/main/java/com/github/scoliossis/modules/impl/movement/Scaffold.java', 'w', encoding='utf-8') as f:
    f.write(code)
