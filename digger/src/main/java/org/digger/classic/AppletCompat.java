package org.digger.classic;

import javax.swing.*;
import java.awt.event.KeyEvent;

public abstract class AppletCompat extends JPanel {

    public String getParameter(String name) {
        return null;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {

        if (e.getID() == KeyEvent.KEY_RELEASED) {
            keyUp(convertToSwing(e.getKeyCode()));

        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
            keyDown(convertToSwing(e.getKeyCode()));
        }

    }

    protected abstract boolean keyUp(int key);

    protected abstract boolean keyDown(int key);

    private int convertToSwing(int awtCode) {
        switch (awtCode) {
            case KeyEvent.VK_LEFT:
                return 1006;
            case KeyEvent.VK_RIGHT:
                return 1007;
            case KeyEvent.VK_UP:
                return 1004;
            case KeyEvent.VK_DOWN:
                return 1005;
            case KeyEvent.VK_F1:
                return 1008;
            case KeyEvent.VK_F10:
                return 1021;
            case KeyEvent.VK_PLUS:
                return 1031;
            case KeyEvent.VK_MINUS:
                return 1032;
        }
        return awtCode;
    }
}