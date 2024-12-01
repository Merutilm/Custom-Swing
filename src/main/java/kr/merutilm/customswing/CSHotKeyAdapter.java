package kr.merutilm.customswing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public abstract class CSHotKeyAdapter extends KeyAdapter {
    private final Set<Integer> pressedKeys = new HashSet<>();
    @Override
    public void keyPressed(KeyEvent e) {
        if (!pressedKeys.contains(e.getKeyCode())) {
            pressedKeys.add(e.getKeyCode());
            keyPressedOnce(e);
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    protected abstract void keyPressedOnce(KeyEvent e);
}
