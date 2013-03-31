package org.vinsert.bot.accounts;

/**
 * @author iJava
 */
public class Account {

    private final String username;
    private final String password;
    private final String pin;
    private final String reward;

    public Account(final String username, final String password, final String pin, final String reward) {
        this.username = username;
        this.password = password;
        this.pin = pin;
        this.reward = reward;
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
        return username + ":" + password + ":" + pin + ":" + reward + ";";
    }
}
