import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

old_code = 'anim.setDirection(m.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);'
new_code = 'anim.setDirection(m.isEnabled() && !m.hide ? Direction.FORWARDS : Direction.BACKWARDS);'

if old_code in code:
    code = code.replace(old_code, new_code)
    with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
        f.write(code)
    print("Replaced!")
else:
    print("Old logic not found!")
