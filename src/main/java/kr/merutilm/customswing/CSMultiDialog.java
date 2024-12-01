package kr.merutilm.customswing;


import java.awt.*;
import java.io.Serial;

public class CSMultiDialog extends CSDialog {
    @Serial
    private static final long serialVersionUID = 162300488563368265L;
    private final CSValueInputGroupPanel inputPanel;

    public CSMultiDialog(CSFrame master, String title, Runnable enterFunction) {
        this(master, title, 250, 200, enterFunction);
    }

    public CSMultiDialog(CSFrame master, String title, int w, int h, Runnable enterFunction) {
        super(master, w, h);

        Point masterWindowLocation = master.getLocation();
        Dimension masterWindowSize = master.getSize();

        setLocation(masterWindowLocation.x + (masterWindowSize.width - getWidth()) / 2, masterWindowLocation.y + (masterWindowSize.height - getHeight()) / 2);

        CSPanel panel = new CSPanel(master);
        panel.setBounds(0, 0, getWidth(), getHeight());
        panel.setBackground(new Color(100, 100, 100));
        int titleHeight = 30;
        int buttonHeight = CSButton.BUTTON_HEIGHT;

        CSPanel titlePanel = new CSPanel(master);
        titlePanel.setBounds(CSValuePanel.INTERVAL, CSValuePanel.INTERVAL, getWidth() - CSValuePanel.INTERVAL * 2, titleHeight - CSValuePanel.INTERVAL * 2);
        titlePanel.setBackground(new Color(80, 80, 80));
        titlePanel.getNameLabel().setForeground(new Color(180, 180, 180));
        titlePanel.setName(title);

        CSPanel inputPanelGroup = new CSPanel(master);
        inputPanelGroup.setBounds(CSValuePanel.INTERVAL, titleHeight, getWidth() - CSValuePanel.INTERVAL * 2, getHeight() - titleHeight - buttonHeight - CSValuePanel.INTERVAL);
        inputPanelGroup.setBackground(new Color(40, 40, 40));

        CSToggleButton enterButton = new CSToggleButton(master, new Rectangle(CSValuePanel.INTERVAL, getHeight() - buttonHeight, getWidth() - CSValuePanel.INTERVAL * 2, buttonHeight - CSValuePanel.INTERVAL), "Generate");
        enterButton.addLeftClickAction(e -> {
            enterFunction.run();
            dispose();
        });


        inputPanel = new CSValueInputGroupPanel(master, inputPanelGroup, "", CSValueInputGroupPanel.InputType.VERTICAL, false);
        inputPanelGroup.add(inputPanel);
        panel.add(titlePanel);
        panel.add(inputPanelGroup);
        panel.add(enterButton);

        add(panel);
    }

    public CSValueInputGroupPanel getInput() {
        return inputPanel;
    }
}
