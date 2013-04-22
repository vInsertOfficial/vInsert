package org.vinsert.bot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.vinsert.bot.util.VBLogin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SDN {

    private static String getCookie(final URL url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "vinsert");
        conn.connect();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        for (int i = 0; i < 5; i++) {
            String line = reader.readLine();
            if (i == 4) return line.trim();
        }
        return null;
    }

    public static List<String[]> getData() {
        try {
            final List<String[]> data = new ArrayList<>();
            final URL url = new URL("http://repo.vinsert.org/getscripts/" + VBLogin.self.getUserId());
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "vinsert");
            conn.setRequestProperty("Cookie", getCookie(url));
            conn.connect();
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String content = "";
                while((line = reader.readLine()) != null) {
                    content += line;
                }
                reader.close();
                String[] scripts = content.split("},");
                for(String str : scripts) {
                  data.add(new String[]{str.split("\"id\": |}")[1], str.split("\"vip\":|, \"type\"")[1]});
                }
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<byte[]> getScriptByteList() {
        try {
            final List<byte[]> bytes = new ArrayList<>();
            final List<String[]> data = getData();
            if (!data.isEmpty()) {
                for (final String[] array : data) {
                    final String id = array[0];
                    if (array[1].replaceAll("\"", "").equals("yes")) {
                        if (VBLogin.self.getUsergroupId() == VBLogin.auth_reg) {
                            continue;
                        }
                    }
                    final URL url = new URL("http://repo.vinsert.org/load/" + id + ".jar");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "vinsert");
                    bytes.add(IOHelper.downloadBinary(conn.getInputStream()));
                }
            }
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}