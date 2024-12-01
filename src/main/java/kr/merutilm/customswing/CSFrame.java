package kr.merutilm.customswing;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class CSFrame extends JFrame {
    private final List<KeyEventListener> eventListener = new ArrayList<>();
    private boolean hotKeyAllowed = true;
    private boolean controlPressed = false;
    private boolean altPressed = false;
    private boolean shiftPressed = false;

    private final List<CSDialog> subDialogs = new ArrayList<>();

    public void addSubDialog(CSDialog component) {
        subDialogs.add(component);
    }

    public void disposeAll() {
        for (CSDialog subDialog : subDialogs) {
            subDialog.dispose();
        }
    }
    protected static final int X_CORRECTION_FRAME = -14;
    protected static final int Y_CORRECTION_FRAME = -37;
    @Serial
    private static final long serialVersionUID = 1966675078653752384L;

    public CSFrame(String title, @Nullable Image icon, int width, int height) {
        setTitle(title);
        setIconImage(icon);
        setBounds(0, 0, width, height);
        getContentPane().setBackground(Color.WHITE);
        setFocusable(true);
        setLayout(null);

        boolean[] executing = new boolean[]{false};

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {

            if (ke.getID() == KeyEvent.KEY_PRESSED) {
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_CONTROL -> controlPressed = true;
                    case KeyEvent.VK_ALT -> altPressed = true;
                    case KeyEvent.VK_SHIFT -> shiftPressed = true;
                    default -> {
                        runEvents(ke, true);
                        if (!executing[0]) {
                            runEvents(ke, false);
                            executing[0] = true;
                        }
                    }
                }
            }
            if (ke.getID() == KeyEvent.KEY_RELEASED) {
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_CONTROL -> controlPressed = false;
                    case KeyEvent.VK_ALT -> altPressed = false;
                    case KeyEvent.VK_SHIFT -> shiftPressed = false;
                    default -> executing[0] = false;
                }

            }
            return false;
        });

    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        setPreferredSize(new Dimension(width - X_CORRECTION_FRAME, height - Y_CORRECTION_FRAME));
    }

    @Override
    public void pack() {
        super.pack();
        setVisible(true);
        repaint();
    }

    public boolean isControlPressed() {
        return controlPressed;
    }

    public boolean isAltPressed() {
        return altPressed;
    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public void setHotKeyAllowed(boolean hotKeyAllowed) {
        this.hotKeyAllowed = hotKeyAllowed;
    }

    public void addKeyListener(Runnable action, int keyCode) {
        addKeyListener(action, keyCode, false);
    }

    public void addKeyListener(Runnable action, int keyCode, boolean requiredControlKey) {
        addKeyListener(action, keyCode, requiredControlKey, false);
    }

    public void addKeyListener(Runnable action, int keyCode, boolean requiredControlKey, boolean requiredShiftKey) {
        addKeyListener(action, keyCode, requiredControlKey, requiredShiftKey, false);
    }

    public void addKeyListener(Runnable action, int keyCode, boolean requiredControlKey, boolean requiredShiftKey, boolean requiredAltKey) {
        eventListener.add(new KeyEventListener(action, keyCode, requiredControlKey, requiredShiftKey, requiredAltKey, false));
    }

    public void addChainKeyListener(Runnable action, int keyCode) {
        addChainKeyListener(action, keyCode, false);
    }

    public void addChainKeyListener(Runnable action, int keyCode, boolean requiredControlKey) {
        addChainKeyListener(action, keyCode, requiredControlKey, false);
    }

    public void addChainKeyListener(Runnable action, int keyCode, boolean requiredControlKey, boolean requiredShiftKey) {
        addChainKeyListener(action, keyCode, requiredControlKey, requiredShiftKey, false);
    }

    public void addChainKeyListener(Runnable action, int keyCode, boolean requiredControlKey, boolean requiredShiftKey, boolean requiredAltKey) {
        eventListener.add(new KeyEventListener(action, keyCode, requiredControlKey, requiredShiftKey, requiredAltKey, true));
    }

    private record KeyEventListener(
            Runnable action,
            int keyCode,
            boolean requiredControlKey,
            boolean requiredShiftKey,
            boolean requiredAltKey,
            boolean enableChain
    ) {
    }

    protected void runEvents(KeyEvent e, boolean enabledChain) {
        if (hotKeyAllowed) {
            for (KeyEventListener keyEventListener : eventListener) {
                if (keyEventListener.enableChain() == enabledChain && e.getKeyCode() == keyEventListener.keyCode && keyEventListener.requiredControlKey == controlPressed && keyEventListener.requiredShiftKey == shiftPressed && keyEventListener.requiredAltKey == altPressed) {
                    keyEventListener.action.run();
                }
            }
        }
    }
}
