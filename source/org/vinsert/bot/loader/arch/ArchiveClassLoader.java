package org.vinsert.bot.loader.arch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.vinsert.Configuration;
import org.vinsert.bot.util.InstructionSearcher;

//TODO: this shit needs some serious refactoring
public class ArchiveClassLoader extends ClassLoader {

	/**
	 * The archive.
	 */
	private Archive<?> archive;
	/**
	 * The protection domain.
	 */
	private ProtectionDomain domain;

	/**
	 * The JSON mappings file
	 */
	private Map<String, JSONObject> mappings = new HashMap<String, JSONObject>();

	public ArchiveClassLoader(Archive<?> archive) throws IOException {
		this.archive = archive;
		Permissions permissions = getAppletPermissions();
		this.domain = new ProtectionDomain(new CodeSource(null, (Certificate[]) null), permissions);
		loadMappings();

//		for (ClassNode c : (Archive<ClassNode>) archive) {
//			modify(c);
//		}
//		archive.write(new File("./jarfile.jar"));
//		System.exit(0);

	}
	
	
	private File getLocalVersionFile() {
		return new File(Configuration.STORAGE_DIR + File.separator + Configuration.versionfile);
	}
	private File getLocalInsertionsFile() {
		return new File(Configuration.STORAGE_DIR + File.separator + Configuration.jsonfile);
	}
	
