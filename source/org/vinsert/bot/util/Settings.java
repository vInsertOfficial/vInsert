package org.vinsert.bot.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vinsert.Configuration;

public class Settings {

	private static HashMap<String, String> settings = new HashMap<String, String>();
	private static final String SETTINGS_DIR = Configuration.STORAGE_DIR + File.separator + "settings.conf";
	
	public Settings() {
		
	}
	
	public static void set(String key, String value) {
		settings.remove(key);
		settings.put(key, value);
	}
	
	public static String get(String key) {
		return settings.get(key);
	}
	
	public static void save() {
		try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_DIR));
            Iterator<Map.Entry<String, String>> iterator = settings.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry<String, String>)iterator.next();
                writer.write(pair.getKey() + "=" + pair.getValue());
                writer.newLine();
                iterator.remove();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void read() {
		try {
            File settingsFile = new File(SETTINGS_DIR);
            BufferedReader reader = null;
            if(settingsFile.exists()) {
                reader = new BufferedReader(new FileReader(SETTINGS_DIR));
            } else {
                InputStream is = Settings.class.getClassLoader().getResourceAsStream(SETTINGS_DIR);
                reader = new BufferedReader(new InputStreamReader(is));
            }

            String line;
            String[] splits;
            while((line = reader.readLine()) != null) {
                if(line.contains("=")) {
                    splits = line.split("=");
                    if(splits.length > 1)
                        settings.put(splits[0], splits[1]);
                    else
                        settings.put(splits[0], "");
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void reset() {
		settings.clear();
		settings.put("theme", "system");
		settings.put("script_dir", Configuration.COMPILED_DIR);
		save();
	}
}
