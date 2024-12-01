package kr.merutilm.customswing;


import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class CSValuePanel extends CSPanel {
    static final int INTERVAL = 2;
    @Serial
    private static final long serialVersionUID = 4493751405940421308L;
    static final double DEFAULT_INPUT_BOUNDS_RATIO = 0.3;
    private final JLabel optionName;

    /**
     * <strong>값 패널</strong>
     * <p></p>
     * <p></p>
     * 값 템플릿입니다.
     */
    public CSValuePanel(CSFrame master, Rectangle valueInputMasterPanelBounds, String name) {
        super(master);
        super.setLayout(null);
        super.setBackground(new Color(60, 60, 60));
        super.setBounds(valueInputMasterPanelBounds);
        optionName = new JLabel(name);
        optionName.setLayout(null);
        optionName.setBounds(INTERVAL, INTERVAL, (int) (valueInputMasterPanelBounds.getWidth() * DEFAULT_INPUT_BOUNDS_RATIO - INTERVAL * 2), (int) valueInputMasterPanelBounds.getHeight() - INTERVAL * 2);
        optionName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        optionName.setHorizontalAlignment(SwingConstants.CENTER);
        optionName.setForeground(new Color(220, 220, 220));
        add(optionName);

    }

    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        optionName.setBounds(INTERVAL, INTERVAL, (int) (getWidth() * DEFAULT_INPUT_BOUNDS_RATIO - INTERVAL * 2), getHeight() - INTERVAL * 2);
        repaint();
    }

    public JLabel getOptionName() {
        return optionName;
    }
}
