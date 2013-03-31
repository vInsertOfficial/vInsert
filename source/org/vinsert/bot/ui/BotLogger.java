package org.vinsert.bot.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.vinsert.Configuration;


/**
 * A simple box UI for logging messages to.
 * 
 * @author tommo
 * 
 */
@SuppressWarnings("all")
public class BotLogger extends JList<String> {

	public static final int MAX_LIST_SIZE = 80;// tweak this number?
	public static final Rectangle BOTTOM_OF_WINDOW = new Rectangle(0,
			Integer.MAX_VALUE, 0, 0);
	private static final Formatter formatter = new Formatter() {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"h:mm");

		@Override
		public String format(final LogRecord record) {
			String name = record.getSourceClassName();
			if (name == null || name.equals("null"))
				name = "Default";
			final int max = 16;
			final String append = "...";
			
			return String.format("%s  %-" + max + "s %s", dateFormat.format(record.getMillis()), 
					name.length() > max ? name.substring(0, max - append.length()) : name,
							record.getMessage());
		}
	};

	private static final long serialVersionUID = -402184203325742570L;
	private static DefaultListModel model = new DefaultListModel();
	private JScrollPane scroller;

	public BotLogger() {
		super(model);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		setAutoscrolls(true);
		setCellRenderer(new Renderer());
		scroller = new JScrollPane(this,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(null);
		scroller.setPreferredSize(new Dimension(Configuration.BOT_LOGGER_WIDTH, Configuration.BOT_LOGGER_HEIGHT));
	}

	public JScrollPane createScrollPane() {
		return scroller;
	}

	public synchronized void log(final LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JScrollBar vbar = scroller.getVerticalScrollBar();
				boolean end = vbar.getMaximum() == vbar.getValue()
						+ vbar.getVisibleAmount();

				/*
				 * Remove the first index to prevent the list becoming gigantic
				 */
				if (model.getSize() >= MAX_LIST_SIZE) {
					// TODO
					// model.remove(0);
				}

				model.addElement(new LogEntry(record));
				ensureIndexIsVisible(model.size() - 1);
				// if (end) {
				// scrollRectToVisible(BOTTOM_OF_WINDOW);
				// scroller.scrollRectToVisible(BOTTOM_OF_WINDOW);
				// }
			}
		});
	}

	private static class LogEntry {

		public final LogRecord record;
		public final String formatted;

		public LogEntry(final LogRecord record) {
			this.record = record;
			formatted = BotLogger.formatter.format(record);
		}

		@Override
		public String toString() {
			return BotLogger.formatter.format(record);
		}

	}
	
	private static class Renderer implements ListCellRenderer {

	    private final Border EMPTY_BORDER = new EmptyBorder(1, 1, 1, 1);
	    private final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);

	    public Component getListCellRendererComponent(final JList list,
	                            final Object value, final int index, final boolean isSelected,
	                            final boolean cellHasFocus) {
	      if (!(value instanceof LogEntry))
	        return new JLabel();
	      final LogEntry wlr = (LogEntry) value;

	      final JTextArea result = new JTextArea(wlr.formatted);
	      result.setComponentOrientation(list.getComponentOrientation());
	      result.setFont(list.getFont());
	      result.setBorder(cellHasFocus || isSelected ? SELECTED_BORDER
	          : EMPTY_BORDER);

	      result.setForeground(Color.GRAY);
	      result.setBackground(Color.WHITE);

	      if (wlr.record.getLevel() == Level.SEVERE) {
	    	  result.setForeground(Color.red.darker());
	      }

	      if (wlr.record.getLevel() == Level.WARNING) {
	    	  result.setForeground(Color.BLACK);
	      }

	      if ((wlr.record.getLevel() == Level.FINE)
	          || (wlr.record.getLevel() == Level.FINER)
	          || (wlr.record.getLevel() == Level.FINEST)) {
	    	  result.setForeground(Color.BLACK);
	      }

	      return result;
	    }

	  }

}