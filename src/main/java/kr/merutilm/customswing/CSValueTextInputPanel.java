package kr.merutilm.customswing;

import javax.annotation.Nonnull;
import javax.swing.*;

import kr.merutilm.base.util.ConsoleUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.function.Function;

public abstract class CSValueTextInputPanel<R> extends CSValueInputPanel {
    @Serial
    private static final long serialVersionUID = -6849663245982501934L;
    private final JTextField optionValue;
    private String prevValue = null;
    private boolean isModifying = false;

    /**
     * <strong>입력 패널</strong>
     * <p>
     * <p>
     * 문자를 입력후 엔터키를 누르면 해당 문자로 함수를 돌립니다.<p>
     * 만약 문자가 잘못된 경우, 경고 메시지와 함께 마지막 수정 값으로 롤백됩니다.<p>
     */
    protected CSValueTextInputPanel(CSFrame master, Rectangle valueInputMasterPanelBounds, String name, @Nonnull R firstValue, Function<String, R> mapper, boolean nullable) {
        super(master, valueInputMasterPanelBounds, name, nullable);

        optionValue = new JTextField(firstValue.toString()){
            @Override
            public void setText(String t) {
                super.setText(t);
                prevValue = t;
            }
        };
        optionValue.setLayout(null);
        optionValue.setHorizontalAlignment(SwingConstants.RIGHT);
        optionValue.setBorder(null);
        optionValue.addKeyListener(new CSHotKeyAdapter() {
            @Override
            protected void keyPressedOnce(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    master.setHotKeyAllowed(true);
                    optionValue.setText(prevValue == null ? firstValue.toString() : prevValue);
                    showModifying(false);
                } else if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    master.setHotKeyAllowed(false);
                    showModifying(true);
                }

            }
        });
        optionValue.addActionListener(e -> {
            try {
                master.setHotKeyAllowed(true);
                String text = optionValue.getText();
                optionValue.setEnabled(false);
                optionValue.setEnabled(true);
                enterFunction(mapper.apply(text));
                showModifying(false);
                prevValue = text;
            } catch (RuntimeException f) {
                optionValue.setForeground(new Color(255, 100, 100));
            
                Runnable r = ((Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.hand"));
                if(r != null){
                    r.run();
                }
                ConsoleUtils.logError(f);
                JOptionPane.showMessageDialog(null, "Invalid Value!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                showModifying(false);
                optionValue.setText(prevValue == null ? firstValue.toString() : prevValue);
            }
        });

        setEnabled(true);
        showModifying(false);
        setBounds(valueInputMasterPanelBounds);
        addNullAction(e -> {
            if (Boolean.TRUE.equals(e)) {
                optionValue.setText(firstValue.toString());
                showModifying(false);
            }
        });
        add(optionValue);

    }

    private void showModifying(boolean modify) {
        isModifying = modify;
        if (modify) {
            optionValue.setForeground(new Color(255, 200, 200));
        } else {
            optionValue.setForeground(new Color(200, 255, 200));
        }
    }

    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        optionValue.setBounds((int) (INTERVAL + getWidth() * DEFAULT_INPUT_BOUNDS_RATIO), INTERVAL, (int) (getWidth() * (1 - DEFAULT_INPUT_BOUNDS_RATIO) - (nullable ? INTERVAL + NULLABLE_BUTTON_NAME_LENGTH : INTERVAL * 2)), getHeight() - INTERVAL * 2);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (optionValue != null) {

            optionValue.setEnabled(enabled);

            if (enabled) {
                optionValue.setBackground(new Color(40, 40, 40));
                showModifying(isModifying);
            } else {
                optionValue.setBackground(new Color(20, 20, 20));
            }
        }
    }

    public JTextField getTextPanel() {
        return optionValue;
    }

    @Override
    public void changeNull(boolean b) {
        super.changeNull(b);
        setEnabled(!b);
    }

    public abstract void enterFunction(R value);
}
