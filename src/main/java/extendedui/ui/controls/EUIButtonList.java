package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;

public class EUIButtonList extends EUIBase
{
    public static final float ICON_SIZE = Scale(40);
    public static final float BUTTON_W = Scale(200);
    public static final float BUTTON_H = Scale(51);
    public static final float STARTING_X = Scale(160);
    public static final float STARTING_Y = Settings.HEIGHT * 0.92f;
    public static final int DEFAULT_VISIBLE = 14;

    public final ArrayList<EUIButton> buttons = new ArrayList<>();
    protected float xPos;
    protected float yPos;
    protected float buttonWidth;
    protected float buttonHeight;
    protected int highlightedIndex;
    protected int topButtonIndex;
    protected int visibleButtons;
    protected EUIButton downButton;
    protected EUIButton upButton;

    public EUIButtonList()
    {
        this(DEFAULT_VISIBLE, STARTING_X, STARTING_Y, BUTTON_W, BUTTON_H);
    }

    public EUIButtonList(int visibleCount, float xPos, float yPos, float buttonWidth, float buttonHeight)
    {
        final float y = yPos - (visibleCount + 1) * buttonHeight;
        visibleButtons = visibleCount;
        this.xPos = xPos;
        this.yPos = yPos;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        upButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new AdvancedHitbox(xPos - ICON_SIZE, y + (ICON_SIZE / 2), ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex - 1))
                .SetText(null);
        upButton.background.SetRotation(-90);
        downButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new AdvancedHitbox(upButton.hb.cX + ICON_SIZE, upButton.getY(), ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex + 1))
                .SetText(null);
        downButton.background.SetRotation(-90);
    }

    @Override
    public void Update()
    {
        for (EUIButton b : buttons) {
            b.TryUpdate();
        }
        upButton.TryUpdate();
        downButton.TryUpdate();
        UpdateNonMouseInput();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        for (EUIButton b : buttons) {
            b.TryRenderCentered(sb);
        }
        upButton.TryRenderCentered(sb);
        downButton.TryRenderCentered(sb);
    }

    public void Clear()
    {
        buttons.clear();
        SetTopButtonIndex(0);
    }

    public void SetTopButtonIndex(int index) {
        topButtonIndex = Math.max(0, index);
        int lastButtonIndex = topButtonIndex + visibleButtons;

        for (int i = 0; i < buttons.size(); i++) {
            if (i >= topButtonIndex && i < lastButtonIndex) {
                buttons.get(i).SetPosition(xPos, yPos - (i - topButtonIndex) * buttonHeight).SetActive(true);
            }
            else {
                buttons.get(i).SetActive(false);
            }
        }

        upButton.SetActive(topButtonIndex > 0);
        downButton.SetActive(topButtonIndex < buttons.size() - visibleButtons);
    }

    public void SelectButton(EUIButton button)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            EUIButton b = buttons.get(i);
            if (b == button)
            {
                highlightedIndex = i;
                b.SetTextColor(Settings.GREEN_TEXT_COLOR);
            }
            else
            {
                b.SetTextColor(Color.WHITE);
            }
        }
    }

    public EUIButton AddButton(ActionT1<EUIButton> onClick, String title) {
        EUIButton button = new EUIButton(ImageMaster.COLOR_TAB_BAR, new AdvancedHitbox(buttonWidth, buttonHeight))
                .SetFont(FontHelper.buttonLabelFont, 0.8f)
                .SetButtonScale(1f, 1.2f)
                .SetOnClick((b) -> {
                    SelectButton(b);
                    onClick.Invoke(b);
                })
                .SetText(title);
        buttons.add(button);
        SetTopButtonIndex(0);
        SelectButton(buttons.get(0));
        return button;
    }

    protected void UpdateNonMouseInput()
    {
        if (Settings.isControllerMode) {
            if (CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                buttons.get((highlightedIndex + 1) % buttons.size()).OnLeftClick();
            } else if (CInputActionSet.pageLeftViewDeck.isJustPressed()) {
                buttons.get(highlightedIndex == 0 ? buttons.size() - 1 : highlightedIndex - 1).OnLeftClick();
            }
        }
    }
}
