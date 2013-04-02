package org.vinsert.bot.ui;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.vinsert.Application;
import org.vinsert.bot.Bot;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.Game.GameState;
import org.vinsert.bot.util.PasteScript;
import org.vinsert.component.HijackCanvas;
import org.vinsert.component.ProjectionListener;
import org.vinsert.component.debug.*;
import org.vinsert.insertion.IClient;


/**
 * Bot UI utility toolbar
 * @author tommo
 *
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
    private JMenuItem pasteScript = new JMenuItem("New Script");
	private JMenu interfaces = new JMenu("Interfaces");
	private JMenuItem interfacesInventory = new JMenuItem("Draw Inventory");
	private JMenuItem interfacesMenu = new JMenuItem("Draw Menu Info");
	private JMenuItem interfacesWidgets = new JMenuItem("Draw Widgets");
        private JMenuItem interfaceExplorer = new JMenuItem("Interface Explorer");
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
	private JMenuItem objectsInfo = new JMenuItem("Info");
	private JMenuItem objectsModels = new JMenuItem("Models");
	private JMenuItem objectsHulls = new JMenuItem("Hulls");
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
	
	private ImageIcon tickIcon = icon("res/icon_tick_small.png");
	private ImageIcon inputEnabled = icon("res/icon_input_small.png");
	private ImageIcon inputEnabledHighlighted = icon("res/icon_input_small_highlighted.png");
	private ImageIcon inputDisabled = icon("res/icon_input_small_disabled.png");
	private ImageIcon inputDisabledHighlighted = icon("res/icon_input_small_disabled_highlighted.png");

	public BotToolBar(BotWindow window, final boolean log) {
		this.window = window;
		init(log);
	}
	
	private void init(final boolean log) {
		miscLogin.setEnabled(false);
		miscFps.setEnabled(false);
		miscFloor.setEnabled(false);
		
		setFloatable(false);
		
		settings.add(accounts);
		settings.addSeparator();
        settings.add(pasteScript);
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
		objects.add(objectsInfo);
		objects.add(objectsModels);
		objects.add(objectsHulls);
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
		settings.addSeparator();
		settings.add(exit);
		
		newBotButton.setIcon(icon("res/icon_plus_small.png"));
		newBotButton.setContentAreaFilled(false);
		newBotButton.setRolloverEnabled(true);
		newBotButton.setRolloverIcon(icon("res/icon_plus_small_highlighted.png"));
	    runScriptButton.setIcon(icon("res/icon_start_small.png"));
	    runScriptButton.setContentAreaFilled(false);
	    runScriptButton.setRolloverEnabled(true);
	    runScriptButton.setRolloverIcon(icon("res/icon_start_small_highlighted.png"));
	    pauseScriptButton.setIcon(icon("res/icon_pause_small.png"));
	    pauseScriptButton.setContentAreaFilled(false);
	    pauseScriptButton.setRolloverEnabled(true);
	    pauseScriptButton.setRolloverIcon(icon("res/icon_pause_small_highlighted.png"));
	    stopScriptButton.setIcon(icon("res/icon_stop_small.png"));
	    stopScriptButton.setContentAreaFilled(false);
	    stopScriptButton.setRolloverEnabled(true);
	    stopScriptButton.setRolloverIcon(icon("res/icon_stop_small_highlighted.png"));
	    inputButton.setIcon(inputEnabled);
	    inputButton.setContentAreaFilled(false);
	    inputButton.setRolloverEnabled(true);
	    inputButton.setRolloverIcon(inputEnabledHighlighted);
	    settingsButton.setIcon(icon("res/icon_settings_small.png"));
	    settingsButton.setContentAreaFilled(false);
	    settingsButton.setRolloverEnabled(true);
	    settingsButton.setRolloverIcon(icon("res/icon_settings_small_highlighted.png"));
	    
	    accounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BotAccountManager().setVisible(true);
			}
	    });
	    newBotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.addNewBot(log);
			}
		});
	    runScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (window.getActiveBot() == null) return;
				new BotScriptSelector(log).setVisible(true);
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

        pasteScript.addActionListener(new ActionListener() {
      			public void actionPerformed(ActionEvent e) {
                      String url = JOptionPane.showInputDialog("Enter Pastebin URL :");
                      new PasteScript(url);
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
        objectsInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleDebugger(objectsInfo, DebugObjectInfo.class);
			}
        });
        objectsModels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleDebugger(objectsModels, DebugObjectModels.class);
			}
        });
        objectsHulls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleDebugger(objectsHulls, DebugObjectHulls.class);
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
                    IClient client = (IClient) b.getApplet();
                    ScriptContext c = new ScriptContext(b, client, null);
                    if (b != null && c.game.getGameState() == GameState.INGAME) {
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
        
	    add(newBotButton);
	    add(Box.createHorizontalGlue());
		add(runScriptButton);
		add(pauseScriptButton);
		add(stopScriptButton);
		add(inputButton);
		add(settingsButton);
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
	
	public static ImageIcon icon(String path) {
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(ImageIO.read(Application.class.getClassLoader().getResource(path)));
		} catch (IllegalArgumentException e) {
		} catch (IOException e) {
		}
		
		if (icon == null) {
			icon = new ImageIcon(path);
		}
		
		return icon;
	}
}
