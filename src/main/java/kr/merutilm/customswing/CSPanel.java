package kr.merutilm.customswing;

import javax.swing.*;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;

import java.awt.*;
import java.awt.event.KeyListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CSPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 2939658161648852158L;
    private transient HexColor baseColor = HexColor.WHITE;
    private transient HexColor mergerColor = HexColor.WHITE;
    private transient HexColor mergerTextColor = HexColor.WHITE;
    private final CSFrame main;
    private final JLabel name;
    private final List<Consumer<Boolean>> enableAction = new ArrayList<>();

    public CSFrame getMainFrame() {
        return main;
    }

    public CSPanel(CSFrame main) {
        this.main = main;
        this.name = new JLabel("", SwingConstants.CENTER);

        setFocusable(true);
        if (main != null) {
            for (KeyListener e : main.getKeyListeners()) {
                addKeyListener(e);
            }
        }

        setLayout(null);
        name.setLayout(null);
        name.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        add(name);
        repaint();
    }

    public void addEnabledAction(Consumer<Boolean> action) {
        enableAction.add(action);
    }

    private void runEnabledAction(boolean enable) {
        for (Consumer<Boolean> enterFunction : enableAction) {
            enterFunction.accept(enable);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        runEnabledAction(enabled);
    }



    private void refreshColor() {
        setBackground(baseColor.blend(HexColor.ColorBlendMode.MULTIPLY, mergerColor).toAWT());
        name.setForeground(baseColor.blend(HexColor.ColorBlendMode.MULTIPLY, mergerTextColor).toAWT());
    }

    protected Point2D getPanelLocation() {
        return new Point2D(getX(), getY());
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public String getName() {
        return name.getText();
    }

    public JLabel getNameLabel() {
        return name;
    }

    public void setBaseColor(HexColor baseColor) {
        this.baseColor = baseColor;
        refreshColor();
    }

    public void setMergerColor(HexColor mergerColor) {
        this.mergerColor = mergerColor;
        refreshColor();
    }

    public void setMergerTextColor(HexColor mergerTextColor) {
        this.mergerTextColor = mergerTextColor;
        refreshColor();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getBackground().getAlpha() != 0) {
            g.setColor(new Color(0, 0, 0, 80));
            g.drawRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        name.setBounds(-1, -1, w, h);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        name.setSize(d);
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        name.setSize(w, h);
    }
}
