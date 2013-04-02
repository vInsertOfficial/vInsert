package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.vinsert.Application;
import org.vinsert.Configuration;
import org.vinsert.bot.Bot;
import org.vinsert.bot.SDN;
import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.accounts.AccountManager;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptInfo;
import org.vinsert.bot.util.ScriptClassLoader;

public class BotScriptSelector extends JDialog {

	private final JDialog self = this;

	private JTable table = new JTable();

	private final List<ScriptInfo> scripts = new ArrayList<ScriptInfo>();

	private final List<ScriptInfo> currentScripts = new ArrayList<ScriptInfo>();

    private String path = Configuration.COMPILED_DIR;

    public BotScriptSelector(final boolean log) {
        setTitle("Script Selector");
        setModal(true);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        final JMenuBar devToolBar = new JMenuBar();
        devToolBar.setBorder(new EmptyBorder(1, 1, 0, 0));
        devToolBar.setLayout(new BorderLayout());
        devToolBar.setMargin(new Insets(1, 1, 1, 1));
        loadScriptPath();
        final JLabel scriptPathLabel = new JLabel(path);
        devToolBar.add(scriptPathLabel, BorderLayout.WEST);
        final JButton selectPath = new JButton("Select Path...");
        selectPath.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(scriptPathLabel.getText()));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showOpenDialog(self) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                final File sp = chooser.getSelectedFile();
                try {
                    final File path = new File(Configuration.SCRIPT_PATH_FILE);
                    if (!path.exists() && !path.createNewFile()) {
                        throw new IOException();
                    } else {
                        final FileOutputStream out = new FileOutputStream(path);
                        out.write(sp.getAbsolutePath().getBytes());
                        out.flush();
                        out.close();
                    }
                } catch (final IOException ignored) {
                    BotWindow.error("Error", "Failed to save the script load path");
                }
                scriptPathLabel.setText(sp.getAbsolutePath());
                try {
                    load(sp, log);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
        devToolBar.add(Box.createHorizontalGlue());
        devToolBar.add(selectPath, BorderLayout.EAST);
        setJMenuBar(devToolBar);
        final JScrollPane scrollpane = new JScrollPane(table);
        add(scrollpane, BorderLayout.CENTER);
        final List<String> accounts = new ArrayList<String>();
        AccountManager.loadAccounts();
        for (final Account account : AccountManager.getAccounts()) {
            accounts.add(account.getUsername());
        }
        final JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout());
        final Font tahoma = new Font("Tahoma", Font.BOLD, 10);
        final JComboBox<String> combobox = new JComboBox<String>(accounts.toArray(new String[accounts.size()]));
        combobox.setFont(tahoma);
        bottom.add(combobox);
        final JTextField search = new JTextField();
        search.setFont(tahoma);
        bottom.add(search);
        final JButton start = new JButton("Start");
        start.setFont(tahoma);
        bottom.add(start);
        add(bottom, BorderLayout.SOUTH);
        try {
            final List<ScriptInfo> allScripts = new ArrayList<ScriptInfo>();
            if (log) {
                for (ScriptInfo script : SDN.getScriptDefinitions()) {
                    System.out.println(script.getName());
                    allScripts.add(script);
                }
            }
            for (ScriptInfo script : scripts) {
                allScripts.add(script);
            }
            final DefaultTableModel model = new CustomTableModel(new Object[]{
                    "Script", "Version", "Description", "Author"}, 0);
            load(new File(path), log);
            search.addKeyListener(new KeyAdapter() {
                public void keyReleased(final KeyEvent e) {
                    final String text = search.getText();
                    currentScripts.clear();
                    if (text.replaceAll(" ", "").length() == 0) {
                        final DefaultTableModel model = new DefaultTableModel(
                                new Object[]{"Script", "Version",
                                        "Description", "Author"}, 0);
                        for (final ScriptInfo definition : allScripts) {
                            currentScripts.add(definition);
                            String authors = "";
                            for (int i = 0; i < definition.getAuthors().length; i++) {
                                authors += definition.getAuthors()[i];
                                if (i < definition.getAuthors().length - 1) {
                                    authors += ", ";
                                }
                            }
                            model.addRow(new Object[]{definition.getName(),
                                    definition.getVersion(),
                                    definition.getDesc(), authors});
                        }
                        table.setModel(model);
                    } else {
                        final DefaultTableModel model = new DefaultTableModel(
                                new Object[]{"Script", "Version",
                                        "Description", "Author"}, 0);
                        for (final ScriptInfo definition : scripts) {
                            final String script = definition.getName();
                            final String description = definition.getDesc();
                            boolean authored = false;
                            for (final String author : definition.getAuthors()) {
                                if (text.toLowerCase().equals(
                                        author.toLowerCase())) {
                                    authored = true;
                                }
                            }
                            boolean descripted = false;
                            if (description.replaceAll(" ", "").length() > 0) {
                                for (final String s : description.split(" ")) {
                                    if (text.toLowerCase().contains(
                                            s.toLowerCase())) {
                                        descripted = true;
                                        break;
                                    }
                                }
                            }
                            if (authored
                                    || descripted
                                    || script.toLowerCase().contains(
                                    text.toLowerCase())) {
                                currentScripts.add(definition);
                                String authors = "";
                                for (int i = 0; i < definition.getAuthors().length; i++) {
                                    authors += definition.getAuthors()[i];
                                    if (i < definition.getAuthors().length - 1) {
                                        authors += ", ";
                                    }
                                }
                                model.addRow(new Object[]{
                                        definition.getName(),
                                        definition.getVersion(),
                                        definition.getDesc(), authors});
                            }
                        }
                        table.setModel(model);
                    }
                    table.updateUI();
                    table.repaint();
                }
            });
            start.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            final int selected = table.getSelectedRow();
                            if (selected != -1) {
                                final ScriptInfo def = currentScripts
                                        .get(selected);
                                try {
                                    setVisible(false);
                                    Bot bot = Application.getBotWindow()
                                            .getActiveBot();
                                    if (bot != null) {
                                        int scriptIndex = table
                                                .getSelectedRow();
                                        int accIndex = combobox
                                                .getSelectedIndex();
                                        if (scriptIndex > -1) {
                                            Account account = null;
                                            if (accIndex > -1) {
                                                account = AccountManager
                                                        .getAccounts().get(
                                                                accIndex);
                                            }
                                            Application
                                                    .getBotWindow()
                                                    .getActiveBot()
                                                    .pushScript(
                                                            loadScript(scriptIndex),
                                                            true, account);
                                            dispose();
                                        }
                                    }
                                } catch (final Exception err) {
                                    err.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            });
            setLocationRelativeTo(null);
            pack();
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); 
        } catch (FileNotFoundException e) {
            e.printStackTrace(); 
        } catch (InstantiationException e) {
            e.printStackTrace(); 
        } catch (IOException e) {
            e.printStackTrace(); 
        } catch (IllegalAccessException e) {
            e.printStackTrace(); 
        }
    }

    private void loadScriptPath() {
        final File path = new File(Configuration.SCRIPT_PATH_FILE);
        if (!path.exists()) {
            return;
        }

        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            final String line = in.readLine();
            final File dir = new File(line);
            if (!dir.exists()) {
                return;
            }
            this.path = line;
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void load(final File directory, final boolean log) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            FileNotFoundException, IOException {
        scripts.clear();
        final DefaultTableModel model = new CustomTableModel(new Object[]{
                "Script", "Version", "Description", "Author"}, 0);
        ScriptClassLoader.loadLocal(scripts, directory);
        if (log) {
            for (ScriptInfo info : SDN.getScriptDefinitions()) {
                scripts.add(info);
            }
        }
        for (final ScriptInfo definition : scripts) {
            currentScripts.add(definition);
            String authors = "";
            for (int i = 0; i < definition.getAuthors().length; i++) {
                authors += definition.getAuthors()[i];
                if (i < definition.getAuthors().length - 1) {
                    authors += ", ";
                }
            }
            model.addRow(new Object[]{definition.getName(),
                    definition.getVersion(), definition.getDesc(), authors});
        }
        table.setModel(model);
    }

	public Script loadScript(int index) {
		Script script = null;
		try {
			script = scripts.get(index).getClazz().asSubclass(Script.class)
					.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return script;
	}

    private class CustomTableModel extends DefaultTableModel {

        public CustomTableModel(final Object[] objects, final int i) {
            super(objects, i);
        }

        @Override
        public boolean isCellEditable(final int col, final int row) {
            return false;
        }
    }
}