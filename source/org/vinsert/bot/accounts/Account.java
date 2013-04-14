package org.vinsert.bot.accounts;

import org.vinsert.bot.util.Cryption;

/**
 * @author iJava
 */
public class Account {

    private static String username;
	private static String password;
	private static String pin;
	private static String reward;

	public Account(final String username, final String password, final String pin, final String reward) {
		Account.username = Cryption.baseDecrypt(username);
		Account.password = Cryption.baseDecrypt(password);
		Account.pin = Cryption.baseDecrypt(pin);
		Account.reward = Cryption.baseDecrypt(reward);
	}

	public boolean hasBankPin() {
		return !pin.equals("1");
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPin() {
		return pin;
	}

	public String getReward() {
		return reward;
	}

	@Override
	public String toString() {
		return Cryption.baseEncrypt(username) + ":" + Cryption.baseEncrypt(password) + ":" + Cryption.baseEncrypt(pin) + ":" + Cryption.baseEncrypt(reward) + "";
	}
	
}
