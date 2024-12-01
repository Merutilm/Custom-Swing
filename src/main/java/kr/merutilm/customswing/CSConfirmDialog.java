package kr.merutilm.customswing;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import kr.merutilm.base.struct.HexColor;

public class CSConfirmDialog extends CSDialog {
    @Serial
    private static final long serialVersionUID = 162300488563368265L;

    private final CSFrame master;
    private final List<OptionAttribute> optionAttributeList = new ArrayList<>();
    private final CSPanel selectionPanel;

    public CSConfirmDialog(CSFrame master, String title, int height, String message) {
        super(master, 200, height);
        this.master = master;

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

        CSPanel messagePanel = new CSPanel(master);
        messagePanel.setBounds(CSValuePanel.INTERVAL, 30, getWidth() - CSValuePanel.INTERVAL * 2, getHeight() - 30 - CSButton.BUTTON_HEIGHT - CSValuePanel.INTERVAL * 2);
        messagePanel.setBackground(new Color(60, 60, 60));
        messagePanel.setName(message);
        messagePanel.getNameLabel().setForeground(new Color(190, 190, 190));
        messagePanel.getNameLabel().setVisible(true);

        this.selectionPanel = new CSPanel(master);
        selectionPanel.setBounds(CSValuePanel.INTERVAL, getHeight() - CSButton.BUTTON_HEIGHT - CSValuePanel.INTERVAL, getWidth() - CSValuePanel.INTERVAL * 2, CSButton.BUTTON_HEIGHT);

        panel.add(titlePanel);
        panel.add(messagePanel);
        panel.add(selectionPanel);
        add(panel);
    }


    @Override
    public void setup() {
        for (int i = 0; i < optionAttributeList.size(); i++) {
            int x = (getWidth() - CSValuePanel.INTERVAL * 2) * i / optionAttributeList.size();
            int y = 0;

            OptionAttribute attribute = optionAttributeList.get(i);
            CSToggleButton button = new CSToggleButton(master, new Rectangle(x, y, getWidth() / optionAttributeList.size(), CSButton.BUTTON_HEIGHT), attribute.name());

            button.addLeftClickAction(attribute.action());
            button.setBaseColor(attribute.color());

            selectionPanel.add(button);
        }

        super.setup();
    }

    public void addOption(String name, Consumer<CSToggleButton> action) {
        addOption(name, action, HexColor.WHITE);
    }
    public void addGreenOption(String name, Consumer<CSToggleButton> action) {
        addOption(name, action, HexColor.get(150, 255, 150));
    }
    public void addRedOption(String name, Consumer<CSToggleButton> action) {
        addOption(name, action, HexColor.get(255, 150, 150));
    }

    public void addOption(String name, Consumer<CSToggleButton> action, HexColor color) {
        optionAttributeList.add(new OptionAttribute(name, action.andThen(e -> dispose()), color));
    }



    private record OptionAttribute(
            String name,
            Consumer<CSToggleButton> action,
            HexColor color
    ) {

    }
}
