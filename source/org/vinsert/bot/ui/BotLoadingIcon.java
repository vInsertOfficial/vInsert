package org.vinsert.bot.ui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;


/**
 * Simple UI loading icon
 * @author IamSharp
 */
public class BotLoadingIcon extends JPanel {

	public static JProgressBar prog;

	public BotLoadingIcon() {

        prog = new javax.swing.JProgressBar();

        setLayout(new java.awt.GridBagLayout());

        prog.setPreferredSize(new java.awt.Dimension(285, 30));
        add(prog, new java.awt.GridBagConstraints());

		prog.updateUI();
		prog.setIndeterminate(true);
		prog.setStringPainted(true);
	}

	public static void text(String s){
		prog.setString(s);
	}

}
