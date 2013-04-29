package org.vinsert.bot.ui;

import org.vinsert.Configuration;
import org.vinsert.bot.Bot;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A simple box UI for logging messages to.
 *
 * @author tommo
 */
@SuppressWarnings("all")
public class BotLogger extends JList<String> {

    public static final int MAX_LIST_SIZE = 200;// tweak this number?
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
    private DefaultListModel model = new DefaultListModel();
    private JScrollPane scroller;
    private Bot bot;

    public BotLogger(Bot bot) {
        super();
        setModel(model);
        this.bot = bot;
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        setAutoscrolls(true);
        setCellRenderer(new Renderer());
        setOpaque(false);
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

                if(!bot.isEnableLogger()) {
                    return;
                }
				/*
                 * Remove the last index to prevent the list becoming gigantic
				 */
                if (model.getSize() >= MAX_LIST_SIZE) {
                    model.remove(model.getSize() - 1);
                }
                model.add(0, new LogEntry(record));
            }
        });
    }

    private class LogEntry {

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
        private final Color BACKGROUND = new Color(0x4d4d4d);

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

            result.setForeground(Color.LIGHT_GRAY);
            result.setBackground(BACKGROUND);

            if (wlr.record.getLevel() == Level.SEVERE) {
                result.setForeground(Color.red.darker());
            }

            if (wlr.record.getLevel() == Level.WARNING) {
                result.setForeground(Color.WHITE);
            }

            if ((wlr.record.getLevel() == Level.FINE)
                    || (wlr.record.getLevel() == Level.FINER)
                    || (wlr.record.getLevel() == Level.FINEST)) {
                result.setForeground(Color.WHITE);
            }

            return result;
        }

    }

}