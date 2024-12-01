package kr.merutilm.customswing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

import kr.merutilm.base.functions.BooleanConsumer;
import kr.merutilm.base.selectable.BooleanValue;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.base.struct.PolarPoint;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.util.TaskManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.Serial;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CSValueInputGroupPanel extends CSAnimatablePanel {
    @Serial
    private static final long serialVersionUID = 6916652123156800538L;
    private final InputType type;
    private int createdInputs = 0;
    private final transient Point2D sizePerOneInput;
    private final int thisPanelWidth;
    private final boolean isSpecialValueParameter;
    private final int offY;
    private transient Point2D location;
    private final Color innerParameterColor = new Color(50, 50, 50);
    private final int panelOptionNameLength;
    private final int panelNameLength;
    private final JLabel optionName;
    private final List<CSValueTextInputPanel<?>> textInputs = new ArrayList<>();
    private final List<CSSelectionSwitchButton> listInputs = new ArrayList<>();
    private final CSPanel targetPanel;
    private boolean wheelListenerAdded = false;
    private final boolean showName;
    private final transient List<Runnable> propertyChangedAction = new ArrayList<>();

    public void addPropertyChangedAction(Runnable action) {
        propertyChangedAction.add(action);
    }

    /**
     * 입력값 중 하나라도 변동사항이 생기면 호출됩니다.
     */
    public void runPropertyChangedAction() {
        for (Runnable action : propertyChangedAction) {
            action.run();
        }
    }

    public CSPanel getTargetPanel() {
        return targetPanel;
    }

    public void count(){
        createdInputs++;
    }

    public Point2D locationInPanel(){
        return location;
    }

    public boolean isShowName(){
        return showName;
    }

    /**
     * Special Value 가 특정 이벤트의 파라미터로 들어갈 경우, 가로 길이 조정에 사용합니다.
     *
     * @param master    메인 프레임
     * @param type      가로,세로형 입력
     * @param location  해당 패널의 왼쪽 위 끝 좌표
     * @param nullable  null 가능 여부, 패널 하나당 좌표를 계산하는데만 사용된다
     * @param maxAmount 최대 입력판 개수
     */
    private CSValueInputGroupPanel(CSFrame master, CSPanel targetPanel, InputType type, Point2D location,
            boolean nullable, int maxAmount, boolean showName) {
        super(master);
        this.type = type;
        this.targetPanel = targetPanel;
        this.offY = (int) location.y();
        this.location = location;
        this.showName = showName;
        this.panelNameLength = showName ? 150 : 0;
        this.thisPanelWidth = targetPanel.getWidth() - panelNameLength; //파라미터로 들어갈 패널의 이름 길이가 150이다.
        this.panelOptionNameLength = (int) (thisPanelWidth * CSValuePanel.DEFAULT_INPUT_BOUNDS_RATIO + CSValuePanel.INTERVAL);
        this.sizePerOneInput = new Point2D((thisPanelWidth - (nullable ? CSValueInputPanel.NULLABLE_BUTTON_NAME_LENGTH : 0) - panelOptionNameLength) / (double) maxAmount, CSButton.BUTTON_HEIGHT);
        this.optionName = new JLabel();
        this.isSpecialValueParameter = true;

        setBackground(new Color(60, 60, 60));
        generate(new Point2D(0, 0)); //패널 안에 딱 맞아들어가기 때문에 무조건 0,0임
    }

    /**
     * 일반적인 이벤트의 파라미터를 넣을 때 사용
     */
    public CSValueInputGroupPanel(CSFrame master, CSPanel targetPanel, String name, InputType type, boolean showName) {
        super(master);
        this.panelNameLength = showName ? 150 : 0;
        this.panelOptionNameLength = panelNameLength;
        this.targetPanel = targetPanel;
        this.type = type;
        this.location = Point2D.ORIGIN;
        this.offY = 0;
        this.showName = showName;
        this.thisPanelWidth = targetPanel.getWidth();
        this.sizePerOneInput = new Point2D((double)thisPanelWidth - panelOptionNameLength, CSButton.BUTTON_HEIGHT);
        this.optionName = new JLabel(name);
        this.isSpecialValueParameter = false;

        setBackground(new Color(80, 80, 80));
        generate(location);
    }

    /**
     * constructor only
     */
    private void generate(Point2D location) {

        setLayout(null);
        setLocation((int) location.x(), (int) location.y());
        setSize(0, CSButton.BUTTON_HEIGHT);

        if (panelNameLength > 0) {
            optionName.setLayout(null);
            optionName.setHorizontalAlignment(SwingConstants.CENTER);
            optionName.setVerticalAlignment(SwingConstants.CENTER);
            optionName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            optionName.setForeground(new Color(220, 220, 220));
            optionName.setBounds(0, 0, panelNameLength, CSButton.BUTTON_HEIGHT);
            add(optionName);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component input : getComponents()) {
            input.setEnabled(enabled);
        }
        if (!enabled) {
            for (CSSelectionSwitchButton input : listInputs) {
                input.setOn(false);
            }
        }
    }

    @Override
    public void setLocation(int x, int y) {
        for (CSSelectionSwitchButton inputList : listInputs) {
            Point distance = new Point(inputList.getButtonList().getX() - getX(), inputList.getButtonList().getY() - getY());
            Point result = new Point(x + distance.x, y + distance.y);
            inputList.getButtonList().setLocation(result);
        }
        super.setLocation(x, y);
    }

    @Override
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    public List<CSValueTextInputPanel<?>> getTextInputs() {
        return Collections.unmodifiableList(textInputs);
    }

    public List<CSSelectionSwitchButton> getListInputs() {
        return Collections.unmodifiableList(listInputs);
    }

    /**
     * Create Nonnull Text Input.
     */
    public <R> CSValueTextInputPanel<R> createTextInput(String name, R firstValue, @Nonnull R defaultValueIfNull, Function<String, R> mapper, Consumer<R> enterFunction) {
        return create(name, firstValue, defaultValueIfNull, mapper, enterFunction, false);
    }

    /**
     * Create Nullable Text Input.
     */
    public <R> CSValueTextInputPanel<R> createNullableTextInput(String name, @Nullable R firstValue, @Nonnull R defaultValueIfNull, Function<String, R> mapper, Consumer<R> enterFunction) {
        return create(name, firstValue, defaultValueIfNull, mapper, enterFunction, true);
    }

    /**
     * Create Nonnull Select Input.
     */
    public <R> CSValueSelectionInputPanel<R> createSelectInput(String name, R firstValue, @Nonnull R defaultValueIfNull, R[] options, Consumer<R> enterFunction) {
        return create(name, firstValue, defaultValueIfNull, options, enterFunction, false);
    }

    /**
     * Create Nullable Boolean Input.
     */
    public <R> CSValueSelectionInputPanel<R> createNullableSelectInput(String name, @Nullable R firstValue, @Nonnull R defaultValue, R[] options, Consumer<R> enterFunction) {
        return create(name, firstValue, defaultValue, options, enterFunction, true);
    }

    /**
     * Create Nonnull Boolean Input.
     */
    public void createBoolInput(String name, Boolean firstValue, Boolean defaultValueIfNull, BooleanConsumer enterFunction) {
        Consumer<BooleanValue> e = value -> enterFunction.accept(value.bool());
        create(name, firstValue == null ? null : BooleanValue.typeOf(String.valueOf(firstValue)), BooleanValue.typeOf(String.valueOf(defaultValueIfNull)), BooleanValue.values(), e, false);
    }

    /**
     * Create Nullable Boolean Input.
     */
    public void createNullableBoolInput(String name, Boolean firstValue, Boolean defaultValue, Consumer<Boolean> enterFunction) {
        Consumer<BooleanValue> e = value -> enterFunction.accept(value == null ? defaultValue : value.bool());
        create(name, firstValue == null ? null : BooleanValue.typeOf(String.valueOf(firstValue)), BooleanValue.typeOf(String.valueOf(defaultValue)), BooleanValue.values(), e, true);
    }

    /**
     * Create Nonnull Template Input.
     */
    public <S extends Struct<?>> CSValuePanel createTemplateInput(String name, S firstValue,
            @Nonnull S defaultValueIfNull, Class<S> classType, Consumer<S> enterFunction,
            TemplateProvider<S> templateIfNoMatch) {
        return create(name, firstValue, defaultValueIfNull, classType, enterFunction, false, templateIfNoMatch);
    }

    /**
     * Create Nullable Template Input.
     */
    public <S extends Struct<?>> CSValuePanel createNullableTemplateInput(String name, @Nullable S firstValue,
            @Nonnull S defaultValue, Class<S> classType, Consumer<S> enterFunction,
            TemplateProvider<S> templateIfNoMatch) {
        return create(name, firstValue, defaultValue, classType, enterFunction, true, templateIfNoMatch);
    }

    public void createTitle(String name) {
        Rectangle r1 = getRectInputSet();
        createdInputs++;
        getRectInputSet();
        createdInputs++;
        CSPanel panel = new CSPanel(getMainFrame());
        panel.setBounds(r1.x, r1.y, r1.width, r1.height * 2);
        panel.setName(name);
        panel.getNameLabel().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        panel.getNameLabel().setForeground(Color.WHITE);
        panel.setBackground(new Color(32, 32, 32));
        add(panel);
    }

    private <R> CSValueTextInputPanel<R> create(String name, @Nullable R firstValue, @Nonnull R defaultValue, Function<String, R> mapper, Consumer<R> enterFunction, boolean nullable) {
        Rectangle r = getRectInputSet();
        Consumer<R> finalEnterFunction = enterFunction.andThen(e -> runPropertyChangedAction());
        CSValueTextInputPanel<R> valueInputPanel = new CSValueTextInputPanel<>(getMainFrame(), r, name, firstValue == null ? defaultValue : firstValue, mapper, nullable) {
            @Serial
            private static final long serialVersionUID = 5085276561989404744L;

            @Override
            public void enterFunction(R value) {
                finalEnterFunction.accept(value);
            }
        };
        if (isSpecialValueParameter) {
            valueInputPanel.setBackground(innerParameterColor);
        }
        if (nullable) {
            valueInputPanel.addNullAction(e -> {
                if (Boolean.TRUE.equals(e)) {
                    finalEnterFunction.accept(null);
                } else {
                    finalEnterFunction.accept(firstValue == null ? defaultValue : firstValue);
                }
            });

            valueInputPanel.changeNull(firstValue == null);
        }
        add(valueInputPanel);
        textInputs.add(valueInputPanel);
        createdInputs++;
        return valueInputPanel;
    }

    private <R> CSValueSelectionInputPanel<R> create(String name, @Nullable R firstValue, @Nonnull R defaultValue, R[] options, Consumer<R> enterFunction, boolean nullable) {
        Rectangle r = getRectInputSet();

        Point2D location = this.location.add(new Point2D(r.x, r.y));
        Consumer<R> finalEnterFunction = enterFunction.andThen(e -> runPropertyChangedAction());

        CSValueSelectionInputPanel<R> valueInputPanel = new CSValueSelectionInputPanel<>(getMainFrame(), targetPanel, new Point((int) location.x(), (int) location.y()), r, name, firstValue == null ? defaultValue : firstValue, finalEnterFunction, nullable, options);
        if (isSpecialValueParameter) {
            valueInputPanel.setBackground(innerParameterColor);
        }


        if (nullable) {
            valueInputPanel.addNullAction(e -> {
                if (Boolean.TRUE.equals(e)) {
                    finalEnterFunction.accept(null);
                } else {
                    valueInputPanel.setDefault();
                    finalEnterFunction.accept(firstValue == null ? defaultValue : firstValue);
                }
            });

            valueInputPanel.changeNull(firstValue == null);
        }
        add(valueInputPanel);
        if (valueInputPanel.getSelectionSwitchButton() != null) {
            listInputs.add(valueInputPanel.getSelectionSwitchButton());
        }
        createdInputs++;
        return valueInputPanel;
    }


    private <S extends Struct<?>> CSValuePanel create(String name, @Nullable S firstValue, @Nonnull S defaultValue,
            Class<S> classType, Consumer<S> enterFunction, boolean nullable, TemplateProvider<S> templateIfNoMatch) {
        Rectangle r = getRectInputSet();
        CSValueInputPanel valueInputPanel = new CSValueInputPanel(getMainFrame(), r, name, nullable);
        CSValueInputGroupPanel templatePanel = structTemplate(getMainFrame(), targetPanel, firstValue, defaultValue,
                location.add(new Point2D(r.x, r.y)), classType, enterFunction, nullable, showName, templateIfNoMatch);

        templatePanel.addPropertyChangedAction(this::runPropertyChangedAction);
        valueInputPanel.add(templatePanel);

        textInputs.addAll(templatePanel.getTextInputs()); // 모든 텍스트 패널 흡수
        listInputs.addAll(templatePanel.getListInputs()); // 모든 리스트 패널 흡수

        valueInputPanel.addEnabledAction(e -> templatePanel.setEnabled(valueInputPanel.isEnabled()));
        valueInputPanel.add(templatePanel);

        if (nullable) {
            valueInputPanel.addNullAction(e -> {
                valueInputPanel.setEnabled(!e);
                if (Boolean.TRUE.equals(e)) {
                    enterFunction.accept(null);
                } else {
                    enterFunction.accept(firstValue == null ? defaultValue : firstValue);
                }

                for (CSValueTextInputPanel<?> textInput : templatePanel.getTextInputs()) {
                    textInput.getNullAction().forEach(f -> f.accept(e));
                }
                runPropertyChangedAction();
            });
            valueInputPanel.changeNull(firstValue == null);
        }

        add(valueInputPanel);
        count();
        return valueInputPanel;
    }

    /**
     * 모든 입력 닫기
     */
    public void closeAllOpenedInputs() {
        closeAllOpenedTextInputs();
        closeAllOpenedListInputs();
    }

    void closeAllOpenedTextInputs() {
        for (CSValueTextInputPanel<?> textInput : getTextInputs()) {
            if (textInput.isEnabled()) {
                textInput.setEnabled(false);
                textInput.setEnabled(true);
            }
        }
    }

    void closeAllOpenedListInputs() {
        for (CSSelectionSwitchButton inputList : getListInputs()) {
            inputList.setOn(false);
        }
    }

    public static <S extends Struct<?>> CSValueInputGroupPanel structTemplate(CSFrame master, CSPanel targetPanel,
            @Nullable S firstValue, @Nonnull S defaultValue, Point2D location, Class<S> classType,
            Consumer<S> enterFunction, boolean nullable, boolean showName, TemplateProvider<S> templateIfNoMatch) {

        S value = firstValue == null ? defaultValue : firstValue;
        CSValueInputGroupPanel groupPanel = new CSValueInputGroupPanel(master, targetPanel,
                CSValueInputGroupPanel.InputType.HORIZONTAL, location, nullable,
                Arrays.stream(classType.getDeclaredFields()).filter(v -> !Modifier.isStatic(v.getModifiers())).toList()
                        .size(),
                showName);

        if (value instanceof HexColor s) {

                HexColor.Builder edit = s.edit();
                groupPanel.createTextInput("R", s.r(), 255, Integer::parseInt, e -> {
                    edit.setR(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("G", s.g(), 255, Integer::parseInt, e -> {
                    edit.setG(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("B", s.b(), 255, Integer::parseInt, e -> {
                    edit.setB(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("A", s.a(), 255, Integer::parseInt, e -> {
                    edit.setA(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
            } else if (value instanceof ImageFile s) {
                ImageFile.Builder edit = s.edit();

                CSValueTextInputPanel<String> textInputPanel = groupPanel.createTextInput("Image Name", s.name(), "",
                        v -> v, e -> {
                            edit.setName(e);
                            enterFunction.accept(classType.cast(edit.build()));
                        });

            new CSFileDropper<>(textInputPanel.getTextPanel(), e -> {
            }, new CSFileDropper.DropActions<>("png", e -> {
            }, (e, file) -> {
                edit.setName(file.getName());
                e.setText(file.getName());
                enterFunction.accept(classType.cast(edit.build()));
                groupPanel.runPropertyChangedAction();
            }));
        } else if (value instanceof Point2D s) {
            Point2D.Builder edit = s.edit();
            groupPanel.createTextInput("x", s.x(), 0.0, Double::parseDouble, e -> {
                edit.setX(e);
                enterFunction.accept(classType.cast(edit.build()));
            });
            groupPanel.createTextInput("y", s.y(), 0.0, Double::parseDouble, e -> {
                edit.setY(e);
                enterFunction.accept(classType.cast(edit.build()));
            });
        } else if (value instanceof Point3D s) {
            Point3D.Builder edit = s.edit();
                groupPanel.createTextInput("x", s.x(), 0.0, Double::parseDouble, e -> {
                    edit.setX(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("y", s.y(), 0.0, Double::parseDouble, e -> {
                    edit.setY(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("z", s.z(), 0.0, Double::parseDouble, e -> {
                    edit.setZ(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
        } else if (value instanceof PolarPoint s) {
                PolarPoint.Builder edit = s.edit();
                groupPanel.createTextInput("radius", s.radius(), 0.0, Double::parseDouble, e -> {
                    edit.setRadius(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createTextInput("angle", s.angle(), 0.0, Double::parseDouble, e -> {
                    edit.setAngle(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
        } else {
                templateIfNoMatch.provide(value, groupPanel, classType, enterFunction);
        }
            
        return groupPanel;
    }

    public Rectangle getRectInputSet() {
        Rectangle r;
        Point2D size;
        int xSize = (int) sizePerOneInput.x();

        if (type == InputType.HORIZONTAL) {
            size = new Point2D(sizePerOneInput.x() * (createdInputs + 1), sizePerOneInput.y());
            r = new Rectangle((int) sizePerOneInput.x() * createdInputs + panelOptionNameLength, 0, xSize, (int) sizePerOneInput.y());
        } else {
            size = new Point2D(sizePerOneInput.x(), sizePerOneInput.y() * (createdInputs + 1));
            r = new Rectangle(panelOptionNameLength, (int) sizePerOneInput.y() * createdInputs, xSize, (int) sizePerOneInput.y());
        }

        setSize((int) size.x() + panelOptionNameLength, (int) size.y());
        optionName.setSize(panelOptionNameLength, getHeight());

        if (!isSpecialValueParameter && !wheelListenerAdded && size.y() > targetPanel.getHeight() - location.y()) {
            addMouseWheelListener(new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int add;
                    if (e.getWheelRotation() > 0) { //down
                        add = -100;
                    } else { //up
                        add = 100;
                    }

                    if (offY < location.y() + add) {
                        location = new Point2D(0, offY);
                    } else if (location.y() + add < (double) targetPanel.getHeight()
                            - createdInputs * CSButton.BUTTON_HEIGHT) {
                        location = location.edit()
                                .setY((double) targetPanel.getHeight() - createdInputs * CSButton.BUTTON_HEIGHT)
                                .build();
                    } else {
                        location = location.add(0, add);
                    }
                    TaskManager.runTask(() -> moveAnimation(100, location, Ease.LINEAR.fun()));
                }
            });
            wheelListenerAdded = true;
        }

        return r;
    }

    public enum InputType {
        HORIZONTAL, VERTICAL
    }

    public interface TemplateProvider<S extends Struct<?>> {
        void provide(S value, CSValueInputGroupPanel groupPanel, Class<S> classType, Consumer<S> enterFunction);
    }

}
