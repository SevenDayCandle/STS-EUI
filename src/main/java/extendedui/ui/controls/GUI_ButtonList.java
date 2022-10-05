package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.GUI_Base;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;

public class GUI_ButtonList extends GUI_Base
{
    public static final float ICON_SIZE = Scale(40);
    public static final float BUTTON_W = Scale(200);
    public static final float BUTTON_H = Scale(51);
    public static final float STARTING_X = Scale(160);
    public static final float STARTING_Y = Settings.HEIGHT * 0.92f;
    public static final int DEFAULT_VISIBLE = 14;

    public final ArrayList<GUI_Button> buttons = new ArrayList<>();
    protected float xPos;
    protected float yPos;
    protected float buttonWidth;
    protected float buttonHeight;
    protected int highlightedIndex;
    protected int topButtonIndex;
    protected int visibleButtons;
    protected GUI_Button downButton;
    protected GUI_Button upButton;

    public GUI_ButtonList()
    {
        this(DEFAULT_VISIBLE, STARTING_X, STARTING_Y, BUTTON_W, BUTTON_H);
    }

    public GUI_ButtonList(int visibleCount, float xPos, float yPos, float buttonWidth, float buttonHeight)
    {
        final float y = yPos - (visibleCount + 1) * buttonHeight;
        visibleButtons = visibleCount;
        this.xPos = xPos;
        this.yPos = yPos;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        upButton = new GUI_Button(ImageMaster.CF_LEFT_ARROW, new AdvancedHitbox(xPos - ICON_SIZE, y + (ICON_SIZE / 2), ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex - 1))
                .SetText(null);
        upButton.background.SetRotation(-90);
        downButton = new GUI_Button(ImageMaster.CF_RIGHT_ARROW, new AdvancedHitbox(upButton.hb.cX + ICON_SIZE, upButton.getY(), ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex + 1))
                .SetText(null);
        downButton.background.SetRotation(-90);
    }

    @Override
    public void Update()
    {
        for (GUI_Button b : buttons) {
            b.TryUpdate();
        }
        upButton.TryUpdate();
        downButton.TryUpdate();
        UpdateNonMouseInput();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        for (GUI_Button b : buttons) {
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

    public void SelectButton(GUI_Button button)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            GUI_Button b = buttons.get(i);
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

    public GUI_Button AddButton(ActionT1<GUI_Button> onClick, String title) {
        GUI_Button button = new GUI_Button(ImageMaster.COLOR_TAB_BAR, new AdvancedHitbox(buttonWidth, buttonHeight))
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
                SelectButton(buttons.get((highlightedIndex + 1) % buttons.size()));
            } else if (CInputActionSet.pageLeftViewDeck.isJustPressed()) {
                SelectButton(buttons.get(highlightedIndex == 0 ? buttons.size() - 1 : highlightedIndex - 1));
            }
        }
    }
}
