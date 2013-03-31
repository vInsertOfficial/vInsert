package org.vinsert.bot.ui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.accounts.AccountManager;

/**
 * @author Tekk
 * @author Aion
 * @author Timer
 * @author Paris
 * @author iJava
 */
@SuppressWarnings("serial")
public class BotAccountManager extends JDialog implements ActionListener {

    private static final String[] RANDOM_REWARDS = {"Cash", "Runes", "Coal", "Essence", "Ore", "Bars", "Gems", "Herbs",
            "Seeds", "Charms", "Surprise", "Emote", "Costume", "Attack",
            "Defence", "Strength", "Constitution", "Range", "Prayer", "Magic",
            "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking",
            "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving",
            "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
            "Summoning", "Dungeoneering"};


    private static class RandomRewardEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 6519185448833736787L;

        @SuppressWarnings({ "unchecked", "rawtypes" })
		public RandomRewardEditor() {
            super(new JComboBox(RANDOM_REWARDS));
        }
    }

    private static class PasswordCellEditor extends DefaultCellEditor {
        private static final long serialVersionUID = -8042183192369284908L;

        public PasswordCellEditor() {
            super(new JPasswordField());
        }
    }

    private static class PasswordCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -8149913137634230574L;

        @Override
        protected void setValue(final Object value) {
            if (value == null) {
                setText("");
            } else {
                final String str = value.toString();
                final StringBuilder b = new StringBuilder();
                for (int i = 0; i < str.length(); ++i) {
                    b.append("\u25CF");
                }
                setText(b.toString());
            }
        }
    }

    private class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(final ListSelectionEvent evt) {
            final int row = table.getSelectedRow();
            if (!evt.getValueIsAdjusting()) {
                removeButton.setEnabled(row >= 0 && row < table.getRowCount());
            }
        }
    }


    private JTable table;
    private JButton removeButton;

    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            final JButton button = (JButton) e.getSource();
            if (button.getText().equals("Save")) {
                AccountManager.getAccounts().clear();
                String[] data = new String[4];
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int x = 0; x < table.getColumnCount(); x++) {
                        data[x] = (String) table.getValueAt(i, x);
                    }
                    AccountManager.getAccounts().add(new Account(data[0], data[1], data[2], data[3]));
                }
                AccountManager.saveAccounts();
                dispose();
            } else if (button.getToolTipText().equals("Add")) {
                final String str = JOptionPane.showInputDialog(getParent(), "Enter the account username:", "New Account", JOptionPane.QUESTION_MESSAGE);
                if (str == null || str.isEmpty()) {
                    return;
                }
                final int row = table.getRowCount();
                ((DefaultTableModel) table.getModel()).addRow(new Object[]{str, null, null, null});
                ((DefaultTableModel) table.getModel()).fireTableRowsInserted(row, row);
            } else if (button.getToolTipText().equals("Remove")) {
                final int row = table.getSelectedRow();
                final String user = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
                if (user != null) {
                    for (Account account : AccountManager.getAccounts()) {
                        if (account.getUsername().equals(user)) {
                            AccountManager.getAccounts().remove(account);
                        }
                    }
                    ((DefaultTableModel) table.getModel()).fireTableRowsDeleted(row, row);
                }
            }
        }
    }

    public BotAccountManager() {
        setTitle("Accounts");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        showGUI();
    }

    /**
     * Creates and displays the main GUI This GUI has the list and the main  * buttons
     */
    public void showGUI() {
        final JScrollPane scrollPane = new JScrollPane();
        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Username", "Password", "Pin", "Reward"}));
        AccountManager.loadAccounts();
        if (AccountManager.hasAccounts()) {
            for (Account account : AccountManager.getAccounts()) {
                ((DefaultTableModel) table.getModel()).addRow(new Object[]{account.getUsername(), account.getReward(), account.getPin()
                        , account.getReward()});
            }
        }
        final JToolBar bar = new JToolBar();
        bar.setMargin(new Insets(1, 1, 1, 1));
        bar.setFloatable(false);
        removeButton = new JButton("Remove");
        final JButton newButton = new JButton("Add");
        final JButton doneButton = new JButton("Save");
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        table.setShowGrid(true);
        final TableColumnModel cm = table.getColumnModel();
        cm.getColumn(cm.getColumnIndex("Password")).setCellRenderer(new PasswordCellRenderer());
        cm.getColumn(cm.getColumnIndex("Password")).setCellEditor(new PasswordCellEditor());
        cm.getColumn(cm.getColumnIndex("Pin")).setCellRenderer(new PasswordCellRenderer());
        cm.getColumn(cm.getColumnIndex("Pin")).setCellEditor(new PasswordCellEditor());
        cm.getColumn(cm.getColumnIndex("Reward")).setCellEditor(new RandomRewardEditor());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(table);
        add(scrollPane, BorderLayout.CENTER);
        newButton.setFocusable(false);
        newButton.setToolTipText(newButton.getText());
        newButton.setText("+");
        bar.add(newButton);
        removeButton.setFocusable(false);
        removeButton.setToolTipText(removeButton.getText());
        removeButton.setText("-");
        bar.add(removeButton);
        bar.add(Box.createHorizontalGlue());
        doneButton.setToolTipText(doneButton.getText());
        bar.add(doneButton);
        newButton.addActionListener(this);
        removeButton.addActionListener(this);
        doneButton.addActionListener(this);
        add(bar, BorderLayout.SOUTH);
        final int row = table.getSelectedRow();
        removeButton.setEnabled(row >= 0 && row < table.getRowCount());
        table.clearSelection();
        doneButton.requestFocus();
        setPreferredSize(new Dimension(600, 300));
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    public static void main(String[] args) {
        new BotAccountManager().setVisible(true);
    }
}
