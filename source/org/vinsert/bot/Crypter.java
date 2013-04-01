package org.vinsert.bot;

import org.vinsert.bot.util.Utils;

/**
 * @author Tyler Sedlar
 */
public class Crypter {

	private static String key = randkey();

	public static void setKey(final String key) {
		Crypter.key = key;
	}

	public static String randkey() {
		final String chars = "1234567890qwertyuiopasdfghjklzxcvbnm[;.,/]'`=";
		String out = "";
		for (int i = 0; i < Utils.random(1, chars.length()); i++) {
			out += Character.toString(chars.charAt(Utils.random(0, chars.length() - 1)));
		}
		return out;
	}

	public static String crypt(final String string, final String key) {
		String out = "";
		int index = 0;
		for (final char c : string.toCharArray()) {
			if (index >= key.length()) {
				index = 0;
			}
			out += Character.toString((char) (c ^ key.charAt(index)));
			index++;
		}
		return out;
	}

	public static String crypt(final String string) {
		return crypt(string, key);
	}
}
