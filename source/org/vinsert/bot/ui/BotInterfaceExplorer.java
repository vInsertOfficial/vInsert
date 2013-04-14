package org.vinsert.bot.ui;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.component.ProjectionListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author tholomew
 */
public class BotInterfaceExplorer extends JFrame implements ProjectionListener {

    /**
     *
     */
    private static final long serialVersionUID = -2287588384260930650L;
    private JButton filter;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextField searchQuery;
    private JTextPane widgetInformation;
    private JTree widgets;
    DefaultMutableTreeNode widgetsNode = new DefaultMutableTreeNode("Widgets");
    Widget selWid;  //currently selected widget
    static ScriptContext c;
    ArrayList<DefaultMutableTreeNode> parents = new ArrayList<>();

    public BotInterfaceExplorer(ScriptContext ctx) {
        c = ctx;
        initComponents();
    }

    private void initComponents() {

        searchQuery = new JTextField();
        filter = new JButton();
        jScrollPane1 = new JScrollPane();
        widgetInformation = new JTextPane();
        jScrollPane2 = new JScrollPane();
        widgets = new JTree();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Interface Explorer by tholomew");
        setResizable(false);

        filter.setText("Search");
        filter.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterActionPerformed(evt);
            }
        });

        widgetInformation.setContentType("text/html");
        widgetInformation.setEditable(false);
        jScrollPane1.setViewportView(widgetInformation);
        for (int p : c.widgets.getValidParentIds()) {
            parents.add(new DefaultMutableTreeNode("Parent " + p));
            for (Widget w : c.widgets.get(p)) {
                if (w == null) {
                    continue;
                }
                parents.get(parents.size() - 1).add(new DefaultMutableTreeNode("Child " + w.getId()));
            }
            widgetsNode.add(parents.get(parents.size() - 1));
        }
        widgets.setModel(new DefaultTreeModel(widgetsNode));
        widgets.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                nodeSelect(e);
            }
        });
        jScrollPane2.setViewportView(widgets);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(searchQuery, GroupLayout.PREFERRED_SIZE, 368, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(filter, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE).addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(searchQuery, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addComponent(filter)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE).addComponent(jScrollPane1)).addContainerGap()));

        pack();
    }

    private void search(String query) {
        long start = System.currentTimeMillis();
        parents.clear();
        widgetsNode.removeAllChildren();
        ArrayList<Integer> vIds = new ArrayList<Integer>();
        if (query.equals("")) {
            for (int p : c.widgets.getValidParentIds()) {
                parents.add(new DefaultMutableTreeNode("Parent " + p));
                for (Widget w : c.widgets.get(p)) {
                    if (w == null) {
                        continue;
                    }
                    parents.get(parents.size() - 1).add(new DefaultMutableTreeNode("Child " + w.getId()));
                }
                widgetsNode.add(parents.get(parents.size() - 1));
            }
        } else {
            for (Widget wid : c.widgets.getValidated()) {
                if (wid == null) {
                    continue;
                }
                if (wid.getText().toLowerCase().contains(query.toLowerCase())
                        || wid.getTooltip().toLowerCase().contains(query.toLowerCase())
                        || wid.getSelectedAction().toLowerCase().contains(query.toLowerCase())
                        || getActions(wid.getParentId(), wid.getId()).toLowerCase().contains(query.toLowerCase())) {
                    if (!vIds.contains(wid.getParentId())) {
                        vIds.add(wid.getParentId());
                        parents.add(new DefaultMutableTreeNode("Parent " + wid.getParentId()));
                        for (Widget w : c.widgets.get(wid.getParentId())) {
                            if (w == null) {
                                continue;
                            }
                            if (w.getText().toLowerCase().contains(query.toLowerCase())
                                    || w.getTooltip().toLowerCase().contains(query.toLowerCase())
                                    || w.getSelectedAction().toLowerCase().contains(query.toLowerCase())
                                    || getActions(w.getParentId(), w.getId()).toLowerCase().contains(query.toLowerCase())) {
                                parents.get(parents.size() - 1).add(new DefaultMutableTreeNode("Child " + w.getId()));
                            }
                        }
                        widgetsNode.add(parents.get(parents.size() - 1));
                    }
                }
            }
        }
        widgets.setModel(new DefaultTreeModel(widgetsNode));
        pack();
        long end = System.currentTimeMillis();
        System.out.println("Completed Search and filter in " + (end - start) + "ms. Keyword: \"" + query + "\"");
    }

    private void filterActionPerformed(ActionEvent evt) {
        search(searchQuery.getText());
    }

    private void nodeSelect(TreeSelectionEvent evt) {
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) widgets.getLastSelectedPathComponent();
            if (node != null) {
                if (!node.getUserObject().equals("Widgets")) {
                    DefaultMutableTreeNode pNode = (DefaultMutableTreeNode) node.getParent();
                    if (pNode != null) {
                        if (!pNode.getUserObject().equals("Widgets")) {
                            String ps = String.valueOf(pNode.getUserObject());
                            String cs = String.valueOf(node.getUserObject());
                            int p = Integer.parseInt(ps.substring(7, ps.length())); //parent
                            int c = Integer.parseInt(cs.substring(6, cs.length())); //child
                            setInformation(p, c);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Hi I'm a GUI and I return null pointers all the fucking time");
        }
    }

    private void setInformation(int par, int chi) {
        widgetInformation.setText("<font face=\"Calibri\">"
                + "<b>Parent ID:</b> " + getParentId(par, chi) + "<br>"
                + "<b>ID:</b> " + getId(par, chi) + "<br>"
                + "<b>Child Count:</b> " + getChildCount(par, chi) + "<br>"
                + "<b>Model ID:</b> " + getModelId(par, chi) + "<br>"
                + "<b>Text:</b> " + getText(par, chi) + "<br>"
                + "<b>Name:</b> " + getName(par, chi) + "<br>"
                + "<b>Spell Name:</b> " + getSpellName(par, chi) + "<br>"
                + "<b>Tooltip:</b> " + getTooltip(par, chi) + "<br>"
                + "<b>Selected Action:</b> " + getSelAction(par, chi) + "<br>"
                + "<b>Actions:</b> " + getActions(par, chi) + "<br>"
                + "<b>Slot Contents:</b> " + getSlotContents(par, chi) + "<br>"
                + "<b>Slot Sizes:</b> " + getSlotSizes(par, chi) + "<br>");


        widgetInformation.setText(widgetInformation.getText() + "</font>");
        selWid = c.widgets.get(par, chi);

    }

    private String getText(int par, int chi) {
        return c.widgets.get(par, chi).getText();
    }

    private String getSelAction(int par, int chi) {
        return c.widgets.get(par, chi).getSelectedAction();
    }

    private String getName(int par, int chi) {
        return c.widgets.get(par, chi).getName();
    }

    private String getSpellName(int par, int chi) {
        return c.widgets.get(par, chi).getSpellName();
    }

    private String getTooltip(int par, int chi) {
        return c.widgets.get(par, chi).getTooltip();
    }

    private int getModelId(int par, int chi) {
        return c.widgets.get(par, chi).getModelId();
    }

    private int getId(int par, int chi) {
        return c.widgets.get(par, chi).getId();
    }

    private int getParentId(int par, int chi) {
        return c.widgets.get(par, chi).getParentId();
    }

    private int getChildCount(int par, int chi) {
        return c.widgets.get(par, chi).getChildCount();
    }

    private String getActions(int par, int chi) {
        String actionLine = "";
        String[] actions = c.widgets.get(par, chi).getActions();
        if (actions != null) {
            for (int i = 0; i < actions.length; i++) {
                if (actions[i] != null) {
                    if (!actions[i].startsWith(" ") || !actions[i].isEmpty()) {
                        actionLine = actionLine + actions[i];
                        if (i < actions.length - 1) {
                            actionLine = actionLine + ", ";
                        }
                    }
                }
            }
        }
        return actionLine;
    }

    private String getSlotContents(int par, int chi) {
        String scLine = "";
        int[] contents = c.widgets.get(par, chi).getSlotContents();
        if (contents != null) {
            for (int i = 0; i < contents.length; i++) {
                scLine = scLine + contents[i];
                if (i < contents.length - 1) {
                    scLine = scLine + ", ";
                }
            }
        }
        return scLine;
    }

    private String getSlotSizes(int par, int chi) {
        String ssLine = "";
        int[] sizes = c.widgets.get(par, chi).getSlotSizes();
        if (sizes != null) {
            for (int i = 0; i < sizes.length; i++) {
                ssLine = ssLine + sizes[i];
                if (i < sizes.length - 1) {
                    ssLine = ssLine + ", ";
                }
            }
        }
        return ssLine;
    }

    @Override
    public void render(Graphics2D g) {
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        if (selWid != null) {
            g.setColor(Color.ORANGE);
            g.drawRect(selWid.getX(), selWid.getY(), selWid.getBounds().width, selWid.getBounds().height);
        }
    }
}
