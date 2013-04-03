package org.vinsert.bot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
import org.vinsert.bot.util.VBLogin;

/**
 * Asynchronous script loader
 * @author tommo
 *
 */
public class BotScriptViewer extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final List<ScriptInfo> scripts = new ArrayList<ScriptInfo>();

	private final DefaultTableModel model;
	private JTable table;
	private JScrollPane tableScroll;
	
	private JComboBox<String> accounts;
	private JButton runButton;
	private JProgressBar loadingBar;
	private JPanel panel;

	public BotScriptViewer() {
		model = new DefaultTableModel(new Object[] { "Script", "Version", "Description", "Authors" }, 0);
		table = new JTable();
		table.setModel(model);
		updateTable();
		tableScroll = new JScrollPane(table);
		
		populateAccounts();
		accounts.setPreferredSize(new Dimension(100, 20));
        
        runButton = new JButton("Execute");
        runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
        });
        loadingBar = new JProgressBar();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

		setTitle("Script Viewer");
		
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(480, 320);
		setLocationRelativeTo(null);
		
		panel.add(accounts, BorderLayout.WEST);
		panel.add(loadingBar, BorderLayout.CENTER);
		panel.add(runButton, BorderLayout.EAST);
		add(tableScroll, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	/**
	 * Executes the selected script
	 */
	public void execute() {
		 final int selected = table.getSelectedRow();
		 if (selected != -1) {
			 setVisible(false);
			 SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Bot bot = Application.window.getActiveBot();
					if (bot != null) {
						int scriptIndex = table.getSelectedRow();
						int accIndex = accounts.getSelectedIndex();
						if (scriptIndex > -1) {
							Account account = null;
							if (accIndex > -1) {
								account = AccountManager.getAccounts().get(accIndex);
							}
							Script s;
							try {
								s = scripts.get(scriptIndex).getClazz().asSubclass(Script.class)
										.newInstance();
								bot.pushScript(s, false, account);
							} catch (InstantiationException e) {
								e.printStackTrace();
								BotWindow.error("Error loading script", "Cannot instantiate script - Script has no no-args constructor!");
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								BotWindow.error("Error loading script", "Illegal class access!");
							}
						} else {
							BotWindow.warn("Oops", "Invalid selected script!");
						}
					} else {
						BotWindow.warn("Oops!", "Invalid bot instance!");
					}
				}
			 });
		 } else {
			 BotWindow.warn("Oops!", "Please select a script to execute.");
		 }
	}
	
	/**
	 * Populates the account list
	 */
	public void populateAccounts() {
		final List<String> accountList = new ArrayList<String>();
        AccountManager.loadAccounts();
        for (final Account account : AccountManager.getAccounts()) {
        	accountList.add(account.getUsername());
        }
        accounts = new JComboBox<String>(accountList.toArray(new String[accountList.size()]));
	}

	/**
	 * Loads the scripts in a background task
	 */
	public void load() {
		loadingBar.setIndeterminate(true);
		loadingBar.setStringPainted(true);
		loadingBar.setString("Loading scripts...");
		ScriptLoaderWorker loader = new ScriptLoaderWorker();
		loader.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("script")) {
					ScriptInfo info = (ScriptInfo) evt.getNewValue();
					scripts.add(info);
					
					model.addRow(new Object[] { info.getName(), info.getVersion(), info.getDesc(), aggregateStrings(info.getAuthors()) });
					updateTable();
				} else if (evt.getPropertyName().equals("finished")) {
					resetLoadingBar();
				}
			}
		});
		loader.execute();
	}
	
	/**
	 * Resets the loading bar
	 */
	public void resetLoadingBar() {
		loadingBar.setIndeterminate(false);
		loadingBar.setValue(0);
		loadingBar.setStringPainted(false);
	}
	
	/**
	 * Aggregates and formats an array of strings
	 * @param strings
	 * @return
	 */
	public String aggregateStrings(String ... strings) {
		StringBuilder sb = new StringBuilder();
		sb.append(strings[0]);
		for (int i = 1; i > strings.length; i++) {
			sb.append(", ").append(strings[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Updates the table
	 */
	public void updateTable() {
		table.setModel(model);
		table.updateUI();
        table.repaint();
	}
	
	/**
	 * The background script loader
	 * @author tommo
	 *
	 */
	public static class ScriptLoaderWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			List<ScriptInfo> locals = new ArrayList<ScriptInfo>();
			ScriptClassLoader.loadLocal(locals, new File(Configuration.COMPILED_DIR));
			
			for (ScriptInfo si : locals) {
				firePropertyChange("script", null, si);
			}
			
			final List<ScriptInfo> definitions = new ArrayList<ScriptInfo>();
			final List<String[]> jars = SDN.getData(String.valueOf(VBLogin.self.getUserId()));
			final List<byte[]> scriptList = SDN.getScriptByteList();
			if (jars != null) {
				for (int i = 0; i < scriptList.size(); i++) {
					try {
						final File file = File.createTempFile("script" + i, "jar");
						final OutputStream out = new FileOutputStream(file);
						out.write(scriptList.get(i));
						out.close();
						ScriptClassLoader.load(definitions, file);
						if (definitions.size() > 0) {
							firePropertyChange("script", null, definitions.get(definitions.size() - 1));
						}
					} catch (final Exception ignored) {
						ignored.printStackTrace();
					}
				}
			}

			firePropertyChange("finished", null, null);
			return null;
		}
		
	}

}
