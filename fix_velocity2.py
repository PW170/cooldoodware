import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/combat/Velocity.java', 'r', 'utf-8') as f:
    code = f.read()

code = re.sub(
    r'S12PacketEntityVelocity packet = \(\(S12PacketEntityVelocity\) event\.packet\);\s*if \(packet\.getEntityID\(\) != C\.p\(\)\.getEntityId\(\)\) return;',
    r'S12PacketEntityVelocity packet = ((S12PacketEntityVelocity) event.packet);\n        if (C.p() == null || packet.getEntityID() != C.p().getEntityId()) return;',
    code,
    flags=re.MULTILINE
)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/combat/Velocity.java', 'w', 'utf-8') as f:
    f.write(code)
