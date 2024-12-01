package kr.merutilm.customswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class CSDialog extends JFrame {
    @Serial
    private static final long serialVersionUID = 162300488563368265L;

    private final List<Runnable> disposeAction = new ArrayList<>();
    private final CSFrame master;
    public CSDialog(CSFrame master, int width, int height) {

        this.master = master;

        master.disposeAll();
        master.addSubDialog(this);

        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setFocusable(true);
        setLayout(null);
        setVisible(false);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        final int[] xo = {0};
        final int[] yo = {0};
        final Point[] currentLocation = {new Point(getLocation())};
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xo[0] = e.getXOnScreen();
                yo[0] = e.getYOnScreen();
                currentLocation[0] = new Point(getLocation());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getXOnScreen() - xo[0];
                int dy = e.getYOnScreen() - yo[0];
                setLocation(currentLocation[0].x + dx, currentLocation[0].y + dy);
            }
        });
    }

    public void setup() {
        if (isVisible()) {
            throw new IllegalStateException("Already Visible");
        } else {
            pack();
            setVisible(true);
            requestFocus();
        }
    }

    public void addDisposeAction(Runnable action){
        disposeAction.add(action);
    }
    @Override
    public void dispose() {
        super.dispose();
        for (Runnable action : disposeAction) {
            action.run();
        }
        master.setHotKeyAllowed(true);
    }
}
