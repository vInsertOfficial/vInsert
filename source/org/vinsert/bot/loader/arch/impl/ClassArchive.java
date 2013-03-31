package org.vinsert.bot.loader.arch.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.vinsert.bot.loader.arch.Archive;


public class ClassArchive implements Archive<ClassNode> {

	/**
	 * The loaded classes.
	 */
	private Map<String, byte[]> classes;
	/**
	 * The loaded classes accepted into class nodes.
	 */
	private Map<String, ClassNode> nodes;

	/**
	 * Constructs a class archive.
	 * @param classes The classes.
	 * @throws IOException
	 */
	public ClassArchive(Map<String, byte[]> classes) throws IOException {
		this.classes = classes;
		this.nodes = load();
	}
	
	public ClassNode get(String name) {
		return nodes.get(name);
	}

	@Override
	public byte[] getEntry(String name) throws ClassNotFoundException {
		ClassNode node = nodes.get(name);
		if (node == null) {
			throw new ClassNotFoundException("Class "+name+" is not valid in the archive.");
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}

	@Override
	public Map<String, ClassNode> load() throws IOException {
		Map<String, ClassNode> clses = new HashMap<String, ClassNode>();
		for (String name : classes.keySet()) {
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(classes.get(name));
			reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
			clses.put(node.name.replace(".class", ""), node);
		}
		return clses;
	}

	@Override
	public void write(File file) throws IOException {
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(file));
		for(String key : classes.keySet()){
			byte[] bytes = classes.get(key);
			jos.putNextEntry(new JarEntry(key.replaceAll("\\.", "/") + ".class"));
			jos.write(bytes);
			jos.closeEntry();

		}
		jos.close();
	}

	@Override
	public Iterator<ClassNode> iterator() {
		return nodes.values().iterator();
	}

}
