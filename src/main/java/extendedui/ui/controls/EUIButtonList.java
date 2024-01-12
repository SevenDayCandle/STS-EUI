package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.hitboxes.EUIHitbox;

import java.util.ArrayList;

public class EUIButtonList extends EUIBase {
    public static final float ICON_SIZE = scale(48);
    public static final float BUTTON_W = scale(250);
    public static final float BUTTON_H = scale(51);
    public static final float STARTING_X = scale(160);
    public static final float STARTING_Y = Settings.HEIGHT * 0.92f;
    public static final int DEFAULT_VISIBLE = 14;

    public final ArrayList<EUIButton> buttons = new ArrayList<>();
    private int highlightedIndex;
    private int topButtonIndex;
    private int visibleButtons;
    protected float xPos;
    protected float yPos;
    protected float fontScale = 0.8f;
    protected EUIButton downButton;
    protected EUIButton upButton;
    public float buttonWidth;
    public float buttonHeight;

    public EUIButtonList() {
        this(DEFAULT_VISIBLE, STARTING_X, STARTING_Y, BUTTON_W, BUTTON_H);
    }

    public EUIButtonList(int visibleCount, float xPos, float yPos, float buttonWidth, float buttonHeight) {
        final float y = yPos - (visibleCount + 1) * buttonHeight;
        visibleButtons = visibleCount;
        this.xPos = xPos;
        this.yPos = yPos;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        upButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new EUIHitbox(xPos - ICON_SIZE, y + (ICON_SIZE / 2), ICON_SIZE, ICON_SIZE))
                .setOnClick(__ -> setTopButtonIndex(topButtonIndex - 1));
        upButton.setActive(false);
        upButton.background.setRotation(-90);
        downButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new EUIHitbox(upButton.hb.cX + ICON_SIZE, upButton.getY(), ICON_SIZE, ICON_SIZE))
                .setOnClick(__ -> setTopButtonIndex(topButtonIndex + 1));
        downButton.setActive(false);
        downButton.background.setRotation(-90);
    }

    public EUIButton addButton(ActionT1<EUIButton> onClick, String title) {
        EUIButton button = new EUIButton(ImageMaster.COLOR_TAB_BAR, new EUIHitbox(buttonWidth, buttonHeight))
                .setLabel(FontHelper.buttonLabelFont, fontScale, title)
                .setButtonScale(1f, 1.5f)
                .setOnClick((b) -> {
                    selectButton(b);
                    onClick.invoke(b);
                });
        buttons.add(button);
        setTopButtonIndex(0);
        selectButton(buttons.get(0));
        return button;
    }

    public void clear() {
        buttons.clear();
        setTopButtonIndex(0);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (EUIButton b : buttons) {
            b.tryRenderCentered(sb);
        }
        upButton.tryRenderCentered(sb);
        downButton.tryRenderCentered(sb);
    }

    public void selectButton(EUIButton button) {
        for (int i = 0; i < buttons.size(); i++) {
            EUIButton b = buttons.get(i);
            if (b == button) {
                highlightedIndex = i;
                b.label.setColor(Settings.GREEN_TEXT_COLOR);
            }
            else {
                b.label.setColor(Color.WHITE);
            }
        }
    }

    public EUIButtonList setFontScale(float fontScale) {
        this.fontScale = fontScale;
        return this;
    }

    public void setTopButtonIndex(int index) {
        topButtonIndex = Math.max(0, index);
        int lastButtonIndex = topButtonIndex + visibleButtons;

        for (int i = 0; i < buttons.size(); i++) {
            if (i >= topButtonIndex && i < lastButtonIndex) {
                buttons.get(i).setPosition(xPos, yPos - (i - topButtonIndex) * buttonHeight).setActive(true);
            }
            else {
                buttons.get(i).setActive(false);
            }
        }

        upButton.setActive(topButtonIndex > 0);
        downButton.setActive(topButtonIndex < buttons.size() - visibleButtons);
    }

    @Override
    public void updateImpl() {
        for (EUIButton b : buttons) {
            b.tryUpdate();
        }
        upButton.tryUpdate();
        downButton.tryUpdate();
        updateNonMouseInput();
    }

    protected void updateNonMouseInput() {
        if (Settings.isControllerMode) {
            if (CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                buttons.get((highlightedIndex + 1) % buttons.size()).onLeftClick();
            }
            else if (CInputActionSet.pageLeftViewDeck.isJustPressed()) {
                buttons.get(highlightedIndex == 0 ? buttons.size() - 1 : highlightedIndex - 1).onLeftClick();
            }
        }
    }
}
