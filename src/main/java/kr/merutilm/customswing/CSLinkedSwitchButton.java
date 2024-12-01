package kr.merutilm.customswing;

import kr.merutilm.base.functions.BooleanConsumer;

/**
 * <p>스위치를 서로 연결합니다.</p>
 * 무조건 한 개 이상의 버튼이 켜져 있어야 합니다.
 */
final class CSLinkedSwitchButton<B extends CSSwitchButton> {
    private boolean isOffingRemote;

    @SafeVarargs
    private CSLinkedSwitchButton(B firstButton, B... target) {

        firstButton.setOn(true);

        for (B button : target) {
            button.addAction(0, (BooleanConsumer) e -> {
                if (Boolean.TRUE.equals(e)) {
                    isOffingRemote = true;
                    for (B b : target) {
                        if (b != button) {
                            b.setOn(false);
                        }
                    }
                    isOffingRemote = false;
                } else {
                    if (!isOffingRemote) {
                        button.setOn(true);
                    }
                }

            });
        }
    }

    @SafeVarargs
    static <B extends CSSwitchButton> void link(B firstButton, B... target) {
        new CSLinkedSwitchButton<>(firstButton, target);
    }

}
