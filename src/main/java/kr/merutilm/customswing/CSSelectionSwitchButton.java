package kr.merutilm.customswing;

import java.awt.*;
import java.io.Serial;
import java.util.Arrays;
import java.util.function.Consumer;

public class CSSelectionSwitchButton extends CSSwitchButton {

    @Serial
    private static final long serialVersionUID = 8009757625520604470L;
    private static final int ADDITIONAL_Y = 100;
    private final CSToggleButtonList buttonList;

    /**
     * <strong>값 선택 스위치</strong>
     * <p>
     * <p>
     * 스위치를 킬 시 특정 명령을 수행하는 버튼 여러개를 생성합니다.<p>
     * 버튼 클릭 후, 타겟 스위치는 꺼집니다.
     */
    @SafeVarargs
    <R> CSSelectionSwitchButton(CSFrame master, CSPanel targetPanel, Point locationOnOptionPanel, Rectangle selectOptionMasterPanelBounds, String name, Consumer<R> enterFunction, R... options) {
        super(master, selectOptionMasterPanelBounds, name);

        CSToggleButton[] buttons = Arrays.stream(options).map(s -> new CSToggleButton(master, s.toString())).toArray(CSToggleButton[]::new);


        buttonList = new CSToggleButtonList(master, new Rectangle(0, 0, (int) selectOptionMasterPanelBounds.getWidth(), (int) selectOptionMasterPanelBounds.getHeight() + ADDITIONAL_Y), buttons);

        buttonList.addSelectAction((i, e) -> {
            setSwitchButtonName(options[i].toString());
            enterFunction.accept(options[i]);
            setOn(false);
        });

        addAction(buttonList::setVisible);


        Point finalLocation = selectOptionMasterPanelBounds.getLocation();
        finalLocation.translate(locationOnOptionPanel.x, locationOnOptionPanel.y + BUTTON_HEIGHT);
        buttonList.setLocation(finalLocation);
        
        targetPanel.add(buttonList, 0);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (buttonList != null) {
            buttonList.setVisible(false);
        }
    }

    public CSToggleButtonList getButtonList() {
        return buttonList;
    }

    void setSwitchButtonName(String name) {
        setName(name);
    }

}
