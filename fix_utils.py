import os
import glob

def fix_file(fpath):
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()
    
    content = content.replace("implements Utils ", "")
    content = content.replace("implements Utils", "")
    content = content.replace("import com.github.scoliossis.utils.tenacity.Utils;", "")
    content = content.replace("import com.github.scoliossis.utils.tenacity.misc.FileUtils;", "import java.io.InputStream; import java.io.InputStreamReader; import java.io.BufferedReader;")
    
    if "ShaderUtil" in fpath:
        content = content.replace("FileUtils.readInputStream", "readInputStream")
        inline_method = """
    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\\n');
        } catch (Exception e) { e.printStackTrace(); }
        return stringBuilder.toString();
    }
"""
        if "public static String readInputStream" not in content:
            content = content.replace("public class ShaderUtil {", "public class ShaderUtil {" + inline_method)
            
    with open(fpath, "w", encoding="utf-8") as f:
        f.write(content)

for f in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/render/*.java"):
    fix_file(f)

for f in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/animations/*.java"):
    fix_file(f)

for f in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/animations/impl/*.java"):
    fix_file(f)

for f in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/misc/*.java"):
    fix_file(f)

print("Fixed.")
