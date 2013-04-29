package org.vinsert.bot.ui;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.tools.Objects;
import org.vinsert.component.HijackCanvas;
import org.vinsert.component.ProjectionListener;
import org.vinsert.component.debug.*;
import org.vinsert.insertion.IClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Bot UI utility toolbar
 *
 * @author tommo
 */
public class BotToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;
    private BotWindow window;
    private JButton newBotButton = new JButton();
    private JButton runScriptButton = new JButton();
    private JButton pauseScriptButton = new JButton();
    private JButton stopScriptButton = new JButton();
    private JButton inputButton = new JButton();
    private JButton settingsButton = new JButton();

    private JPopupMenu settings = new JPopupMenu("Settings");
    private JMenuItem accounts = new JMenuItem("Accounts");
    private JMenu interfaces = new JMenu("Interfaces");
    private JMenuItem interfacesInventory = new JMenuItem("Draw Inventory");
    private JMenuItem interfacesMenu = new JMenuItem("Draw Menu Info");
    private JMenuItem interfacesWidgets = new JMenuItem("Draw Widgets");
    private JMenuItem interfaceExplorer = new JMenuItem("Interface Explorer");
    private JMenuItem disableCanvas = new JMenuItem("Disable Canvas");
    private JMenuItem enableLogger = new JMenuItem("Enable Logging");
    private JMenu npcs = new JMenu("Npcs");
    private JMenuItem npcsInfo = new JMenuItem("Info");
    private JMenuItem npcsModels = new JMenuItem("Models");
    private JMenuItem npcsHulls = new JMenuItem("Hulls");
    private JMenu players = new JMenu("Players");
    private JMenuItem playersMy = new JMenuItem("My Info");
    private JMenuItem playersInfo = new JMenuItem("Info");
    private JMenuItem playersModels = new JMenuItem("Models");
    private JMenuItem playersHulls = new JMenuItem("Hulls");
    private JMenu objects = new JMenu("Objects");
    private JMenu all = new JMenu("All");
    private JMenu walls = new JMenu("Walls");
    private JMenu interactive = new JMenu("Interactive");
    private JMenu floor = new JMenu("Floor");
    private JMenu boundary = new JMenu("Boundary");
    private JMenuItem allObjectsInfo = new JMenuItem("Info");
    private JMenuItem allObjectsModels = new JMenuItem("Models");
    private JMenuItem allObjectsHulls = new JMenuItem("Hulls");
    private JMenuItem interactiveObjectsInfo = new JMenuItem("Info");
    private JMenuItem interactiveObjectsModels = new JMenuItem("Models");
    private JMenuItem interactiveObjectsHulls = new JMenuItem("Hulls");
    private JMenuItem wallsObjectsInfo = new JMenuItem("Info");
    private JMenuItem wallsObjectsModels = new JMenuItem("Models");
    private JMenuItem wallsObjectsHulls = new JMenuItem("Hulls");
    private JMenuItem floorObjectsInfo = new JMenuItem("Info");
    private JMenuItem floorObjectsModels = new JMenuItem("Models");
    private JMenuItem floorObjectsHulls = new JMenuItem("Hulls");
    private JMenuItem boundaryObjectsInfo = new JMenuItem("Info");
    private JMenuItem boundaryObjectsModels = new JMenuItem("Models");
    private JMenuItem boundaryObjectsHulls = new JMenuItem("Hulls");
    private JMenu misc = new JMenu("Misc");
    private JMenuItem miscMouse = new JMenuItem("Mouse Info");
    private JMenuItem miscCursor = new JMenuItem("Draw Cursor");
    private JMenuItem miscCamera = new JMenuItem("Camera Info");
    private JMenuItem miscLogin = new JMenuItem("Login Index");
    private JMenuItem miscFps = new JMenuItem("Draw FPS");
    private JMenuItem miscFloor = new JMenuItem("Draw Floor Height");
    private JMenuItem miscTiles = new JMenuItem("Draw Tiles");
    private JMenuItem miscGroundInfos = new JMenuItem("Draw Ground Info");
    private JMenuItem miscGroundModels = new JMenuItem("Draw Ground Models");
    private JMenuItem miscGroundHulls = new JMenuItem("Draw Ground Hulls");
    private JMenuItem miscWidgetSettings = new JMenuItem("Detect Widget Settings");
    private JMenuItem miscVarbitSettings = new JMenuItem("Detect Varbit Settings");
    private JMenuItem exit = new JMenuItem("Exit");

    private ImageIcon tickIcon = Configuration.icon("res/icon_tick_small.png");
    private ImageIcon inputEnabled = Configuration.icon("res/icon_input_small.png");
    private ImageIcon inputEnabledHighlighted = Configuration.icon("res/icon_input_small_highlighted.png");
    private ImageIcon inputDisabled = Configuration.icon("res/icon_input_small_disabled.png");
    private ImageIcon inputDisabledHighlighted = Configuration.icon("res/icon_input_small_disabled_highlighted.png");

    public BotToolBar(BotWindow window) {
        this.window = window;
        init();
    }

    private void init() {
        miscLogin.setEnabled(false);
        miscFps.setEnabled(false);
        miscFloor.setEnabled(false);

        setPreferredSize(new Dimension(Configuration.BOT_TOOLBAR_WIDTH, Configuration.BOT_TOOLBAR_HEIGHT));
        setFloatable(false);

        settings.add(accounts);
        settings.addSeparator();
        interfaces.add(interfacesInventory);
        interfaces.add(interfacesMenu);
        interfaces.add(interfacesWidgets);
        interfaces.add(interfaceExplorer);
        settings.add(interfaces);
        players.add(playersMy);
        players.add(playersInfo);
        players.add(playersModels);
        players.add(playersHulls);
        settings.add(players);
        npcs.add(npcsInfo);
        npcs.add(npcsModels);
        npcs.add(npcsHulls);
        settings.add(npcs);
        objects.add(all);
        objects.add(interactive);
        objects.add(walls);
        objects.add(floor);
        objects.add(boundary);
        all.add(allObjectsInfo);
        all.add(allObjectsModels);
        all.add(allObjectsHulls);
        interactive.add(interactiveObjectsInfo);
        interactive.add(interactiveObjectsModels);
        interactive.add(interactiveObjectsHulls);
        walls.add(wallsObjectsInfo);
        walls.add(wallsObjectsModels);
        walls.add(wallsObjectsHulls);
        floor.add(floorObjectsInfo);
        floor.add(floorObjectsModels);
        floor.add(floorObjectsHulls);
        boundary.add(boundaryObjectsInfo);
        boundary.add(boundaryObjectsModels);
        boundary.add(boundaryObjectsHulls);
        settings.add(objects);
        misc.add(miscMouse);
        misc.add(miscCursor);
        misc.add(miscTiles);
        misc.add(miscCamera);
        misc.add(miscLogin);
        misc.add(miscFps);
        misc.add(miscFloor);
        misc.add(miscGroundInfos);
        misc.add(miscGroundModels);
        misc.add(miscGroundHulls);
        misc.add(miscWidgetSettings);
        misc.add(miscVarbitSettings);
        settings.add(misc);
        settings.add(disableCanvas);
        settings.add(enableLogger);
        settings.addSeparator();
        settings.add(exit);

        newBotButton.setIcon(Configuration.icon("res/icon_plus_small.png"));
        newBotButton.setContentAreaFilled(false);
        newBotButton.setRolloverEnabled(true);
        newBotButton.setRolloverIcon(Configuration.icon("res/icon_plus_small_highlighted.png"));
        runScriptButton.setIcon(Configuration.icon("res/icon_start_small.png"));
        runScriptButton.setContentAreaFilled(false);
        runScriptButton.setRolloverEnabled(true);
        runScriptButton.setRolloverIcon(Configuration.icon("res/icon_start_small_highlighted.png"));
        pauseScriptButton.setIcon(Configuration.icon("res/icon_pause_small.png"));
        pauseScriptButton.setContentAreaFilled(false);
        pauseScriptButton.setRolloverEnabled(true);
        pauseScriptButton.setRolloverIcon(Configuration.icon("res/icon_pause_small_highlighted.png"));
        stopScriptButton.setIcon(Configuration.icon("res/icon_stop_small.png"));
        stopScriptButton.setContentAreaFilled(false);
        stopScriptButton.setRolloverEnabled(true);
        stopScriptButton.setRolloverIcon(Configuration.icon("res/icon_stop_small_highlighted.png"));
        inputButton.setIcon(inputEnabled);
        inputButton.setContentAreaFilled(false);
        inputButton.setRolloverEnabled(true);
        inputButton.setRolloverIcon(inputEnabledHighlighted);
        settingsButton.setIcon(Configuration.icon("res/icon_settings_small.png"));
        settingsButton.setContentAreaFilled(false);
        settingsButton.setRolloverEnabled(true);
        settingsButton.setRolloverIcon(Configuration.icon("res/icon_settings_small_highlighted.png"));

        accounts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BotAccountManager().setVisible(true);
            }
        });
        newBotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.addNewBot();
            }
        });
        runScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if (window.getActiveBot() == null) return;
                final BotScriptViewer bsv = new BotScriptViewer();
                bsv.load();

                //new BotScriptSelector(log).setVisible(true);
