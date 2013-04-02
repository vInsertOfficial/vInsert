package org.vinsert.bot.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.vinsert.Configuration;
import org.vinsert.bot.Crypter;
import org.vinsert.bot.IOHelper;

/**
 * @author iJava, Velox
 */
public class VBLogin {
	
	public static final int auth_admin = 6;
	public static final int auth_vip = 10; 
	public static final int auth_sw = 13;
	public static final int auth_sm = 5;
	public static final int auth_mod = 7;
	public static final int auth_contrib = 12;
	public static final int auth_sponsor = 11;
	public static final int auth_dev = 9;

    public static VBLogin self = null;

    private final String username;
    private final String password;
    private String key;

    private int userId = -1;
    private int usergroupId = -1;

    private boolean logged = false;

    public VBLogin(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public int getUsergroupId() {
        return usergroupId;
    }

    public int[] getHiddenIds() {
        String scripts = "";
        try {
            scripts = IOHelper.downloadAsString(new URL("https://www.vinsert.org/resources/scripts/hidecheck.php"));
        } catch (final Exception ignored) {
        }
        final List<Integer> idList = new ArrayList<Integer>();
        for (final String s : scripts.split(":")) {
            if (s.length() == 0) {
                continue;
            }
            idList.add(Integer.parseInt(s.replaceAll(":", "")));
        }
        final int[] ids = new int[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            ids[i] = idList.get(i);
        }
        return ids;
    }

    public boolean isLoggedIn() {
        return logged;
    }

    public String getAESKey() {
        return key;
    }

    public boolean login() {
        try {
            final URL url = new URL("http://www.vinsert.org/repo/sec/login.php?username=" + username + "&password=" + password);
            url.openConnection();
            final String page = IOHelper.downloadAsString(url);
            if (page != null && page.replaceAll(" ", "").length() > 0 && !page.equals("0")) {
                logged = true;
                final String[] data = page.split(":");
                
                if (data.length < 2) {
                	return false;
                }
                
                userId = Integer.parseInt(data[0]);
                usergroupId = Integer.parseInt(data[1]);
                return true;
            }
            return false;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void performLogin(final JFrame frame, final JTextField user, final JPasswordField pass, final JCheckBox rememberMe) {
        final VBLogin login = new VBLogin(user.getText().replaceAll(" ", "%20"), new String(pass.getPassword()));
        if (login.login()) {
            if (rememberMe.isSelected()) {
                try {
                    final File file = new File(Configuration.STORAGE_DIR + File.separator + "Account.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    final String key = Crypter.randkey();
                    final String encPass = Crypter.crypt(login.password, key);
                    bw.write(login.getUsername() + ":" + encPass + ":" + key);
                    bw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            self = login;
            frame.setVisible(false);
        } else {
            frame.setTitle("Invalid");
        }
    }

    public static void create(final boolean log) {
        if (!log) {
            final VBLogin login = new VBLogin("", "");
            login.usergroupId = 0;
            login.userId = 0;
            login.logged = true;
            self = login;
        }
        if (self != null) {
            return;
        }
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Login");
        frame.setSize(250, 175);
//        frame.setIconImage(new ImageIcon(VBLogin.class.getResource("/resources/icon_32w.png")).getImage());
        final Container container = frame.getContentPane();
        final JPanel bottom = new JPanel();
        final JPanel center = new JPanel();
        center.setLayout(new GridLayout(4, 1));
        bottom.setLayout(new GridLayout(1, 2));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        final JLabel userLabel = new JLabel("Username");
        center.add(userLabel, BorderLayout.CENTER);
        userLabel.setBounds(85, 10, 80, 20);
        final JTextField user = new JTextField();
        center.add(user, BorderLayout.CENTER);
        user.setBounds(63, 35, 120, 20);
        final JLabel passLabel = new JLabel("Password");
        center.add(passLabel, BorderLayout.CENTER);
        passLabel.setBounds(86, 60, 80, 20);
        final JPasswordField pass = new JPasswordField();
        center.add(pass, BorderLayout.CENTER);
        pass.setBounds(63, 85, 120, 20);
        final JCheckBox rememberMe = new JCheckBox("Remember Me");
        final File account = new File(Configuration.STORAGE_DIR + File.separator + "Account.txt");
        if (account.exists()) {
            try {
                final BufferedReader br = new BufferedReader(new FileReader(account));
                String content = "";
                String line;
                while ((line = br.readLine()) != null) {
                    content += line;
                }
                br.close();
                content = content.replaceAll("\n", "");
                final String[] split = content.split(":");
                user.setText(split[0]);
                pass.setText(Crypter.crypt(split[1], split[2]));
                rememberMe.setSelected(true);
            } catch (Throwable e1) {
            	 e1.printStackTrace();
            }
        }
        bottom.add(rememberMe, BorderLayout.CENTER);
        user.addKeyListener(new KeyAdapter() {
            public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    e.consume();
                    pass.requestFocus();
                    pass.requestFocusInWindow();
                }
            }
        });
        pass.addKeyListener(new KeyAdapter() {
            public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    e.consume();
                    performLogin(frame, user, pass, rememberMe);
                }
            }
        });
        final JButton login = new JButton("Login");
        bottom.add(login, BorderLayout.CENTER);
        container.add(center, BorderLayout.CENTER);
        container.add(bottom, BorderLayout.CENTER);
        login.setBounds(83, 115, 80, 20);
        login.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                performLogin(frame, user, pass, rememberMe);
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
    
}
