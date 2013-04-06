package org.vinsert.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.vinsert.bot.script.ScriptInfo;
import org.vinsert.bot.util.ScriptClassLoader;
import org.vinsert.bot.util.VBLogin;

public class SDN {

	private static String cookie; // "_ddn_intercept_2_=f962edd512bdc3b9cd70134de1324551";

	public static String getCookie(final int userId) throws IOException {
        final URL url = new URL("http://www.vinsert.org/repo/info.php");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "vinsert");
        final PrintWriter writer = new PrintWriter(conn.getOutputStream());
        writer.write("id=" + userId);
        writer.flush();
        conn.connect();
 
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
 
        for (int i = 0; i < 5; i++) {
            String line = reader.readLine();
            if (i == 4) return line.trim();
        }
        return null;
    }
	
	public static ScriptInfo[] getScriptDefinitions() {
		try {
			final List<ScriptInfo> definitions = new ArrayList<ScriptInfo>();
			final List<String[]> jars = getData(String.valueOf(VBLogin.self.getUserId()));
			final List<byte[]> scriptList = getScriptByteList();
			if (jars != null) {
				for (int i = 0; i < scriptList.size(); i++) {
					try {
						final File file = File.createTempFile("script" + i, "jar");
						final OutputStream out = new FileOutputStream(file);
						out.write(scriptList.get(i));
						out.close();
						ScriptClassLoader.load(definitions, file);
					} catch (final Exception ignored) {
						ignored.printStackTrace();
					}
				}
				return definitions.toArray(new ScriptInfo[definitions.size()]);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String[]> getData(final String user) throws IOException {
		final List<String[]> data = new ArrayList<String[]>();
		final URL url = new URL("http://www.vinsert.org/repo/info.php");
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//cookie = getCookie(conn.getInputStream());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", "vinsert");
		conn.setRequestProperty("Cookie:", getCookie(VBLogin.self.getUserId()));
		final PrintWriter writer = new PrintWriter(conn.getOutputStream());
		writer.write("id=" + user);
		writer.flush();
		conn.connect();
		if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
			String line;
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			line = reader.readLine();
			if (line != null) {
				final Matcher arrays = Pattern.compile("\\[(.*?)\\]").matcher(line);
				while (arrays.find()) {
					final Matcher items = Pattern.compile("\\{(.*?)\\}").matcher(arrays.group(1));
					final String[] info = new String[13];
					int i = 0;
					while (items.find()) {
						info[i++] = items.group(1);
					}
					System.out.println(info);
					data.add(info);
				}
			}
		}
		return data;
	}

	public static List<byte[]> getScriptByteList() throws Exception {
		final List<byte[]> bytes = new ArrayList<byte[]>();
		final String userid = String.valueOf(VBLogin.self.getUserId());
		final List<String[]> data = getData(userid);
		if (!data.isEmpty()) {
			for (final String[] array : data) {
				final String id = array[0];
				System.out.println("id " + id);
				final URL url = new URL("http://www.vinsert.org/repo/fetcher.php");
				final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("User-Agent", "vinsert");
				conn.setRequestProperty("Cookie:", getCookie(VBLogin.self.getUserId()));
				final PrintWriter writer = new PrintWriter(conn.getOutputStream());
				writer.write("pass=13489123417238947128934&id=" + id);
				writer.flush();
				bytes.add(IOHelper.downloadBinary(conn.getInputStream()));
			}
		}
		return bytes;
	}
}
