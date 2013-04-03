package org.vinsert.bot.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * An error dialog panel
 * @author tommo
 *
 */
public class BotErrorDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private Throwable throwable;
	private JTextArea area;
	private JScrollPane scroll;
	
	private BotErrorDialog(String title, Throwable throwable) {
		this.title = title;
		this.throwable = throwable;
		init();
	}
	
	public void init() {
		setTitle(title);
		setSize(480, 320);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		area = new JTextArea();
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		area.setText(writer.toString());
		scroll = new JScrollPane(area);
		
		add(scroll);
		
		setVisible(true);
		setLocationRelativeTo(null);
	}
	
	public static void show(String title, Throwable throwable) {
		new BotErrorDialog(title, throwable);
	}

}
