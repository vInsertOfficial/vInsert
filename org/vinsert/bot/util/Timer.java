package org.vinsert.bot.util;

/**
 * @author iJava
 */
public class Timer {

    private long end, start, period;

    /**
     * Constructs a Timer with the length duration of the given parameter.
     *
     * @param period The duration of the Timer.
     */
    public Timer(final long period) {
        this.period = period;
        start = System.currentTimeMillis();
        end = start + period;
    }

    /**
     * Gets the amount of time the Timer has ran for.
     *
     * @return The amount of time the Timer has ran for.
     */
    public long getElapsed() {
        return System.currentTimeMillis() - start;
    }

    /**
     * Gets the time remaining time.
     *
     * @return Gets the remaining time.
     */
    public long getRemaining() {
        return end - System.currentTimeMillis();
    }

    /**
     * Checks if the Timer is still running.
     *
     * @return <t>true</t> if the Timer is running, otherwise <t>false</t>.
     */
    public boolean isRunning() {
        return System.currentTimeMillis() < end;
    }

    /**
     * Sets the ending time period for the Timer.
     *
     * @param newEnd The ending time period for the Timer.
     */
    public void setEndIn(final long newEnd) {
        end = System.currentTimeMillis() + newEnd;
    }

    /**
     * Resets the Timer.
     */
    public void reset() {
        start = System.currentTimeMillis();
        end = start + period;
    }
}
