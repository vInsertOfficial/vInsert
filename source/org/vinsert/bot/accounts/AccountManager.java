package org.vinsert.bot.accounts;

import org.vinsert.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iJava
 */
public class AccountManager {

    private static List<Account> accounts = new ArrayList<Account>();

    public static boolean hasAccounts() {
        return accounts.size() > 0;
    }

    public static int getSize() {
        return accounts.size();
    }

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static void saveAccounts() {
        try {
            StringBuilder rawAccs = new StringBuilder();
            File accountsFile = new File(Configuration.ACCOUNTS_DIR + File.separator + "Accounts.txt");
            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
            }
            for (Account account : accounts) {
                rawAccs.append(account.toString());
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(accountsFile));
            bw.write(rawAccs.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAccounts() {
        try {
        	accounts.clear();
            File accountsFile = new File(Configuration.ACCOUNTS_DIR + File.separator + "Accounts.txt");
            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
                return;
            }
            StringBuilder fileContents = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(accountsFile));
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line);
            }
            br.close();
            String[] rawAccs = fileContents.toString().split(";");
            if (rawAccs[0].length() > 0) {
                for (String acc : rawAccs) {
                    String[] split = acc.split(":");
                    accounts.add(new Account(split[0], split[1], split[2], split[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
