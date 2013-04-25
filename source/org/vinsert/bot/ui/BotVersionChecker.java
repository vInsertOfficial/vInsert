package org.vinsert.bot.ui;

import org.vinsert.Application;
import org.vinsert.Configuration;
import org.vinsert.bot.IOHelper;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 * The version checker
 *
 * @author tommo
 */
public class BotVersionChecker extends JDialog implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 240;
    private static final int HEIGHT = 80;

    private static final int RESULT_FALLBACK = 0;
    private static final int RESULT_UP_TO_DATE = 1;
    private static final int RESULT_OUT_OF_DATE = 2;

    private JProgressBar progressBar;
    private Worker worker;

    public BotVersionChecker() {
        init();
    }

    private void init() {
        final BotVersionChecker ref = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLayout(null);
                setSize(WIDTH, HEIGHT);
                setUndecorated(true);
                setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                setResizable(false);
                setLocationRelativeTo(null);

                progressBar = new JProgressBar();
                progressBar.setString("Checking for updates...");
                progressBar.setStringPainted(true);
                progressBar.setIndeterminate(true);
                progressBar.setPreferredSize(new Dimension(200, 80));
                progressBar.setBounds(0, 0, WIDTH, HEIGHT);

                add(progressBar);

                setVisible(true);

                worker = new Worker();
                worker.addPropertyChangeListener(ref);
                worker.execute();
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("version_check")) {
            int value = (Integer) evt.getNewValue();
            if (value == RESULT_UP_TO_DATE) {
                setVisible(false);
                dispose();
                Application.window = new BotWindow();
                Application.window.init();
                System.out.println("UP_TO_DATE");
            } else if (value == RESULT_OUT_OF_DATE) {
                BotWindow.error("Out of date!", Configuration.BOT_NAME + " has been updated, re-download the new version at http://www.vinsert.org/");
                System.out.println("OUT_OF_DATE");
                System.exit(0);
            } else if (value == RESULT_FALLBACK) {
                setVisible(false);
                dispose();
                BotWindow.warn("Connection error", "Could not verify bot version from server, entering offline mode.");
                Configuration.OFFLINE_MODE = true;
                Application.window = new BotWindow();
                Application.window.init();
                System.out.println("FALLBACK_OFFLINE_MODE");
            }
        }
    }

    /**
     * The worker which executes in the background to actually
     * do the version checking
     *
     * @author tommo
     */
    public class Worker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            try {
                String str = IOHelper.downloadAsString(new URL(Configuration.composeres() + Configuration.versionfile));
                String[] strargs = str.split(",");
                Configuration.remote_major = Integer.parseInt(strargs[0]);
                Configuration.remote_minor = Integer.parseInt(strargs[1]);
                if (Configuration.remote_major > Configuration.BOT_VERSION_MAJOR || Configuration.remote_minor > Configuration.BOT_VERSION_MINOR) {
                    firePropertyChange("version_check", null, RESULT_OUT_OF_DATE);
                }
                firePropertyChange("version_check", null, RESULT_UP_TO_DATE);
            } catch (Exception e) {
                firePropertyChange("version_check", null, RESULT_FALLBACK);
            }
            return null;
        }

    }

}
