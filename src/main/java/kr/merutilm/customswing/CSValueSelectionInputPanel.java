package kr.merutilm.customswing;

import javax.annotation.Nonnull;

import kr.merutilm.base.functions.BooleanConsumer;
import kr.merutilm.base.struct.RectBounds;

import java.awt.*;
import java.io.Serial;
import java.util.function.Consumer;

public class CSValueSelectionInputPanel<R> extends CSValueInputPanel {
    @Serial
    private static final long serialVersionUID = 8911047532069755643L;
    private final CSSelectionSwitchButton optionValue;
    private final transient R defaultValue;
    private final CSSwitchButton[] buttonList;

    @SafeVarargs
    public CSValueSelectionInputPanel(CSFrame master, CSPanel targetPanel, Rectangle valueInputMasterPanelBounds,
            String name, @Nonnull R firstValue, Consumer<R> enterFunction, boolean nullable, R... options) {
        this(master, targetPanel, valueInputMasterPanelBounds.getLocation(), valueInputMasterPanelBounds, name,
                firstValue, enterFunction, nullable, options);
    }
    /**
     * <strong>선택 패널</strong>
     * <p></p>
     * <p></p>
     * <p>스위치를 누르면 Selectable 인스턴스를 가진 버튼이 여러개 등장합니다.</p>
     * <p>버튼을 클릭하면 해당 버튼이 가진 명령이 실행됨과 동시에 스위치가 닫힙니다.</p>
     * <p>해당 버튼의 이름으로 스위치 이름이 바뀝니다.</p>
     * 단, 선택지가 일정 개수 이하일 경우 위 사항을 모두 무시하고 버튼 여러개로 표시합니다
     */
    @SafeVarargs
    public CSValueSelectionInputPanel(CSFrame master, CSPanel targetPanel, Point locationOnOptionPanel, Rectangle valueInputMasterPanelBounds, String name, @Nonnull R firstValue, Consumer<R> enterFunction, boolean nullable, R... options) {
        super(master, valueInputMasterPanelBounds, name, nullable);

        RectBounds rectBounds = new RectBounds((int) (INTERVAL + valueInputMasterPanelBounds.getWidth() * DEFAULT_INPUT_BOUNDS_RATIO), INTERVAL, (int) valueInputMasterPanelBounds.getWidth() - (nullable ? NULLABLE_BUTTON_NAME_LENGTH : INTERVAL), (int) valueInputMasterPanelBounds.getHeight() - INTERVAL);

        int filter = 5;

        this.defaultValue = firstValue;

        if (options.length <= filter && firstValue instanceof Enum<?> selectable) {
            Rectangle rect = rectBounds.convertToRectangle();

            filter = options.length;

            int w = rect.width / filter;

            buttonList = new CSSwitchButton[filter];

            for (int i = 0; i < filter; i++) {

                int x1 = rect.x + w * i;

                CSSwitchButton button = new CSSwitchButton(master, new Rectangle(x1, rect.y, w, rect.height), options[i].toString());
                add(button);
                buttonList[i] = button;
    
                
            }
            CSLinkedSwitchButton.link(buttonList[selectable.ordinal()], buttonList);

            for (int i = 0; i < filter; i++) {
                int index = i;
                buttonList[i].addAction((BooleanConsumer) e -> {
                    if (Boolean.TRUE.equals(e)) {
                        enterFunction.accept(options[index]);
                    }
                });
            }

            optionValue = null;

        } else {
            buttonList = null;
            optionValue = new CSSelectionSwitchButton(master, targetPanel, locationOnOptionPanel, rectBounds.convertToRectangle(), firstValue.toString(), enterFunction, options);
            add(optionValue);
        }
    }

    void setDefault() {
        if (optionValue != null) {
            optionValue.setSwitchButtonName(defaultValue.toString());
        }
        if (buttonList != null && defaultValue instanceof Enum<?> selectable) {
            buttonList[selectable.ordinal()].setOn(true);
        }
    }

    CSSelectionSwitchButton getSelectionSwitchButton() {
        return optionValue;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (optionValue != null) {
            optionValue.setEnabled(enabled);
        }
        if (buttonList != null) {
            for (CSSwitchButton button : buttonList) {
                button.setEnabled(enabled);
            }
        }
    }

    @Override
    public void changeNull(boolean b) {
        super.changeNull(b);
        setEnabled(!b);
    }
}

