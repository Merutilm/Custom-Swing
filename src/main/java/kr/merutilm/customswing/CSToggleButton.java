package kr.merutilm.customswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CSToggleButton extends CSButton {

    @Serial
    private static final long serialVersionUID = -8275605480660647865L;
    private final transient List<Consumer<CSToggleButton>> leftClickAction = new ArrayList<>();
    private final transient List<Consumer<CSToggleButton>> rightClickAction = new ArrayList<>();

    public CSToggleButton(CSFrame master, String name) {
        this(master, new Rectangle(0, 0, 100, BUTTON_HEIGHT), name);
    }

    /**
     * <strong>버튼</strong>
     * <p>
     * <p>
     * 클릭할 시 특정 명령을 수행하는 버튼을 생성합니다.<p>
     */
    CSToggleButton(CSFrame master, Rectangle buttonMasterPanelBounds, String name) {
        super(master);
        setBounds(buttonMasterPanelBounds);
        setName(name);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        runLeftClickAction();
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        runRightClickAction();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    colorPress();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    colorDefault();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    colorDefault();
                }
            }

        });
    }

    public void addLeftClickAction(Consumer<CSToggleButton> action) {
        leftClickAction.add(action);
    }

    void addRightClickAction(Consumer<CSToggleButton> action) {
        rightClickAction.add(action);
    }

    private void runLeftClickAction() {
        for (Consumer<CSToggleButton> consumer : leftClickAction) {
            consumer.accept(this);
        }
    }

    private void runRightClickAction() {
        for (Consumer<CSToggleButton> runnable : rightClickAction) {
            runnable.accept(this);
        }
    }

}
