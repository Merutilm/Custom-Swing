package kr.merutilm.customswing;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.TaskManager;

public class CSAnimatablePanel extends CSPanel {
    private transient Thread moveThread;
    private boolean moving;

    public CSAnimatablePanel(CSFrame main) {
        super(main);
    }

    public boolean isStopped(){
        return !moving;
    }

    public void moveAnimation(long millis, Point2D moveTo, FunctionEase ease) {
        if (millis == 0) {
            setLocation((int) moveTo.x(), (int) moveTo.y());
            return;
        }
        stopAnimation();
        moveThread = TaskManager.runTask(() -> {
            try {
                Point2D start =  getPanelLocation();
                Point2D changeOffset = moveTo.add(start.invert());
                moving = true;
                TaskManager.animate(millis, r -> {
                    Point2D cur = start.add(changeOffset.multiply(r));
                    setLocation((int) cur.x(), (int) cur.y());
                }, ease);
                moving = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void stopAnimation() {
        if (moveThread == null) {
            return;
        }
        moveThread.interrupt();
    }

}
