package kr.merutilm.customswing;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.merutilm.base.functions.BooleanConsumer;
import kr.merutilm.base.struct.RectBounds;

public class CSValueInputPanel extends CSValuePanel {
    static final int INTERVAL = 2;
    @Serial
    private static final long serialVersionUID = 4493251426742421308L;

    static final int NULLABLE_BUTTON_NAME_LENGTH = 40;
    CSSwitchButton nullChangeButton;

    public final boolean nullable;

    /**
     * <strong>값 입력 패널</strong>
     * <p></p>
     * <p></p>
     * 값 입력 템플릿입니다.
     */
    public CSValueInputPanel(CSFrame master, Rectangle valueInputMasterPanelBounds, String name, boolean nullable) {
        super(master, valueInputMasterPanelBounds, name);
        this.nullable = nullable;

        if (nullable) {
            nullChangeButton = new CSSwitchButton(master, new RectBounds((int) valueInputMasterPanelBounds.getWidth() - NULLABLE_BUTTON_NAME_LENGTH + INTERVAL, INTERVAL, (int) valueInputMasterPanelBounds.getWidth() - INTERVAL, (int) valueInputMasterPanelBounds.getHeight() - INTERVAL).convertToRectangle(), "NULL");
            nullChangeButton.addAction(this::changeNull);
            add(nullChangeButton);
        }
    }

    public void changeNull(boolean b) {
        setEnabled(!b);
        if (nullChangeButton != null && nullChangeButton.isOn() != b) {
            nullChangeButton.setOn(b);
            return;
        }
        nullAction.forEach(e -> e.accept(b));
    }

    private final transient List<BooleanConsumer> nullAction = new ArrayList<>();

    public List<BooleanConsumer> getNullAction() {
        return Collections.unmodifiableList(nullAction);
    }

    public void addNullAction(BooleanConsumer nullAction) {
        this.nullAction.add(nullAction);
    }

}
