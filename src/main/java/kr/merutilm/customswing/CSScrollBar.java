package kr.merutilm.customswing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serial;

import kr.merutilm.base.util.AdvancedMath;

abstract class CSScrollBar extends CSPanel {

    @Serial
    private static final long serialVersionUID = 5678772008536506244L;
    protected final CSPanel bar = new CSPanel(getMainFrame());
    enum Type {
        VERTICAL, HORIZONTAL
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled == isEnabled()) {
            super.setEnabled(enabled);
            bar.setEnabled(enabled);
            if (enabled) {
                bar.setBackground(new Color(110, 110, 110));
            } else {
                bar.setBackground(new Color(40, 40, 40));
            }
        }
    }

    CSScrollBar(CSFrame master, Rectangle buttonMasterPanelBounds, CSPanel wheelTarget, int barLength, Type type) {
        super(master);
        setEnabled(false); //위 메서드를 작동해서 색상 설정
        setEnabled(true);
        setLayout(null);
        setBackground(new Color(50, 50, 50));
        setBounds(buttonMasterPanelBounds);

        Rectangle bounds = new Rectangle(buttonMasterPanelBounds);
        bounds.setLocation(0, 0);
        if (type == Type.HORIZONTAL) {
            bounds.setSize(barLength, bounds.height);
        } else {
            bounds.setSize(bounds.width, barLength);
        }
        bar.setBounds(bounds);
        final int[] currentScrollBarX = {0};
        final int[] currentScrollBarY = {0};
        final int[] xOnScreenAtPressed = {0};
        final int[] yOnScreenAtPressed = {0};
        final boolean[] canRefresh = {true};
        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (isEnabled()) {
                    if (canRefresh[0]) {
                        xOnScreenAtPressed[0] = e.getXOnScreen();
                        yOnScreenAtPressed[0] = e.getYOnScreen();
                        canRefresh[0] = false;
                    }
                    bar.setBackground(new Color(70, 70, 70));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    canRefresh[0] = true;
                    if (e.getX() < bar.getWidth() && e.getX() > 0 && e.getY() < bar.getHeight() && e.getY() > 0) {
                        bar.setBackground(new Color(140, 140, 140));
                    } else {
                        bar.setBackground(new Color(110, 110, 110));
                    }
                    currentScrollBarX[0] = bar.getX();
                    currentScrollBarY[0] = bar.getY();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    bar.setBackground(new Color(140, 140, 140));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    bar.setBackground(new Color(110, 110, 110));
                }
            }

        });
        bar.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isEnabled()) {
                    if (type == Type.HORIZONTAL) {
                        int finalX = currentScrollBarX[0] + e.getXOnScreen() - xOnScreenAtPressed[0];
                        finalX = Math.max(0, finalX);
                        finalX = Math.min(finalX, getWidth() - bar.getWidth());
                        scrollValue(AdvancedMath.getRatio(0, (double) getWidth() - bar.getWidth(), finalX));
                        bar.setLocation(finalX, 0);
                    } else {
                        int finalY = currentScrollBarY[0] + e.getYOnScreen() - yOnScreenAtPressed[0];
                        finalY = Math.max(0, finalY);
                        finalY = Math.min(finalY, getHeight() - bar.getHeight());
                        scrollValue(AdvancedMath.getRatio(0, (double)getHeight() - bar.getHeight(), finalY));
                        bar.setLocation(0, finalY);
                    }
                }
            }
        });

        wheelTarget.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (isEnabled()) {
                    int r = e.getWheelRotation();
                    if (type == Type.HORIZONTAL) {
                        switch (r) {
                            case -1 -> currentScrollBarX[0] -= 5;
                            case 1 -> currentScrollBarX[0] += 5;
                            default -> {
                                //noop
                            }
                        }
                        int finalX = currentScrollBarX[0];

                        finalX = Math.max(0, finalX);
                        finalX = Math.min(finalX, getWidth() - bar.getWidth());
                        currentScrollBarX[0] = finalX;

                        scrollValue(AdvancedMath.getRatio(0, (double) getWidth() - bar.getWidth(), finalX));
                        bar.setLocation(finalX, 0);
                    } else {
                        switch (r) {
                            case -1 -> currentScrollBarY[0] -= 5;
                            case 1 -> currentScrollBarY[0] += 5;
                            default -> {
                                //noop
                            }
                        }
                        int finalY = currentScrollBarY[0];

                        finalY = Math.max(0, finalY);
                        finalY = Math.min(finalY, getHeight() - bar.getHeight());
                        currentScrollBarY[0] = finalY;

                        scrollValue(AdvancedMath.getRatio(0, (double) getHeight() - bar.getHeight(), finalY));
                        bar.setLocation(0, finalY);
                    }
                }
            }
        });

        add(bar);
    }

    double getScrollValue() {
        return AdvancedMath.getRatio(0, (double) getHeight() - bar.getHeight(), bar.getY());
    }

    abstract void scrollValue(double ratio);
}
