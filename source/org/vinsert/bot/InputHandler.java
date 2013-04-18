package org.vinsert.bot;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

/**
 * Controls a bot's mouse
 *
 * @author tommo
 * @author BenLand100
 */
public class InputHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener {

    /**
     * The bot instance
     */
    @SuppressWarnings("unused")
    private Bot bot;

    /**
     * The canvas instance
     */
    private Canvas canvas;

    /**
     * The latest mouse position
     */
    private Point position = new Point(-1, -1);

    /**
     * Is human input currently active
     */
    private boolean humanInput = true;

    /**
     * Is the mouse being pressed
     */
    @SuppressWarnings("unused")
    private boolean pressed = false;

    /**
     * Is the mouse present in the window
     */
    private boolean present = true;

    /**
     * The coreListener to handle all events within lists.
     */
    private CoreListener coreListener;

    public InputHandler(Bot bot, Canvas canvas) {
        this.bot = bot;
        this.canvas = canvas;
 
        /*
         * Store and remove the client's listeners and replace them with our own so we can reroute
         * events
         */
        coreListener = new CoreListener(
                canvas.getMouseListeners(),
                canvas.getMouseMotionListeners(),
                canvas.getMouseWheelListeners(),
                canvas.getKeyListeners(),
                canvas.getFocusListeners());

        for (MouseListener mouseListener : canvas.getMouseListeners()) {
            canvas.removeMouseListener(mouseListener);
        }
        for (final MouseMotionListener mouseMotionListener : canvas.getMouseMotionListeners()) {
            canvas.removeMouseMotionListener(mouseMotionListener);
        }
        for (final MouseWheelListener mouseWheelListener : canvas.getMouseWheelListeners()) {
            canvas.removeMouseWheelListener(mouseWheelListener);
        }
        for (final KeyListener keyListener : canvas.getKeyListeners()) {
            canvas.removeKeyListener(keyListener);
        }
        for (final FocusListener focusListener : canvas.getFocusListeners()) {
            canvas.removeFocusListener(focusListener);
        }

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addKeyListener(this);
        canvas.addFocusListener(this);
    }

