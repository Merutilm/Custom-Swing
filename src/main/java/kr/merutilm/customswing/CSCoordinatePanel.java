package kr.merutilm.customswing;

import javax.swing.*;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Range;
import kr.merutilm.base.struct.RectBounds;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.TaskManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serial;
import java.util.concurrent.atomic.AtomicReference;

public class CSCoordinatePanel extends CSPanel {
    @Serial
    private static final long serialVersionUID = 3963671017084828621L;

    private double cx = 0;
    private double cy = 0;

    /**
     * 드래그 및 휠로 이동이 가능한지 여부
     */
    private boolean movable = true;
    /**
     * 축 별로 줌이 가능한지 여부
     */
    private boolean canAxisZoom = true;
    /**
     * 줌이 가능한지 여부
     */
    private boolean canZoom = true;
    /**
     * X좌표 배율
     */
    private double intervalXMultiplier = 1;
    /**
     * Y좌표 배율
     */
    private double intervalYMultiplier = 1;
    /**
     * 클수록 볼 수 있는 영역이 넓어집니다.
     */
    private double zoom = 1;
    /**
     * 회전
     */
    private double rotation = 0;
    /**
     * 이동 경계
     */
    private transient RectBounds movingBoundaries = new RectBounds(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    /**
     * 움직이는 상태인가?
     */
    private boolean coordinateMoving = false;
    /**
     * 좌표 원격 이동 스레드. 움직이다가 한번 더 움직임을 호출할때 interrupt 됩니다
     */
    private transient Thread moveThread;

    private transient Thread zoomThread;
    private transient Thread intXThread;
    private transient Thread intYThread;
    /**
     * 수정자에 따라 마우스 이벤트가 달라집니다
     */
    private MouseMovementType mouseMovementType;
    protected AtomicReference<Double> currWheelZoom = new AtomicReference<>(this.zoom);
    protected AtomicReference<Point2D> currWheelCoordinate = new AtomicReference<>(this.getCoordinate());
    protected AtomicReference<Double> currWheelIntX = new AtomicReference<>(this.intervalXMultiplier);
    protected AtomicReference<Double> currWheelIntY = new AtomicReference<>(this.intervalYMultiplier);

    
    protected CSCoordinatePanel(CSFrame master, Range zoomRange, Range intXRange, Range intYRange) {
        super(master);

        setDoubleBuffered(true);

        mouseMovementType = MouseMovementType.LEFT_CLICK_DRAG_MODE;
        AtomicReference<Double> xp = new AtomicReference<>(0d);
        AtomicReference<Double> yp = new AtomicReference<>(0d);
        AtomicReference<Double> xo = new AtomicReference<>(0d);
        AtomicReference<Double> yo = new AtomicReference<>(0d);

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() > 0) {
                if (!master.isAltPressed() && master.isControlPressed() && master.isShiftPressed()) {

                    if (!canAxisZoom) {
                        return;
                    }

                    if (currWheelIntX.get() < intXRange.max()) {
                        currWheelIntX.set(Math.min(currWheelIntX.get() * 1.111, intXRange.max()));
                        intX(currWheelIntX.get(), 400, Ease.OUT_CUBIC.fun());
                    }

                }
                if (master.isAltPressed() && master.isControlPressed() && !master.isShiftPressed()) {

                    if (!canAxisZoom) {
                        return;
                    }

                    if (currWheelIntY.get() < intYRange.max()) {
                        currWheelIntY.set(Math.min(currWheelIntY.get() * 1.111, intYRange.max()));
                        intY(currWheelIntY.get(), 400, Ease.OUT_CUBIC.fun());
                    }

                }
                if (master.isControlPressed() && !master.isAltPressed() && !master.isShiftPressed()) {

                    if (!canZoom) {
                        return;
                    }

                    if (currWheelZoom.get() < zoomRange.max()) {
                        currWheelZoom.set(Math.min(currWheelZoom.get() * 1.111, zoomRange.max()));
                        zoom(currWheelZoom.get(), 400, Ease.OUT_CUBIC.fun());
                    }

                }
                if (!master.isControlPressed() && !master.isAltPressed() && master.isShiftPressed()) {
                    if (!movable) {
                        return;
                    }
                    if (currWheelCoordinate.get().x() < movingBoundaries.endX()) {
                        currWheelCoordinate.set(new Point2D(Math.min(currWheelCoordinate.get().x() + 120 * currWheelZoom.get() * currWheelIntX.get(), movingBoundaries.endX()), currWheelCoordinate.get().y()));
                        move(currWheelCoordinate.get(), 300, Ease.OUT_CUBIC.fun());
                    }
                }

                if (!master.isControlPressed() && !master.isAltPressed() && !master.isShiftPressed()) {
                    if (!movable) {
                        return;
                    }
                    if (currWheelCoordinate.get().y() < movingBoundaries.endY()) {
                        currWheelCoordinate.set(new Point2D(currWheelCoordinate.get().x(), Math.min(currWheelCoordinate.get().y() + 120 * currWheelZoom.get() * currWheelIntY.get(), movingBoundaries.endY())));
                        move(currWheelCoordinate.get(), 300, Ease.OUT_CUBIC.fun());
                    }
                }


            }
            if ((e.getWheelRotation() < 0)) {
                if (!master.isAltPressed() && master.isControlPressed() && master.isShiftPressed()) {

                    if (!canAxisZoom) {
                        return;
                    }

                    if (currWheelIntX.get() > intXRange.min()) {
                        currWheelIntX.set(Math.max(currWheelIntX.get() * 0.9, intXRange.min()));
                        intX(currWheelIntX.get(), 400, Ease.OUT_CUBIC.fun());
                    }

                }
                if (master.isAltPressed() && master.isControlPressed() && !master.isShiftPressed()) {

                    if (!canAxisZoom) {
                        return;
                    }

                    if (currWheelIntY.get() > intYRange.min()) {
                        currWheelIntY.set(Math.max(currWheelIntY.get() * 0.9, intYRange.min()));
                        intY(currWheelIntY.get(), 400, Ease.OUT_CUBIC.fun());
                    }

                }
                if (master.isControlPressed() && !master.isAltPressed() && !master.isShiftPressed()) {

                    if (!canZoom) {
                        return;
                    }

                    if (currWheelZoom.get() > zoomRange.min()) {
                        currWheelZoom.set(Math.max(currWheelZoom.get() * 0.9, zoomRange.min()));
                        zoom(currWheelZoom.get(), 400, Ease.OUT_CUBIC.fun());
                    }
                }
                if (!master.isControlPressed() && !master.isAltPressed() && master.isShiftPressed()) {
                    if (!movable) {
                        return;
                    }
                    if (currWheelCoordinate.get().x() > movingBoundaries.startX()) {
                        currWheelCoordinate.set(new Point2D(Math.max(currWheelCoordinate.get().x() - 120 * currWheelZoom.get() * currWheelIntX.get(), movingBoundaries.startX()), currWheelCoordinate.get().y()));
                        move(currWheelCoordinate.get(), 300, Ease.OUT_CUBIC.fun());
                    }
                }
                if (!master.isControlPressed() && !master.isAltPressed() && !master.isShiftPressed()) {
                    if (!movable) {
                        return;
                    }
                    if (currWheelCoordinate.get().y() > movingBoundaries.startY()) {
                        currWheelCoordinate.set(new Point2D(currWheelCoordinate.get().x(), Math.max(currWheelCoordinate.get().y() - 120 * currWheelZoom.get() * currWheelIntY.get(), movingBoundaries.startY())));
                        move(currWheelCoordinate.get(), 300, Ease.OUT_CUBIC.fun());
                    }
                }
            }
            fixCoordinate();
        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!movable) {
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    stopMove();
                    xo.set(e.getX() * getZoom() * intervalXMultiplier);
                    yo.set(e.getY() * getZoom() * intervalYMultiplier);
                    xp.set(cx);
                    yp.set(cy);

                    if (master.isShiftPressed() && !master.isAltPressed() && !master.isControlPressed()) {
                        mouseMovementType = MouseMovementType.LEFT_CLICK_SHIFT_MODE;
                    }
                    if (!master.isShiftPressed() && !master.isAltPressed() && !master.isControlPressed()) {
                        mouseMovementType = MouseMovementType.LEFT_CLICK_DRAG_MODE;
                    }
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    if (master.isShiftPressed() && !master.isAltPressed() && !master.isControlPressed()) {
                        mouseMovementType = MouseMovementType.RIGHT_CLICK_SHIFT_MODE;
                    }
                    if (!master.isShiftPressed() && !master.isAltPressed() && !master.isControlPressed()) {
                        mouseMovementType = MouseMovementType.RIGHT_CLICK_DRAG_MODE;
                    }
                    if (!master.isShiftPressed() && !master.isAltPressed() && master.isControlPressed()) {
                        mouseMovementType = MouseMovementType.RIGHT_CLICK_CTRL_MODE;
                    }
                }
            }


        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!movable) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (mouseMovementType == MouseMovementType.LEFT_CLICK_DRAG_MODE) {
                        setCoordinate(new Point2D(
                                xp.get() + xo.get() - e.getX() * getZoom() * intervalXMultiplier,
                                yp.get() + yo.get() - e.getY() * getZoom() * intervalYMultiplier
                        ));
                    }
                    fixCoordinate();
                    currWheelCoordinate.set(getCoordinate());
                }
            }

        });

    }

    /**
     * 영역을 이탈하지 못하도록 막습니다.
     */
    private void fixCoordinate() {
        setCoordinate(getFixedCoordinate(cx, cy));
    }

    /**
     * 위치가 정상적인지 판단
     */
    private Point2D getFixedCoordinate(Point2D coordinate) {
        return getFixedCoordinate(coordinate.x(), coordinate.y());
    }

    /**
     * 위치가 정상적인지 판단
     */
    private Point2D getFixedCoordinate(double cx, double cy) {
        int minX = Math.min(movingBoundaries.startX(), movingBoundaries.endX());
        int maxX = Math.max(movingBoundaries.startX(), movingBoundaries.endX());
        int minY = Math.min(movingBoundaries.startY(), movingBoundaries.endY());
        int maxY = Math.max(movingBoundaries.startY(), movingBoundaries.endY());
        return new Point2D(
                Math.max(minX, Math.min(maxX, cx)),
                Math.max(minY, Math.min(maxY, cy))
        );
    }

    protected final void setCoordinate(Point2D coordinate) {
        double x = coordinate.x();
        double y = coordinate.y();
        setCoordinate(x, y);
    }

    protected void setCoordinate(double cx, double cy) {

        double x = cx;
        double y = cy;

        if (Double.isNaN(x)) {
            x = this.cx;
        }
        if (Double.isNaN(y)) {
            y = this.cy;
        }

        this.cx = x;
        this.cy = y;

    }

    public void setMovingBoundaries(RectBounds movingBoundaries) {
        this.movingBoundaries = movingBoundaries;
    }

    protected void setRotation(double rotation) {
        this.rotation = rotation;
    }

    protected void zoom(double zoom, long millis, FunctionEase ease) {
        double start = this.zoom;

        if (millis == 0) {
            setZoom(zoom);
            return;
        }

        if (zoomThread != null) {
            zoomThread.interrupt();
            zoomThread = null;
        }
        zoomThread = TaskManager.runTask(() -> {
            try {
                double changeOffset = zoom - start;
                TaskManager.animate(millis, r -> {
                    setZoom(start + changeOffset * r);
                    fixCoordinate();
                }, ease);
                zoomThread = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    protected void intX(double intX, long millis, FunctionEase ease) {
        double start = this.intervalXMultiplier;

        if (millis == 0) {
            setIntervalXMultiplier(intX);
            return;
        }

        if (intXThread != null) {
            intXThread.interrupt();
            intXThread = null;
        }
        intXThread = TaskManager.runTask(() -> {
            try {
                double changeOffset = intX - start;
                TaskManager.animate(millis, r -> setIntervalXMultiplier(start + changeOffset * r), ease);
                intXThread = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    protected void intY(double intY, long millis, FunctionEase ease) {
        double start = this.intervalYMultiplier;

        if (millis == 0) {
            setIntervalYMultiplier(intY);
            return;
        }

        if (intYThread != null) {
            intYThread.interrupt();
            intYThread = null;
        }
        intYThread = TaskManager.runTask(() -> {
            try {
                double changeOffset = intY - start;
                TaskManager.animate(millis, r -> setIntervalYMultiplier(start + changeOffset * r), ease);
                intYThread = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    protected void move(Point2D moveTo) {
        move(moveTo, 0, e -> e);
    }

    /**
     * 해당 위치로 이동합니다.
     *
     * @param moveTo 이동할 좌표
     * @param millis 이동 기간
     * @param ease   가감속
     */
    protected void move(Point2D moveTo, long millis, FunctionEase ease) {

        Point2D start = getCoordinate();
        Point2D end = getFixedCoordinate(moveTo);

        double sx = start.x();
        double sy = start.y();

        double ex = end.x();
        double ey = end.y();


        if (millis == 0) {
            setCoordinate(end);
            return;
        }

        stopMove();

        moveThread = TaskManager.runTask(() -> {
            try {
                coordinateMoving = true;
                double cx = ex - sx;
                double cy = ey - sy;

                TaskManager.animate(millis, r -> setCoordinate(sx + cx * r, sy + cy * r), ease);
                moveThread = null;
                coordinateMoving = false;
            } catch (InterruptedException e) {
                coordinateMoving = false;
                Thread.currentThread().interrupt();
            }
        });
    }

    public boolean canVisible(double startX, double startY, double endX, double endY) {
        return canVisible(startX, startY, endX, endY, getWidth());
    }

    protected void stopMove() {
        if (moveThread != null) {
            moveThread.interrupt();
            moveThread = null;
        }
    }

    protected boolean canVisible(double startX, double startY, double endX, double endY, int xRes) {

        int yRes = (int) (xRes * getScreenRatio());

        Point startLocation = toLocation(startX, startY, xRes);
        Point endLocation = toLocation(endX, endY, xRes);

        int minX = Math.min(startLocation.x, endLocation.x);
        int maxX = Math.max(startLocation.x, endLocation.x);
        int minY = Math.min(startLocation.y, endLocation.y);
        int maxY = Math.max(startLocation.y, endLocation.y);

        return minX < xRes &&
               minY < yRes &&
               maxX > 0 &&
               maxY > 0;
    }

    /**
     * 줌에 따른 크기 조정
     */
    public int resize(double current) {
        return (int) resizeDouble(current);
    }

    /**
     * 줌에 따른 크기 조정
     */
    protected double resizeDouble(double current) {
        return current / zoom;
    }

    public double getScreenRatio() {
        return (double) getHeight() / getWidth();
    }

    public Rectangle convert(double startX, double startY, double endX, double endY) {
        Point startLocation = toLocation(startX, startY);
        int x = startLocation.x;
        int y = startLocation.y;
        Point endLocation = toLocation(endX, endY);
        int w = endLocation.x - x;
        int h = endLocation.y - y;

        return new Rectangle(x, y, w, h);
    }

    /**
     * 마우스 이동 타입
     */
    public enum MouseMovementType {
        LEFT_CLICK_DRAG_MODE, LEFT_CLICK_SHIFT_MODE, LEFT_CLICK_CTRL_MODE, RIGHT_CLICK_DRAG_MODE, RIGHT_CLICK_SHIFT_MODE, RIGHT_CLICK_CTRL_MODE
    }

    protected void fillRect(Graphics2D g, double sx, double sy, double ex, double ey) {
        fillRoundRect(g, sx, sy, ex, ey, 0);
    }

    public double getIntervalXMultiplier() {
        return intervalXMultiplier;
    }

    public double getIntervalYMultiplier() {
        return intervalYMultiplier;
    }

    protected void fillRoundRect(Graphics2D g, double sx, double sy, double ex, double ey, double arc) {
        Point start = toLocation(sx, sy);
        Point end = toLocation(ex, ey);
        int a = resize(arc);

        g.fillRoundRect(start.x, start.y, end.x - start.x, end.y - start.y, a, a);
    }

    protected void drawString(Graphics2D g, String s, double sx, double sy, double h) {
        Point start = toLocation(sx, sy);
        int fh = resize(h);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fh / 2));
        g.drawString(s, start.x + resize(4), start.y + 5 * fh / 8);
    }

    protected void drawLine(Graphics2D g, double sx, double sy, double ex, double ey) {
        Point start = toLocation(sx, sy);
        Point end = toLocation(ex, ey);
        g.drawLine(start.x, start.y, end.x, end.y);
    }

    protected void drawCurve(Graphics2D g, double sx, double sy, double ex, double ey, FunctionEase ease, int points) {
        Point start = toLocation(sx, sy);
        Point end = toLocation(ex, ey);

        Polygon p = new Polygon();
        int prevY = start.y;

        for (double ratio = 0; ratio <= 1; ratio += 1.0 / points) {
            int x = (int) AdvancedMath.ratioDivide(start.x, end.x, ratio);
            int y = (int) AdvancedMath.ratioDivide(start.y, end.y, ratio, ease);

            if (prevY != y) {
                p.addPoint(x, y);
                prevY = y;
            }
        }
        g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
    }

    protected void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setCanAxisZoom(boolean canAxisZoom) {
        this.canAxisZoom = canAxisZoom;
    }

    public void setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
    }

    public boolean isCoordinateMoving() {
        return coordinateMoving;
    }

    /**
     * 현재 좌표를 패널 위의 위치로 변환합니다.
     */
    protected Point toLocation(double cx, double cy) {
        return toLocation(cx, cy, getWidth());
    }

    protected Point toLocation(double cx, double cy, int xRes) {
        double x = (cx - this.cx) / zoom / intervalXMultiplier;
        double y = (cy - this.cy) / zoom / intervalYMultiplier;
        Point2D position = new Point2D.Builder(x, y).rotate(rotation).build();

        return new Point(
                (int) (position.x() + xRes / 2.0),
                (int) (position.y() + xRes / 2.0 * getScreenRatio())
        );
    }

    /**
     * 현재 패널 위의 위치를 좌표로 변환합니다.
     */
    public Point2D toCoordinate(double x, double y) {
        return toCoordinate(x, y, getWidth());
    }

    /**
     * 현재 패널 위의 위치를 좌표로 변환합니다.
     */
    protected Point2D toCoordinate(double x, double y, int xRes) {
        double cx = x - xRes / 2.0;
        double cy = y - xRes / 2.0 * getScreenRatio();

        return new Point2D.Builder(cx, cy)
                .rotate(-rotation)
                .multiply(zoom)
                .multiplyX(intervalXMultiplier)
                .multiplyY(intervalYMultiplier)
                .add(getCoordinate())
                .build();
    }

    public MouseMovementType getMouseMovementType() {
        return mouseMovementType;
    }

    public final Point2D getCoordinate() {
        return new Point2D(cx, cy);
    }

    public double getZoom() {
        return zoom;
    }

    public double getRotation() {
        return rotation;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        setCoordinate(getFixedCoordinate(cx, cy));
    }

    public void setIntervalXMultiplier(double intervalXMultiplier) {
        this.intervalXMultiplier = intervalXMultiplier;
    }

    public void setIntervalYMultiplier(double intervalYMultiplier) {
        this.intervalYMultiplier = intervalYMultiplier;
    }

}
