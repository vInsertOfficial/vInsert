package org.vinsert.bot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.vinsert.bot.script.ScriptInfo;
import org.vinsert.bot.script.ScriptManifest;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.generic.ClassGen;

public class ScriptClassLoader {

    public static void load(final List<ScriptInfo> scripts, final File file) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        final JarFile jar = new JarFile(file);
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry e = entries.nextElement();
            final String name = e.getName().replace('/', '.').replace('\\', '.');
            if (name.endsWith(".class")) {
                final ClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
                final Class<?> clazz = loader.loadClass(name.substring(0, name.length() - 6));
                if (clazz != null) {
                    if (clazz.isAnnotationPresent(ScriptManifest.class)) {
                        final ScriptManifest manifest = clazz.getAnnotation(ScriptManifest.class);
                        scripts.add(new ScriptInfo(manifest.name(), manifest.description(), clazz, manifest.authors(), manifest.version(), manifest.type()));
                    }
                }
            }
        }
    }

    private static void putClasses(final List<File> classes, final File directory) {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                putClasses(classes, file);
            } else if (file.toString().endsWith(".class")) {
                classes.add(file);
            }
        }
    }

    private static URL getDirectory(final File file) throws MalformedURLException {
        return new URL(file.toURI().toURL().toString().substring(0, file.toURI().toURL().toString().lastIndexOf('/')) + '/');
    }

    public static void loadLocal(final List<ScriptInfo> scripts, final File directory) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, IOException {
        final List<File> classes = new ArrayList<File>();
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                putClasses(classes, file);
            } else if (file.toString().endsWith(".class")) {
                classes.add(file);
            }
        }
        for (final File file : classes) {
            final ClassGen cg = new ClassGen(new ClassParser(file.toURI().toURL().openStream(), file.getName()).parse());
            final String className = cg.getClassName();
            String fileDir;
            if (className.indexOf('.') != -1) {
                final String classDir = className.substring(0, className.indexOf('.'));
                fileDir = getDirectory(file).toString();
                fileDir = fileDir.substring(0, fileDir.indexOf(classDir + '/'));
            } else {
                fileDir = directory.toURI().toURL().toString();
            }
            final URLClassLoader loader = new URLClassLoader(new URL[]{new URL(fileDir)});
            Class<?> clazz;
            try {
                clazz = loader.loadClass(className);
            } catch (Exception e) {
                e.printStackTrace();
                clazz = null;
            }
            if (clazz != null) {
                if (clazz.isAnnotationPresent(ScriptManifest.class)) {
                    final ScriptManifest manifest = clazz.getAnnotation(ScriptManifest.class);
                    scripts.add(new ScriptInfo(manifest.name(), manifest.description(), clazz, manifest.authors(), manifest.version(), manifest.type()));
                }
            }
        }
    }
}