	private int getLocalMinorVersion() throws IOException {
		if(!getLocalVersionFile().exists())
			return -1;		
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(getLocalVersionFile())));
		StringBuilder response = new StringBuilder();
		String inputLine;
		while((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();
		String[] strargs = response.toString().split(",");
		int local_minor = Integer.parseInt(strargs[1]);
		return local_minor;
	}	
	
	private void fetch(String res) throws IOException {
		URL url = new URL(Configuration.composeres() + res);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
        con.setConnectTimeout(5000);
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

		ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
	    FileOutputStream fos = new FileOutputStream(Configuration.STORAGE_DIR + File.separator + res);
	    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	    rbc.close();
	}
	
	private void loadMappings() {
		try {
            if (!Configuration.OFFLINE_MODE) {
                try {
                    fetch(Configuration.versionfile);
                    fetch(Configuration.jsonfile);
                } catch (final IOException ignored) {
                    System.err.println("Failed to downloaded version file and hooks. Attempting to run without latest hooks.");
                }
            }

			System.out.println(Configuration.jsonfile);
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(getLocalInsertionsFile()));
			GzipCompressorInputStream gzip = new GzipCompressorInputStream(in);
			
			JSONParser parser = new JSONParser();
			JSONArray classes = (JSONArray) parser.parse(new BufferedReader(new InputStreamReader(gzip)));
			for (int i = 0; i < classes.size(); i++) {
				JSONObject classEntry = (JSONObject) classes.get(i);
				String name = (String) classEntry.get("name");
				mappings.put(name, classEntry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getText(String url) throws Exception {
        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) website.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
        connection.setDoInput(true);
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }

	@SuppressWarnings("unchecked")
	private void modify(ClassNode node) {
		if (!mappings.containsKey(node.name)) return;
		JSONObject object = mappings.get(node.name);

		String identity = (String) object.get("identity");
		String parentOverride = (String) object.get("parent_override");
		if (parentOverride != null && !parentOverride.equals("null")) {
			node.superName = parentOverride;
			if (node.superName.contains("Canvas")) {
				for (MethodNode mn : (List<MethodNode>) node.methods) {
					if (mn.name.equals("<init>")) {
						for (AbstractInsnNode insn : mn.instructions.toArray()) {
							if (insn.getOpcode() == Opcodes.INVOKESPECIAL) {
								MethodInsnNode min = (MethodInsnNode) insn;
								if (min.owner.contains("Canvas")) {
									min.owner = node.superName;
								}
							}
						}
					}
				}
			}

			//System.out.println("\t"+node.name+" parent changed to "+parentOverride);
		}
		
		JSONArray interfaces = (JSONArray) object.get("interfaces");
		for (int i = 0; i < interfaces.size(); i++) {
			if (!node.interfaces.contains(interfaces.get(i))) {
				//System.out.println("\tAdded interface " + interfaces.get(i) + " to " + identity + " (" + node.name + ")");

				node.interfaces.add(interfaces.get(i));
			}
		}

		//remove interfaces not specified
		for (int i = 0; i < node.interfaces.size(); i++) {
			if (!interfaces.contains(node.interfaces.get(i))) {
				//System.out.println("\tRemoved interface " + node.interfaces.get(i) + " from " + identity + " (" + node.name + ")");
				node.interfaces.remove(i);
			}
		}

		if (identity.equals("Renderable")) {


			for (MethodNode mn : (List<MethodNode>) node.methods) {
				if ((mn.access & Opcodes.ACC_STATIC) != 0) {
					continue;
				}

				if (!mn.desc.endsWith("V")) {
					continue;
				}

				InstructionSearcher searcher = new InstructionSearcher(mn.instructions, 0, Opcodes.INVOKEVIRTUAL, Opcodes.ASTORE);

				if (searcher.match()) {
					InstructionSearcher search = new InstructionSearcher(mn.instructions, 0, Opcodes.INVOKEVIRTUAL);
					AbstractInsnNode[] primMatches = searcher.getMatches().get(0);
					if (search.match()) {
						AbstractInsnNode[] matches = search.getMatches().get(1);

						VarInsnNode var = new VarInsnNode(Opcodes.ALOAD, 0);
						VarInsnNode var2 = new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) primMatches[primMatches.length - 1]).var);

						mn.instructions.insert(matches[matches.length - 1], var);
						mn.instructions.insert(var, var2);
						mn.instructions.insert(var2, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vinsert/bot/script/callback/ModelCallback", "callback", "(Lorg/vinsert/insertion/IRenderable;Lorg/vinsert/insertion/IModel;)V"));

						//System.out.println("\tInserted model callback to "+node.name+"."+mn.name);
					}
				}
			}
		}
		
//		if (identity.equals("GameObject")) {
//			for (MethodNode mn : (List<MethodNode>) node.methods) {
//				if ((mn.access & Opcodes.ACC_STATIC) != 0) {
//					continue;
//				}
//				
//				if (mn.desc.endsWith("V")) {
//					continue;
//				}
//				
//				InstructionSearcher searcher = new InstructionSearcher(mn.instructions, 10, Opcodes.LDC, Opcodes.IMUL, Opcodes.LDC, Opcodes.INVOKEVIRTUAL, Opcodes.ARETURN);
//				
//				if (searcher.match()) {
//					
//					AbstractInsnNode[] matches = searcher.getMatches().get(0);
//
//					VarInsnNode var = new VarInsnNode(Opcodes.ALOAD, 0);
//					VarInsnNode var2 = new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) matches[matches.length - 1]).var);
////					
//					mn.instructions.insert(mn.instructions.get(mn.instructions.size() - 1), var);
//					mn.instructions.insert(var, var2);
//					mn.instructions.insert(var2, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/hijackrs/bot/script/callback/ModelCallback", "callback", "(Lcom/hijackrs/insertion/Renderable;Lcom/hijackrs/insertion/Model;)V"));
//					System.out.println("\tInserted model callback to "+node.name+"."+mn.name);
//				}
//			}
//		}

		JSONArray mappings = (JSONArray) object.get("mappings");
		for (int i = 0; i < mappings.size(); i++) {
			JSONObject entry = (JSONObject) mappings.get(i);
			String parent = (String) entry.get("parent");
			String name = (String) entry.get("name");
			String getterName = (String) entry.get("identity");
			String signature = (String) entry.get("signature");
			String fieldSignature = (String) entry.get("field_signature");
			boolean isStatic = (Boolean) entry.get("static");
			int opLoad = ((Number) entry.get("op_load")).intValue();
			int opReturn = ((Number) entry.get("op_return")).intValue();
			int modBits = ((Number) entry.get("mod_bits")).intValue();
			int modDirect = ((Number) entry.get("mod_value")).intValue();
			boolean modInverse = (Boolean) entry.get("mod_inverse");

			insert(node, parent, name, getterName, signature, fieldSignature, isStatic, opLoad, opReturn, modBits, modDirect, modInverse);

		}
	}

	@SuppressWarnings("unchecked")
	private void insert(ClassNode node, String parent, String name, String getterName, String signature, String fieldSignature, boolean isStatic, int opLoad, int opReturn, int modBits, int modDirect, boolean modInverse) {
		MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, getterName, "()" + signature, null, null);

		StringBuilder sb = new StringBuilder("\tInserted method "+ getterName + "()"+signature+" "+(isStatic ? "GETSTATIC" : "GETFIELD")+" -> " + parent + "." + name+" "+fieldSignature);

		mn.instructions.add(new VarInsnNode(opLoad, 0));
		mn.instructions.add(new FieldInsnNode((isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD), parent, name, fieldSignature));

		if (modDirect != 1) {
			if (modInverse) {
				BigInteger shift = BigInteger.ONE.shiftLeft(modBits);
				BigInteger val = BigInteger.valueOf(modDirect);
				long inverse = val.modInverse(shift).longValue();
				mn.instructions.add(new LdcInsnNode(inverse));
				mn.instructions.add(new InsnNode(Opcodes.L2I));
				sb.append(" * inverse("+modDirect+");");
			} else {
				mn.instructions.add(new LdcInsnNode(modDirect));
				sb.append(" * "+modDirect);
			}
			mn.instructions.add(new InsnNode(Opcodes.IMUL));//show original code pls
		}
		//System.out.println(sb.toString());	
		mn.instructions.add(new InsnNode(opReturn));

		int size = mn.instructions.size();

		mn.visitMaxs(size, size);
		mn.visitEnd();

		node.methods.add(mn);
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		String clsName = name.replaceAll("\\.", "/");

		ClassNode node = (ClassNode) archive.get(clsName);

		if(node != null) {
			modify(node);
			byte[] clsData = archive.getEntry(clsName);
			if(clsData != null) {
				Class<?> cls = defineClass(name, clsData, 0, clsData.length,
						domain);
				if(resolve)
					resolveClass(cls);
				return cls;
			}
		}
		return super.findSystemClass(name);
	}

	/**
	 * Gets the applet permissions.
	 * TODO Create a proper permission set.
	 * @return The applet permissions.
	 */
	public Permissions getAppletPermissions() {
		Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		return permissions;
	}

	public ProtectionDomain getDomain() {
		return domain;
	}

}