    /**
     * Dispatches a key typed event (a non-letter key)
     *
     * @param keycode
     */
    public void typeNonKey(int keycode) {
        KeyEvent typed = new KeyEvent(getFakeSource(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0,
                (char) keycode, KeyEvent.CHAR_UNDEFINED);
        coreListener.keyPressed(typed);
    }

    /**
     * Dispatches a key pressed event, must release manually
     *
     * @param keycode The keycode, such as KeyEvent.VK_X
     */
    public void pressKey(int keycode) {
        KeyEvent pressed = new KeyEvent(getFakeSource(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                0, keycode, (char) keycode, KeyEvent.KEY_LOCATION_STANDARD);
        coreListener.keyPressed(pressed);
    }

    /**
     * Dispatches a key typed event
     *
     * @param keycode
     */
    public void typeKey(int keycode) {
        KeyEvent typed = new KeyEvent(getFakeSource(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0,
                KeyEvent.VK_UNDEFINED, (char) keycode, KeyEvent.KEY_LOCATION_UNKNOWN);
        coreListener.keyTyped(typed);
    }

    /**
     * Dispatches a key released event
     *
     * @param keycode The keycode, such as KeyEvent.VK_X
     */
    public void releaseKey(int keycode) {
        KeyEvent released = new KeyEvent(getFakeSource(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
                0, keycode, (char) keycode, KeyEvent.KEY_LOCATION_STANDARD);
        coreListener.keyReleased(released);
    }

    /**
     * Dispatches a mouse pressed event, must release manually
     */
    public void pressMouse(boolean right) {
        MouseEvent pressed = new MouseEvent(getFakeSource(), MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), right ? InputEvent.BUTTON3_MASK : 0,
                (int) position.getX(), (int) position.getY(), 1, false);

        coreListener.mousePressed(pressed);
    }

    public void pressMouse() {
        pressMouse(false);
    }

    /**
     * Dispatches a mouse released event
     */
    public void releaseMouse() {
        MouseEvent released = new MouseEvent(getFakeSource(), MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), 0, (int) position.getX(), (int) position.getY(), 1,
                false);
        coreListener.mouseReleased(released);
    }

    /**
     * Moves the mouse to the given position and dispatches a mouse moved event
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void moveMouse(final int x, final int y) {
        final MouseEvent me = new MouseEvent(getFakeSource(), MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(), 0, x, y, 0, false);
        coreListener.mouseMoved(me);
        position.setLocation(x, y);
    }

    /**
     * Moves the mouse to the given position and dispatches a mouse moved event
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void mouseEnter(final int x, final int y) {
        final MouseEvent me = new MouseEvent(getFakeSource(), MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(), 0, x, y, 0, false);
        coreListener.mouseMoved(me);
        position.setLocation(x, y);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        coreListener.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        coreListener.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        coreListener.keyReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //if (humanInput)
            coreListener.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //if (humanInput)
            coreListener.mousePressed(e);
        pressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //if (humanInput)
            coreListener.mouseReleased(e);
        pressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //if (humanInput)
            coreListener.mouseEntered(e);
        present = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //if (humanInput)
            coreListener.mouseExited(e);
        present = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //if (humanInput)
            coreListener.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //if (humanInput)
        {
            position.setLocation(e.getX(), e.getY());
            coreListener.mouseMoved(e);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        //if (humanInput)
            coreListener.focusGained(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        //if (humanInput)
            coreListener.focusLost(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //if (humanInput)
            coreListener.mouseWheelMoved(e);
    }

    public void addScriptListener(final EventListener... listeners) {
        coreListener.addListener(true, listeners);
    }

    public void removeScriptListener(final EventListener... listeners) {
        coreListener.removeListener(true, listeners);
    }

    public Point getPosition() {
        return position;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean isHumanInput() {
        return humanInput;
    }

    public void setHumanInput(boolean humanInput) {
        this.humanInput = humanInput;
    }

    public synchronized Point windMouse(int x, int y, final double speedFactor) {
        double speed = (org.vinsert.bot.util.Utils.randomD() * 15D + 15D) / 10D;
        speed *= speedFactor;
        return windMouseImpl(position.x, position.y, x, y, 9D, 3D, 5D / speed, 10D / speed,
                10D * speed, 8D * speed);
    }

    /**
     * Internal mouse movement algorithm. Do not use this without credit to either Benjamin J. Land
     * or BenLand100. This is synchronized to prevent multiple motions and bannage.
     *
     * @param xs         The x start
     * @param ys         The y start
     * @param xe         The x destination
     * @param ye         The y destination
     * @param gravity    Strength pulling the position towards the destination
     * @param wind       Strength pulling the position in random directions
     * @param minWait    Minimum relative time per step
     * @param maxWait    Maximum relative time per step
     * @param maxStep    Maximum size of a step, prevents out of control motion
     * @param targetArea Radius of area around the destination that should trigger slowing, prevents
     *                   spiraling
     * @result The actual end point
     */
    private synchronized Point windMouseImpl(double xs, double ys, double xe, double ye,
                                             double gravity, double wind, double minWait, double maxWait, double maxStep,
                                             double targetArea) {
        // System.out.println(targetArea);
        final double sqrta = Math.sqrt(5);
        final double sqrtb = Math.sqrt(7);
        double dist, veloX = 0, veloY = 0, windX = 0, windY = 0;
        while ((dist = Math.hypot(xe - xs, ys - ye)) >= 1) {
            wind = Math.min(wind, dist);
            if (dist >= targetArea) {
                windX = windX / sqrta + (Math.random() * (wind * 2D + 1D) - wind) / sqrta;
                windY = windY / sqrtb + (Math.random() * (wind * 2D + 1D) - wind) / sqrtb;
            } else {
                windX /= sqrtb;
                windY /= sqrta;
                if (maxStep < 4) {
                    maxStep = Math.random() * 3 + 3D;
                } else {
                    maxStep /= sqrta;
                }
                // System.out.println(maxStep + ":" + windX + ";" + windY);
            }
            veloX += windX + gravity * (xe - xs) / dist;
            veloY += windY + gravity * (ye - ys) / dist;
            double veloMag = Math.hypot(veloX, veloY);
            if (veloMag > maxStep) {
                double randomDist = maxStep / 2D + Math.random() * maxStep / 2D;
                veloX = (veloX / veloMag) * randomDist;
                veloY = (veloY / veloMag) * randomDist;
            }
            xs += veloX;
            ys += veloY;
            int mx = (int) Math.round(xs);
            int my = (int) Math.round(ys);
            if (position.x != mx || position.y != my) {
                // Scratch
                                /*
                                 * g.drawLine(cx,cy,mx,my); frame.repaint();
                                 */
                // MouseJacking
                                /*
                                 * try { Robot r = new Robot(); r.mouseMove(mx,my); } catch (Exception e) { }
                                 */
                moveMouse(mx, my);
            }
            double step = Math.hypot(xs - position.x, ys - position.y);
            try {
                Thread.sleep(Math.round((maxWait - minWait) * (step / maxStep) + minWait));
            } catch (InterruptedException ex) {
            }
        }
        // System.out.println(Math.abs(xe - cx) + ", " + Math.abs(ye - cy));
        return new Point(position.x, position.y);
    }

    public Component getFakeSource() {
        return bot.getApplet();
    }

    /**
     * CoreListener acts as a wrapper to {@link MouseListener}, {@link MouseMotionListener}, {@link MouseWheelListener},
     * {@link KeyListener}, and {@link FocusListener}.
     * <p>
     * Allows sending events to lists of listeners.
     * </p>
     *
     * @author core
     */
    private class CoreListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener {
        private final List<MouseListener> mouseListeners = new ArrayList<>(), scriptMouseListeners = new ArrayList<>();
        private final List<MouseMotionListener> mouseMotionListeners = new ArrayList<>(), scriptMouseMotionListeners = new ArrayList<>();
        private final List<MouseWheelListener> mouseWheelListeners = new ArrayList<>(), scriptMouseWheelListeners = new ArrayList<>();
        private final List<KeyListener> keyListeners = new ArrayList<>();
        private final List<FocusListener> focusListeners = new ArrayList<>(), scriptFocusListeners = new ArrayList<>();

        public CoreListener(final MouseListener[] mouseListeners,
                            final MouseMotionListener[] mouseMotionListeners,
                            final MouseWheelListener[] mouseWheelListeners,
                            final KeyListener[] keyListeners,
                            final FocusListener[] focusListeners) {
            this.mouseListeners.addAll(Arrays.asList(mouseListeners));
            this.mouseMotionListeners.addAll(Arrays.asList(mouseMotionListeners));
            this.mouseWheelListeners.addAll(Arrays.asList(mouseWheelListeners));
            this.keyListeners.addAll(Arrays.asList(keyListeners));
            this.focusListeners.addAll(Arrays.asList(focusListeners));
        }

        private void addListener(final boolean script, final EventListener... listeners) {
            for (final EventListener listener : listeners) {
                if (listener instanceof MouseListener) {
                    (script ? scriptMouseListeners : mouseListeners).add((MouseListener) listener);
                }
                if (listener instanceof MouseMotionListener) {
                    (script ? scriptMouseMotionListeners : mouseMotionListeners).add((MouseMotionListener) listener);
                }
                if (listener instanceof MouseWheelListener) {
                    (script ? scriptMouseWheelListeners : mouseWheelListeners).add((MouseWheelListener) listener);
                }
                if (!script && listener instanceof KeyListener) {
                    keyListeners.add((KeyListener) listener);
                }
                if (listener instanceof FocusListener) {
                    (script ? scriptFocusListeners : focusListeners).add((FocusListener) listener);
                }
            }
        }

        private void removeListener(final boolean script, final EventListener... listeners) {
            for (final EventListener listener : listeners) {
                if (listener instanceof MouseListener) {
                    (script ? scriptMouseListeners : mouseListeners).remove(listener);
                }
                if (listener instanceof MouseMotionListener) {
                    (script ? scriptMouseMotionListeners : mouseMotionListeners).remove(listener);
                }
                if (listener instanceof MouseWheelListener) {
                    (script ? scriptMouseWheelListeners : mouseWheelListeners).remove(listener);
                }
                if (!script && listener instanceof KeyListener) {
                    keyListeners.add((KeyListener) listener);
                }
                if (listener instanceof FocusListener) {
                    (script ? scriptFocusListeners : focusListeners).remove(listener);
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getSource() == getFakeSource()) {
                e.setSource(canvas);
            }
            for (final KeyListener listener : keyListeners) {
                listener.keyTyped(e);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getSource() == getFakeSource()) {
                e.setSource(canvas);
            }
            for (final KeyListener listener : keyListeners) {
                listener.keyPressed(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getSource() == getFakeSource()) {
                e.setSource(canvas);
            }
            for (final KeyListener listener : keyListeners) {
                listener.keyReleased(e);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseMotionListener listener : mouseMotionListeners) {
                    listener.mouseDragged(e);
                }
            }
            for (final MouseMotionListener listener : scriptMouseMotionListeners) {
                listener.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseMotionListener listener : mouseMotionListeners) {
                    listener.mouseMoved(e);
                }
            }
            for (final MouseMotionListener listener : scriptMouseMotionListeners) {
                listener.mouseMoved(e);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseListener listener : mouseListeners) {
                    listener.mouseClicked(e);
                }
            }
            for (final MouseListener listener : scriptMouseListeners) {
                listener.mouseClicked(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseListener listener : mouseListeners) {
                    listener.mousePressed(e);
                }
            }
            for (final MouseListener listener : scriptMouseListeners) {
                listener.mousePressed(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseListener listener : mouseListeners) {
                    listener.mouseReleased(e);
                }
            }
            for (final MouseListener listener : scriptMouseListeners) {
                listener.mouseReleased(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseListener listener : mouseListeners) {
                    listener.mouseEntered(e);
                }
            }
            for (final MouseListener listener : scriptMouseListeners) {
                listener.mouseEntered(e);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseListener listener : mouseListeners) {
                    listener.mouseExited(e);
                }
            }
            for (final MouseListener listener : scriptMouseListeners) {
                listener.mouseExited(e);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final FocusListener listener : focusListeners) {
                    listener.focusGained(e);
                }
            }
            for (final FocusListener listener : scriptFocusListeners) {
                listener.focusGained(e);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final FocusListener listener : focusListeners) {
                    listener.focusLost(e);
                }
            }
            for (final FocusListener listener : scriptFocusListeners) {
                listener.focusLost(e);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getSource() == getFakeSource() || humanInput) {
                e.setSource(canvas);
                for (final MouseWheelListener listener : mouseWheelListeners) {
                    listener.mouseWheelMoved(e);
                }
            }
            for (final MouseWheelListener listener : scriptMouseWheelListeners) {
                listener.mouseWheelMoved(e);
            }
        }
    }
}