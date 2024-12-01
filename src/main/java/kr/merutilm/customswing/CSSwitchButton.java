package kr.merutilm.customswing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import kr.merutilm.base.functions.BooleanConsumer;
import kr.merutilm.base.struct.HexColor;

public class CSSwitchButton extends CSButton {

    @Serial
    private static final long serialVersionUID = -6490473355146442883L;
    private final AtomicBoolean isOn = new AtomicBoolean(false);
    private static final HexColor ON_COLOR_DEFAULT_TEXT = HexColor.get(0, 167, 233);
    private static final HexColor ON_COLOR_DEFAULT_BACKGROUND = HexColor.get(0, 75, 105);
    private static final HexColor ON_COLOR_PRESS_TEXT = HexColor.get(0, 150, 210);
    private static final HexColor ON_COLOR_PRESS_BACKGROUND = HexColor.get(0, 58, 82);
    private static final HexColor ON_COLOR_DISABLED_TEXT = HexColor.get(0, 75, 105);
    private static final HexColor ON_COLOR_DISABLED_BACKGROUND = HexColor.get(0, 29, 41);

    public boolean isOn() {
        return isOn != null && isOn.get();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            if (isOn()) {
                onColorDefault();
            } else {
                colorDefault();
            }
        } else {
            if (isOn()) {
                onColorDisable();
            } else {
                colorDisable();
            }
        }
    }

    public void setOn(boolean on) {
        if (isEnabled() && on != isOn()) {
            isOn.set(on);
            if (on) {
                onColorDefault();
            } else {
                colorDefault();
            }
            
            runAction(this);

        }
    }

    /**
     * <strong>스위치</strong>
     * <p>
     * <p>
     * 클릭할 시 특정 명령을 수행하는 스위치를 생성합니다.<p>
     */
    CSSwitchButton(CSFrame master, Rectangle switchButtonMasterPanelBounds, String name) {
        super(master);
        setBounds(switchButtonMasterPanelBounds);
        setName(name);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled() && e.getButton() == MouseEvent.BUTTON1) {
                    setOn(!isOn.get());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && e.getButton() == MouseEvent.BUTTON1) {
                    if (isOn()) {
                        onColorPress();
                    } else {
                        colorPress();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled() && e.getButton() == MouseEvent.BUTTON1) {
                    if (isOn()) {
                        onColorDefault();
                    } else {
                        colorDefault();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    if (isOn()) {
                        onColorDefault();
                    } else {
                        colorDefault();
                    }
                }
            }
        });
    }

    protected void onColorDefault() {
        setMergerTextColor(ON_COLOR_DEFAULT_TEXT);
        setMergerColor(ON_COLOR_DEFAULT_BACKGROUND);
    }

    protected void onColorPress() {
        setMergerTextColor(ON_COLOR_PRESS_TEXT);
        setMergerColor(ON_COLOR_PRESS_BACKGROUND);
    }

    protected void onColorDisable() {
        setMergerTextColor(ON_COLOR_DISABLED_TEXT);
        setMergerColor(ON_COLOR_DISABLED_BACKGROUND);
    }

    private final transient List<Consumer<CSSwitchButton>> toggleAction = new LinkedList<>();

    void addAction(Consumer<CSSwitchButton> action) {
        toggleAction.add(action);
    }

    void addAction(BooleanConsumer action) {
        toggleAction.add(e -> action.accept(e.isOn()));
    }
    void addAction(int index, BooleanConsumer action) {
        toggleAction.add(index, e -> action.accept(e.isOn()));
    }
    void addAction(int index, Consumer<CSSwitchButton> action) {
        toggleAction.add(index, action);
    }

    private void runAction(CSSwitchButton button) {
        for (Consumer<CSSwitchButton> action : toggleAction) {
            action.accept(button);
        }
    }
}
