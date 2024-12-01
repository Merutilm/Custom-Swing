package kr.merutilm.customswing;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.Serial;
import java.util.function.Consumer;
import java.util.function.Function;

import kr.merutilm.base.util.ConsoleUtils;

public class CSSingleDialog<R> extends CSDialog {
    @Serial
    private static final long serialVersionUID = 162300488563368265L;


    public CSSingleDialog(CSFrame master, String title, String optionName, R firstValue, Function<String, R> mapper, Consumer<R> enterFunction) {
        super(master, 200, 100);

        Point masterWindowLocation = master.getLocation();
        Dimension masterWindowSize = master.getSize();

        setLocation(masterWindowLocation.x + (masterWindowSize.width - getWidth()) / 2, masterWindowLocation.y + (masterWindowSize.height - getHeight()) / 2);

        CSPanel panel = new CSPanel(master);
        panel.setBounds(0, 0, getWidth(), getHeight());
        panel.setBackground(new Color(100, 100, 100));

        CSPanel titlePanel = new CSPanel(master);
        titlePanel.setBounds(CSValuePanel.INTERVAL, CSValuePanel.INTERVAL, getWidth() - CSValuePanel.INTERVAL * 2, 30 - CSValuePanel.INTERVAL * 2);
        titlePanel.setBackground(new Color(80, 80, 80));
        titlePanel.getNameLabel().setForeground(new Color(180, 180, 180));
        titlePanel.setName(title);

        CSPanel inputPanelGroup = new CSPanel(master);
        inputPanelGroup.setBounds(CSValuePanel.INTERVAL, 30, getWidth() - CSValuePanel.INTERVAL * 2, getHeight() - 30 - CSValuePanel.INTERVAL);
        inputPanelGroup.setBackground(new Color(40, 40, 40));

        CSValueTextInputPanel<R> inputPanel = new CSValueTextInputPanel<>(master, new Rectangle(0, 0, getWidth() - CSValuePanel.INTERVAL * 2, CSButton.BUTTON_HEIGHT), optionName, firstValue, mapper, false) {

            @Override
            public void enterFunction(R value) {
                enterFunction.accept(value);
                dispose();
            }
        };

        inputPanelGroup.add(inputPanel);
        panel.add(titlePanel);
        panel.add(inputPanelGroup);
        add(panel);
        setup();

        if(!isShowing()){
            return;
        }

        try {
            Robot r = new Robot();
            Point location = inputPanel.getLocationOnScreen();

            r.mouseMove(location.x + getWidth() / 2, location.y + CSButton.BUTTON_HEIGHT / 2);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            ConsoleUtils.logError(e);
        }
    }

}
