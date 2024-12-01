package kr.merutilm.customswing;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import kr.merutilm.base.functions.BooleanConsumer;

public final class CSToolbarPanel extends CSPanel {
    @Serial
    private static final long serialVersionUID = 7492719270191194669L;
    private int buttonWidthSum = 0;
    private final transient List<CSButton> buttons = new ArrayList<>();
    public CSToolbarPanel(CSFrame master) {
        super(master);
        setBackground(new Color(30, 30, 30));
    }

    public List<CSButton> getButtons() {
        return buttons;
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        refreshToolButtons();
    }

    public void refreshToolButtons(){
        buttonWidthSum = 0;
        for (CSButton button : buttons) {
            button.setBounds(getButtonBounds(button.getWidth()));
        }
    }

    public void addToggleToolbar(String name, int width, Consumer<CSToggleButton> action) {
        CSToggleButton button = new CSToggleButton(getMainFrame(), getButtonBounds(width), name);
        button.addLeftClickAction(action);
        buttons.add(button);
        add(button);
    }

    public void addSwitchToolbar(String name, int width, BooleanConsumer action, boolean defaultValue) {
        CSSwitchButton button = new CSSwitchButton(getMainFrame(), getButtonBounds(width), name);
        button.addAction(action);
        buttons.add(button);
        add(button);
        button.setOn(defaultValue);
    }

    public void addSwitchToolbar(String name, int width, Consumer<CSSwitchButton> action, boolean defaultValue) {
        CSSwitchButton button = new CSSwitchButton(getMainFrame(), getButtonBounds(width), name);
        button.addAction(action);
        buttons.add(button);
        add(button);
        button.setOn(defaultValue);
    }

    private Rectangle getButtonBounds(int width) {
        buttonWidthSum += width;
        return new Rectangle(getWidth() - buttonWidthSum, 0, width, getHeight());
    }
}
