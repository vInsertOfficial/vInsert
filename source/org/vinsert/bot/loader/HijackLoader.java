package org.vinsert.bot.loader;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AllPermission;
import java.security.Permissions;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.tree.ClassNode;
import org.vinsert.bot.loader.arch.Archive;
import org.vinsert.bot.loader.arch.ArchiveClassLoader;
import org.vinsert.bot.loader.arch.impl.JarArchive;
import org.vinsert.insertion.IClient;



/**
 * A loader used to load the RuneScape client.
 * @author `Discardedx2 (Discardedx2)
 */
public class HijackLoader {

	/**
	 * This class' logger.
	 */
	private final static Logger logger = Logger.getLogger(HijackLoader.class.getSimpleName());
	/**
	 * The source (world id) pattern.
	 */
	public static final Pattern SOURCE_PATTERN = Pattern.compile("src=\"(.*)\" ");
	/**
	 * The archive pattern (directory of gamepack.jar).
	 */
	public static final Pattern ARCHIVE_PATTERN = Pattern.compile("archive=(.*) ");
	/**
	 * The parameter pattern.
	 */
	public static final Pattern PARAMETER_PATTERN = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">");
	/**
	 * The applet.
	 */
	private Applet applet;
	/**
	 * The class loader.
	 */
	private ClassLoader classLoader;

    public static String getWorld() {
        try {
            URL url = new URL("http://oldschool.runescape.com/slu");
            String source = getPageSource(url);
            return source.split("<a href=\\\"http://oldschool|.runescape.com/j1")[1];
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	/**
	 * Creates a new loader.
	 * @param language The language to load the client in.
	 * @param decrypt Should we decrypt the jar file?
	 * @return A runescape loader.
	 * @throws Exception
	 */
	public static HijackLoader create(Language language, boolean decrypt) throws Exception {
		HijackLoader loader = new HijackLoader();
		URL srcUrl = new URL("http://oldschool"+getWorld()+".runescape.com");
		String source = getPageSource(srcUrl);
		Matcher archiveMatcher = ARCHIVE_PATTERN.matcher(source);
		if (archiveMatcher.find()) {
			HijackStub stub = new HijackStub(PARAMETER_PATTERN, source);
			stub.setCodeBase(srcUrl);
			stub.setDocumentBase(srcUrl);
			logger.info("Opening jar connection...");
			URL url = new URL(stub.getCodeBase() + "/"+stub.getParameter("archive"));
			JarURLConnection conn = (JarURLConnection) new URL("jar:"+url.toString()+"!/").openConnection();
			JarFile jar = conn.getJarFile();
			logger.info("Jar connection opened. Loading jar archive...");

			if (decrypt) {
				Archive<ClassNode> archive = new JarArchive(jar);//new ClassArchive(AES.decryptPack(jar.getInputStream(jar.getEntry("inner.pack.gz")), stub.getParameter("0"), stub.getParameter("-1")));
				logger.info("Archive opened. Injecting bytecode...");
				
				ClassLoader classLoader = loader.classLoader = new ArchiveClassLoader(archive);
				Applet applet = loader.applet = (Applet) classLoader.loadClass("client").newInstance();
				applet.setStub(stub);
				applet.setPreferredSize(new Dimension(765, 503));
				applet.init();
				applet.start();
				return loader;
			}

			logger.info("Archive opened. Running applet.");
//			ClassLoader classLoader = loader.classLoader = new URLClassLoader(new URL[]{ url });
//			Applet applet = loader.applet = (Applet) classLoader.loadClass("client").newInstance();
//			applet.setStub(stub);
//			applet.init();
//			applet.start();
		}
		return loader;
	}
	
	public IClient getClient() {
		return (IClient) applet;
	}


	/**
	 * Gets the page source code for the specified URL.
	 * @param url The specified url.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static String getPageSource(URL url) throws IOException, InterruptedException {
		URLConnection uc = url.openConnection();
		uc.addRequestProperty("Accept",	"text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		uc.addRequestProperty("Accept-Charset",	"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		uc.addRequestProperty("Accept-Encoding", "gzip,deflate");
		uc.addRequestProperty("Accept-Language", "en-gb,en;q=0.5");
		uc.addRequestProperty("Connection", "keep-alive");
		uc.addRequestProperty("Host", "www.runescape.com");
		uc.addRequestProperty("Keep-Alive", "300");
		uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
		DataInputStream di = new DataInputStream(uc.getInputStream());
		byte[] tmp = new byte[uc.getContentLength()];
		di.readFully(tmp);
		di.close();
		return new String(tmp);
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

	/**
	 * Gets the class loader.
	 * @return the class loader.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Gets the applet.
	 * @return
	 */
	public Applet getApplet() {
		return applet;
	}

}
