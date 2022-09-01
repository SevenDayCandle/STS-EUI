package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.FakeLibraryCard;
import extendedui.utilities.Mathf;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class GenericFilters<T> extends GUI_CanvasGrid
{
    protected static final Color FADE_COLOR = new Color(0f, 0f, 0f, 0.84f);
    public static final float SPACING = Settings.scale * 22.5f;
    public static final float DRAW_START_X = (float) Settings.WIDTH * 0.15f;
    public static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.87f;
    public static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    public static final float PAD_Y = Scale(45);
    public static final int ROW_SIZE = 8;
    protected int currentTotal;
    protected ActionT1<CardKeywordButton> onClick;
    protected final HashMap<EUITooltip, Integer> CurrentFilterCounts = new HashMap<>();
    protected final ArrayList<CardKeywordButton> FilterButtons = new ArrayList<>();
    protected final AdvancedHitbox hb;
    public final GUI_Button closeButton;
    public final GUI_Button clearButton;
    public final GUI_Label currentTotalHeaderLabel;
    public final GUI_Label currentTotalLabel;
    public final GUI_Label keywordsSectionLabel;
    public final GUI_Toggle sortTypeToggle;
    public final GUI_Toggle sortDirectionToggle;
    protected ArrayList<T> referenceItems;

    protected float draw_x;
    protected boolean invalidated;
    protected boolean isAccessedFromCardPool;
    private boolean shouldSortByCount;
    private boolean sortDesc;

    public GenericFilters()
    {
        super(ROW_SIZE, PAD_Y);
        isActive = false;
        hb = new AdvancedHitbox(DRAW_START_X, DRAW_START_Y, Scale(180), Scale(70)).SetIsPopupCompatible(true);
        closeButton = new GUI_Button(EUIRM.Images.HexagonalButton.Texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).SetIsPopupCompatible(true))
                .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.WHITE)
                .SetPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).SetText(CombatRewardScreen.TEXT[6])
                .SetOnClick(this::Close)
                .SetColor(Color.GRAY);
        clearButton = new GUI_Button(EUIRM.Images.HexagonalButton.Texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f).SetIsPopupCompatible(true))
                .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.WHITE)
                .SetColor(Color.FIREBRICK)
                .SetPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.13f).SetText("Clear")
                .SetOnClick(() -> this.Clear(true, isAccessedFromCardPool));

        keywordsSectionLabel = new GUI_Label(EUIFontHelper.CardTitleFont_Small,
                new AdvancedHitbox(0, 0, Scale(48), Scale(48)))
                .SetFont(EUIFontHelper.CardTitleFont_Small, 0.8f)
                .SetText(EUIRM.Strings.UI_Keywords)
                .SetColor(Settings.GOLD_COLOR)
                .SetAlignment(0.5f, 0.0f, false);
        currentTotalHeaderLabel = new GUI_Label(EUIFontHelper.CardTitleFont_Normal,
                new AdvancedHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.94f, Scale(48), Scale(48)))
                .SetFont(EUIFontHelper.CardTitleFont_Small, 1f)
                .SetText(EUIRM.Strings.UI_Total)
                .SetColor(Settings.GOLD_COLOR)
                .SetAlignment(0.5f, 0.0f, false);
        currentTotalLabel = new GUI_Label(EUIFontHelper.CardTitleFont_Normal,
                new AdvancedHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.906f, Scale(48), Scale(48)))
                .SetFont(EUIFontHelper.CardTitleFont_Small, 1f)
                .SetText("")
                .SetColor(Settings.BLUE_TEXT_COLOR)
                .SetAlignment(0.5f, 0.0f, false);

        sortTypeToggle = new GUI_Toggle( new AdvancedHitbox(0, 0, Scale(170), Scale(32)).SetIsPopupCompatible(true))
                .SetBackground(EUIRM.Images.RectangularButton.Texture(), Color.DARK_GRAY)
                .SetTickImage(null, null, 10)
                .SetFont(EUIFontHelper.CardDescriptionFont_Normal, 0.7f)
                .SetText(EUIRM.Strings.Misc_SortByCount)
                .SetOnToggle(val -> {
                    shouldSortByCount = val;
                    RefreshButtonOrder();
                });

        sortDirectionToggle = new GUI_Toggle( new AdvancedHitbox(0, 0, Scale(48), Scale(48)).SetIsPopupCompatible(true))
                .SetTickImage(new GUI_Image(EUIRM.Images.Arrow.Texture()), new GUI_Image(EUIRM.Images.Arrow.Texture()).SetRotation(180f), 32)
                .SetText("")
                .SetOnToggle(val -> {
                    sortDesc = val;
                    RefreshButtonOrder();
                });
    }

    public GenericFilters<T> Initialize(ActionT1<CardKeywordButton> onClick, ArrayList<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        Clear(false, true);
        CurrentFilterCounts.clear();
        FilterButtons.clear();
        currentTotal = 0;

        EUI.ActingColor = color;
        EUITooltip.UpdateTooltipIcons();
        this.onClick = onClick;
        referenceItems = items;

        InitializeImpl(onClick, items, color, isAccessedFromCardPool);

        // InitializeImpl should set up the CurrentFilterCounts set
        for (Map.Entry<EUITooltip, Integer> filter : CurrentFilterCounts.entrySet())
        {
            int cardCount = filter.getValue();
            FilterButtons.add(new CardKeywordButton(hb, filter.getKey()).SetOnClick(onClick).SetCardCount(cardCount));
        }
        currentTotalLabel.SetText(currentTotal);

        return this;
    }

    public void Open()
    {
        CardCrawlGame.isPopupOpen = true;
        SetActive(true);
    }

    public void Close()
    {
        closeButton.hb.hovered = false;
        closeButton.hb.clicked = false;
        closeButton.hb.justHovered = false;
        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.isPopupOpen = false;
        SetActive(false);
    }

    public void Clear(boolean shouldInvoke, boolean shouldClearColors)
    {
        ClearImpl(shouldInvoke, shouldClearColors);
        if (shouldInvoke && onClick != null)
        {
            onClick.Invoke(null);
        }
    }

    public void Refresh(ArrayList<T> items)
    {
        referenceItems = items;
        invalidated = true;
    }

    public void RefreshButtons()
    {
        CurrentFilterCounts.clear();
        currentTotal = 0;

        if (referenceItems != null)
        {
            currentTotal = GetReferenceCount();
            for (T card : referenceItems)
            {
                for (EUITooltip tooltip : GetAllTooltips(card))
                {
                    CurrentFilterCounts.merge(tooltip, 1, Integer::sum);
                }
            }
        }
        for (CardKeywordButton c : FilterButtons)
        {
            c.SetCardCount(CurrentFilterCounts.getOrDefault(c.Tooltip, 0));
        }

        currentTotalLabel.SetText(currentTotal);

        RefreshButtonOrder();
    }

    public void RefreshButtonOrder()
    {
        sortTypeToggle.SetText(EUIRM.Strings.SortBy(shouldSortByCount ? EUIRM.Strings.UI_Amount : CardLibSortHeader.TEXT[2]));
        FilterButtons.sort((a, b) -> (shouldSortByCount ? a.CardCount - b.CardCount : StringUtils.compare(a.Tooltip.title, b.Tooltip.title)) * (sortDesc ? -1 : 1));

        int index = 0;
        for (CardKeywordButton c : FilterButtons)
        {
            if (c.isActive)
            {
                c.SetIndex(index);
                index += 1;
            }
        }
    }

    @Override
    public boolean TryUpdate() {
        super.TryUpdate();
        if (EUIHotkeys.toggleFilters.isJustPressed()) {
            CardKeywordFilters.ToggleFilters();
        }
        return isActive;
    }

    @Override
    public void Update()
    {
        super.Update();
        hb.y = DRAW_START_Y + scrollDelta - SPACING * 10;
        keywordsSectionLabel.SetPosition(hb.x - SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 7).Update();
        sortTypeToggle.SetPosition(keywordsSectionLabel.hb.x + SPACING * 10, DRAW_START_Y + scrollDelta - SPACING * 7).TryUpdate();
        sortDirectionToggle.SetPosition(sortTypeToggle.hb.x + SPACING * 7, DRAW_START_Y + scrollDelta - SPACING * 7).TryUpdate();
        currentTotalHeaderLabel.Update();
        currentTotalLabel.Update();
        hb.update();
        closeButton.TryUpdate();
        clearButton.TryUpdate();
        if (invalidated)
        {
            invalidated = false;
            RefreshButtons();
        }

        if (!EUI.DoesActiveElementExist())
        {
            for (CardKeywordButton c : FilterButtons)
            {
                c.TryUpdate();
            }

            UpdateInput();
        }

        UpdateImpl();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);
        sb.setColor(FADE_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        hb.render(sb);
        closeButton.TryRender(sb);
        clearButton.TryRender(sb);
        keywordsSectionLabel.Render(sb);
        currentTotalHeaderLabel.Render(sb);
        currentTotalLabel.Render(sb);
        sortTypeToggle.TryRender(sb);
        sortDirectionToggle.TryRender(sb);

        for (CardKeywordButton c : FilterButtons)
        {
            c.TryRender(sb);
        }

        RenderImpl(sb);
    }

    private void UpdateInput()
    {
        if (InputHelper.justClickedLeft)
        {
            if (closeButton.hb.hovered
                    || clearButton.hb.hovered
                    || sortTypeToggle.hb.hovered
                    || sortDirectionToggle.hb.hovered
                    || IsHoveredImpl())
            {
                return;
            }
            for (CardKeywordButton c : FilterButtons)
            {
                if (c.background_button.hb.hovered)
                {
                    //CardCrawlGame.sound.play("UI_CLICK_1");
                    //c.background_button.onLeftClick.Complete(c.background_button);
                    return;
                }
            }
            Close();
            InputHelper.justClickedLeft = false;
        }
        else if (InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed())
        {
            CInputActionSet.cancel.unpress();
            InputHelper.pressedEscape = false;
            Close();
        }
    }

    public void Invoke(CardKeywordButton button)
    {
        if (onClick != null)
        {
            onClick.Invoke(button);
        }
    }

    @Override
    public int CurrentSize()
    {
        return FilterButtons.size();
    }

    public int GetReferenceCount()
    {
        return referenceItems.size();
    }

    abstract public boolean AreFiltersEmpty();
    abstract public boolean IsHoveredImpl();
    abstract public void ClearImpl(boolean shouldInvoke, boolean shouldClearColors);
    abstract public void RenderImpl(SpriteBatch sb);
    abstract public void UpdateImpl();
    abstract public ArrayList<EUITooltip> GetAllTooltips(T c);
    abstract public ArrayList<AbstractCard> ApplyFilters(ArrayList<T> input);
    abstract protected void InitializeImpl(ActionT1<CardKeywordButton> onClick, ArrayList<T> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool);
}
