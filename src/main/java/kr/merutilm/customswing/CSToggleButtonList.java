package kr.merutilm.customswing;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.HexColor;

import java.awt.*;
import java.io.Serial;
import java.util.function.BiConsumer;

public class CSToggleButtonList extends CSPanel {
    @Serial
    private static final long serialVersionUID = -2795189344219865079L;
    private static final int BAR_WIDTH = 10;
    public static final int DEFAULT_LIST_HEIGHT = 150;

    private final CSToggleButton[] buttonList;

    /**
     * <strong>버튼 리스트</strong>
     * <p>
     * <p>
     * 버튼 리스트를 보여줍니다.<p>
     * 일정 수량 이상 많아질 경우 스크롤바가 생성됩니다.<p>
     */
    public CSToggleButtonList(CSFrame master, Rectangle bounds, CSToggleButton... buttonList) {

        super(master);
        this.buttonList = buttonList;

        setBaseColor(HexColor.BLACK);
        setEnabled(true);
        setLayout(null);
        setVisible(false);
        setLocation(bounds.getLocation());

        int width = bounds.width;
        int height = Math.min(bounds.height, CSButton.BUTTON_HEIGHT * buttonList.length);
        boolean isOutOfBounds = height < CSButton.BUTTON_HEIGHT * buttonList.length;

        setSize(width, height);

        if (isOutOfBounds) {
            CSScrollBar scrollBar = getScrollBar(buttonList, height, width);
            add(scrollBar);
        }

        for (int i = 0; i < buttonList.length; i++) {
            buttonList[i].setBounds(0, i * CSButton.BUTTON_HEIGHT, isOutOfBounds ? width - BAR_WIDTH : width, CSButton.BUTTON_HEIGHT);
            add(buttonList[i]);
        }
    }

    @Nonnull
    private CSScrollBar getScrollBar(CSToggleButton[] buttonList, int height, int width) {
        int barHeight = height * height / (buttonList.length * CSButton.BUTTON_HEIGHT);

        CSScrollBar scrollBar = new CSScrollBar(getMainFrame(), new Rectangle(width - BAR_WIDTH, 0, BAR_WIDTH, height), this, barHeight, CSScrollBar.Type.VERTICAL) {
            @Serial
            private static final long serialVersionUID = -3189537688154255003L;

            @Override
            void scrollValue(double ratio) {
                int moveOffset = (int) (ratio * (buttonList.length * CSButton.BUTTON_HEIGHT - height));
                for (int i = 0; i < buttonList.length; i++) {
                    buttonList[i].setBounds(0, i * CSButton.BUTTON_HEIGHT - moveOffset, width - BAR_WIDTH, CSButton.BUTTON_HEIGHT);
                }
            }
        };
        scrollBar.setEnabled(true);
        return scrollBar;
    }

    public void addSelectAction(BiConsumer<Integer, CSToggleButton> action) {
        for (int i = 0; i < buttonList.length; i++) {
            CSToggleButton button = buttonList[i];
            int index = i;
            button.addLeftClickAction(e -> action.accept(index, e));
        }
    }
}