//				Bot bot = window.getActiveBot();
//				if (bot != null) {
//					String scriptname = JOptionPane.showInputDialog("Enter script class name: ");
//					try {
//						if (scriptname == null || scriptname.equals("null") || scriptname.length() == 0) {
//							return;
//						}
//						if (!scriptname.contains(Configuration.SCRIPT_PACKAGE)) {
//							scriptname = Configuration.SCRIPT_PACKAGE + scriptname;
//						}
//						Class<?> scriptclass = this.getClass().getClassLoader().loadClass(scriptname);
//						bot.pushScript((Script) scriptclass.newInstance(), false, null);
//					} catch (Exception exc) {
//						bot.getLogger().log(new LogRecord(Level.SEVERE, "Error loading script class: " + scriptname));
//						exc.printStackTrace();
//					}
//				}
            }
        });
        stopScriptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bot bot = window.getActiveBot();
                if (bot != null) bot.popScript();
            }
        });

        pauseScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Bot bot = window.getActiveBot();
                if (bot != null) {
                    synchronized (bot.scriptStack) {
                        if (!bot.isScriptStackEmpty()) {
                            final Script script = bot.peekScript();
                            System.out.println((script.isPaused() ? "Resumed" : "Paused") + " script: " + script.getManifest().name());
                            script.setPaused(!script.isPaused());
                            //window.getActiveBot().log(window.getClass(), (script.isPaused() ? "Resumed" : "Paused") + " script: " + script.getManifest().name());
                        }
                    }
                }
            }
        });

        settingsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                settings.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        inputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bot bot = window.getActiveBot();
                if (bot != null && bot.getInputHandler() != null) {
                    bot.getInputHandler().setHumanInput(!bot.getInputHandler().isHumanInput());
                    if (bot.getInputHandler().isHumanInput()) {
                        inputButton.setIcon(inputEnabled);
                        inputButton.setRolloverIcon(inputEnabledHighlighted);
                    } else {
                        inputButton.setIcon(inputDisabled);
                        inputButton.setRolloverIcon(inputDisabledHighlighted);
                    }
                }
            }
        });
        enableLogger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bot bot = window.getActiveBot();
                if(bot.isEnableLogger()) {
                    bot.setEnableLogger(false);
                    enableLogger.setIcon(null);
                    return;
                }
                bot.setEnableLogger(true);
                enableLogger.setIcon(tickIcon);
            }
        });
        disableCanvas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bot bot = window.getActiveBot();
                if (bot.isDrawCanvas()) {
                    bot.setDrawCanvas(false);
                    disableCanvas.setIcon(tickIcon);
                    return;
                }
                bot.setDrawCanvas(true);
                disableCanvas.setIcon(null);
            }
        });
        //misc menu
        miscMouse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscMouse, DebugMouseInfo.class);
            }
        });
        miscCamera.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscCamera, DebugCameraInfo.class);
            }
        });
        miscCursor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscCursor, DebugCursor.class);
            }
        });
        miscTiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscTiles, DebugTiles.class);
            }
        });
        miscGroundInfos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscGroundInfos, DebugGroundInfos.class);
            }
        });
        miscGroundModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscGroundModels, DebugGroundModels.class);
            }
        });
        miscGroundHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscGroundHulls, DebugGroundHulls.class);
            }
        });
        miscWidgetSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscWidgetSettings, DebugWidgetSettings.class);
            }
        });
        miscVarbitSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(miscVarbitSettings, DebugVarbitSettings.class);
            }
        });
        //players menu
        playersMy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(playersMy, DebugPlayerInfo.class);
            }
        });
        playersInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(playersInfo, DebugPlayersInfo.class);
            }
        });
        playersModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(playersModels, DebugPlayerModels.class);
            }
        });
        playersHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(playersHulls, DebugPlayerHulls.class);
            }
        });
        //npc menu
        npcsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(npcsInfo, DebugNpcInfo.class);
            }
        });
        npcsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(npcsModels, DebugNpcModels.class);
            }
        });
        npcsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(npcsHulls, DebugNpcHulls.class);
            }
        });
        //object menu
        allObjectsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleObjectsDebugger(allObjectsInfo, DebugObjectInfo.class, Objects.TYPE_ALL);
            }
        });
        allObjectsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(allObjectsModels, DebugObjectModels.class, Objects.TYPE_ALL);
            }
        });
        allObjectsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(allObjectsHulls, DebugObjectHulls.class, Objects.TYPE_ALL);
            }
        });
        interactiveObjectsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleObjectsDebugger(interactiveObjectsInfo, DebugObjectInfo.class, Objects.TYPE_INTERACTABLE);
            }
        });
        interactiveObjectsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(interactiveObjectsModels, DebugObjectModels.class, Objects.TYPE_INTERACTABLE);
            }
        });
        interactiveObjectsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(interactiveObjectsHulls, DebugObjectHulls.class, Objects.TYPE_INTERACTABLE);
            }
        });
        wallsObjectsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleObjectsDebugger(wallsObjectsInfo, DebugObjectInfo.class, Objects.TYPE_WALL_DECORATION);
            }
        });
        wallsObjectsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(wallsObjectsModels, DebugObjectModels.class, Objects.TYPE_WALL_DECORATION);
            }
        });
        wallsObjectsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(wallsObjectsHulls, DebugObjectHulls.class, Objects.TYPE_WALL_DECORATION);
            }
        });
        floorObjectsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleObjectsDebugger(floorObjectsInfo, DebugObjectInfo.class, Objects.TYPE_FLOOR_DECORATION);
            }
        });
        floorObjectsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(floorObjectsModels, DebugObjectModels.class, Objects.TYPE_FLOOR_DECORATION);
            }
        });
        floorObjectsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(floorObjectsHulls, DebugObjectHulls.class, Objects.TYPE_FLOOR_DECORATION);
            }
        });
        boundaryObjectsInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleObjectsDebugger(boundaryObjectsInfo, DebugObjectInfo.class, Objects.TYPE_BOUNDARY);
            }
        });
        boundaryObjectsModels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(boundaryObjectsModels, DebugObjectModels.class, Objects.TYPE_BOUNDARY);
            }
        });
        boundaryObjectsHulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleObjectsDebugger(boundaryObjectsHulls, DebugObjectHulls.class, Objects.TYPE_BOUNDARY);
            }
        });

        //interfaces menu
        interfacesInventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(interfacesInventory, DebugInventory.class);
            }
        });
        interfacesMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(interfacesMenu, DebugMenu.class);
            }
        });
        interfacesWidgets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDebugger(interfacesWidgets, DebugWidgets.class);
            }
        });
        interfaceExplorer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bot b = window.getActiveBot();
                if (b != null) {
                    IClient client = (IClient) b.getApplet();
                    ScriptContext c = new ScriptContext(b, client, null);
                    final BotInterfaceExplorer bie = new BotInterfaceExplorer(c);
                    final HijackCanvas canvas = b.getCanvas();
                    canvas.getListeners().add(bie);
                    java.awt.EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            bie.addWindowListener(new WindowAdapter() {

                                @Override
                                public void windowClosing(WindowEvent e) {
                                    bie.dispose();
                                    canvas.getListeners().remove(bie);
                                }
                            });
                            bie.setVisible(true);
                        }
                    });
                }
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        updateComponents(null);
    }

    /**
     * Removes and re-adds all components in the toolbar
     *
     * @param components Extra components to be appended at the front
     */
    public void updateComponents(List<JComponent> components) {
        removeAll();
        if (components != null) {
            for (JComponent c : components) {
                add(c);
            }
        }
        add(newBotButton);
        add(Box.createHorizontalGlue());
        add(runScriptButton);
        add(pauseScriptButton);
        add(stopScriptButton);
        add(inputButton);
        add(settingsButton);
        revalidate();
    }

    private void toggleDebugger(JMenuItem src, Class<?> listener) {
        Bot b = window.getActiveBot();
        if (b != null) {
            HijackCanvas c = b.getCanvas();
            boolean removed = false;
            if (c == null) {
                b.getLogger().log(new LogRecord(Level.SEVERE, "Error accessing canvas..."));
                return;
            }
            for (int i = 0; i < c.getListeners().size(); i++) {
                ProjectionListener pl = c.getListeners().get(i);
                if (pl.getClass().equals(listener)) {
                    Debugger d = (Debugger) pl;
                    d.uninstall();
                    c.getListeners().remove(i);
                    removed = true;
                    src.setIcon(null);
                    b.getLogger().log(new LogRecord(Level.FINE, "Uninstalled " + d.getClass().getSimpleName().replaceAll("Debug", "") + " debugger"));
                    break;
                }
            }

            if (!removed) {
                try {
                    Debugger d = (Debugger) listener.newInstance();
                    d.install(b);
                    c.getListeners().add(d);
                    src.setIcon(tickIcon);
                    b.getLogger().log(new LogRecord(Level.FINE, "Installed " + d.getClass().getSimpleName().replaceAll("Debug", "") + " debugger"));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void toggleObjectsDebugger(JMenuItem src, Class<?> listener, int type) {
        Bot b = window.getActiveBot();
        if (b != null) {
            HijackCanvas c = b.getCanvas();
            boolean removed = false;
            if (c == null) {
                b.getLogger().log(new LogRecord(Level.SEVERE, "Error accessing canvas..."));
                return;
            }
            for (int i = 0; i < c.getListeners().size(); i++) {
                ProjectionListener pl = c.getListeners().get(i);
                if (pl.getClass().equals(listener)) {
                    ObjectDebugger d = (ObjectDebugger) pl;
                    if (d.getType() == type) {
                        d.uninstall();
                        c.getListeners().remove(i);
                        removed = true;
                        src.setIcon(null);
                        b.getLogger().log(new LogRecord(Level.FINE, "Uninstalled " + d.getClass().getSimpleName().replaceAll("Debug", "") + " debugger"));
                        break;
                    }
                }
            }

            if (!removed) {
                try {
                    ObjectDebugger d = (ObjectDebugger) listener.newInstance();
                    d.setType(type);
                    d.install(b);
                    c.getListeners().add(d);
                    src.setIcon(tickIcon);
                    b.getLogger().log(new LogRecord(Level.FINE, "Installed " + d.getClass().getSimpleName().replaceAll("Debug", "") + " debugger"));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
