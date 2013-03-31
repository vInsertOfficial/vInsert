package org.vinsert.bot.util;

import org.vinsert.Application;
import org.vinsert.Configuration;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author iJava
 */
public class PasteScript {

    private String url;
    private String contents;
    private String name;

    public PasteScript(String url) {
        this.url = url;
        contents = download();
        name = getScriptName();
        writeFile();
        compile();
    }

    public void compile() {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return;
            }
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(Configuration.SOURCE_DIR
                    + File.separator + name + ".java"));
            JavaCompiler.CompilationTask task = compiler.getTask(null,
                    fileManager, diagnostics, null,
                    null, compilationUnits);
            boolean success = task.call();
            File output = new File(Configuration.COMPILED_DIR + File.separator + name + ".class");
            File input = new File(Configuration.SOURCE_DIR + File.separator + name + ".class");
            input.renameTo(output);
            fileManager.close();
            Application.getBotWindow().getActiveBot().getLogger().log(new LogRecord(Level.INFO, success ? "Compiled Script : " + name
            : "Compilation Error"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        try {
            File file = new File(Configuration.SOURCE_DIR + File.separator + name + ".java");
            Configuration.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(contents);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getScriptName() {
        return contents.substring(contents.indexOf("class") + 6, contents.indexOf("extends") - 1);
    }

    public String download() {
        try {
            return getSource(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSource(URL url) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream is = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }
}
