package kr.merutilm.customswing;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class CSValueLabelPanel extends CSValuePanel {
    @Serial
    private static final long serialVersionUID = -6849663245982501934L;
    private final JTextField optionValue;

    /**
     * <strong>값 패널</strong>
     */
    public CSValueLabelPanel(CSFrame master, String name, String firstValue) {
        super(master, new Rectangle(), name);

        optionValue = new JTextField(firstValue);
        optionValue.setLayout(null);
        optionValue.setHorizontalAlignment(SwingConstants.RIGHT);
        optionValue.setBorder(null);
        optionValue.setEnabled(false);
        add(optionValue);
        optionValue.setBackground(new Color(20, 20, 20));
        optionValue.setForeground(Color.WHITE);

    }

    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        optionValue.setBounds((int) (INTERVAL + getWidth() * DEFAULT_INPUT_BOUNDS_RATIO), INTERVAL,
                (int) (getWidth() * (1 - DEFAULT_INPUT_BOUNDS_RATIO) - (INTERVAL * 2)), getHeight() - INTERVAL * 2);
    }


    public void setText(String text) {
        SwingUtilities.invokeLater(() -> optionValue.setText(text));
    }

}
