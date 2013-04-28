package org.vinsert.component.debug;

import java.awt.*;

/**
 * @author iJava
 */
public abstract class ObjectDebugger extends Debugger {

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void draw(Graphics2D g) {
    }
}
