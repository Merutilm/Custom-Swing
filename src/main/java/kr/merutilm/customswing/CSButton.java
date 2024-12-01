package kr.merutilm.customswing;


import java.io.Serial;

import kr.merutilm.base.struct.HexColor;

public abstract class CSButton extends CSPanel {
    @Serial
    private static final long serialVersionUID = 5533840164140798042L;
    public static final int BUTTON_HEIGHT = 25;

    static final HexColor COLOR_DEFAULT_TEXT = HexColor.get(200, 200, 200);
    static final HexColor COLOR_DEFAULT_BACKGROUND = HexColor.get(90, 90, 90);
    static final HexColor COLOR_PRESS_TEXT = HexColor.get(180, 180, 180);
    static final HexColor COLOR_PRESS_BACKGROUND = HexColor.get(70, 70, 70);
    static final HexColor COLOR_DISABLED_TEXT = HexColor.get(150, 150, 150);
    static final HexColor COLOR_DISABLED_BACKGROUND = HexColor.get(40, 40, 40);

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            colorDefault();
        } else {
            colorDisable();
        }
    }

    CSButton(CSFrame master) {
        super(master);
        setEnabled(true);
    }

    protected void colorDefault() {
        setMergerTextColor(COLOR_DEFAULT_TEXT);
        setMergerColor(COLOR_DEFAULT_BACKGROUND);
    }

    protected void colorPress() {
        setMergerTextColor(COLOR_PRESS_TEXT);
        setMergerColor(COLOR_PRESS_BACKGROUND);
    }

    protected void colorDisable() {
        setMergerTextColor(COLOR_DISABLED_TEXT);
        setMergerColor(COLOR_DISABLED_BACKGROUND);
    }
}
