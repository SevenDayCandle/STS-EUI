package extendedui.ui.cardFilter;

import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.FakeLibraryCard;
import extendedui.utilities.Mathf;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CardKeywordFilters extends GUI_CanvasGrid
{
    public enum CostFilter
    {
        CostX("X", -1, -1),
        Cost0("0", 0, 0),
        Cost1("1", 1, 1),
        Cost2("2", 2, 2),
        Cost3Plus("3+", 3, 9999),
        Unplayable("Unplayable", -9999, -2);

        public final int lowerBound;
        public final int upperBound;
        public final String name;

        CostFilter(String name, int lowerBound, int upperBound)
        {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.name = name;
        }
    }

    protected static final Color FADE_COLOR = new Color(0f, 0f, 0f, 0.84f);
    public static final float SPACING = Settings.scale * 22.5f;
    public static final float DRAW_START_X = (float) Settings.WIDTH * 0.15f;
    public static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.87f;
    public static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    public static final float PAD_Y = Scale(45);
    public static final int ROW_SIZE = 8;
    public static final HashSet<AbstractCard.CardColor> CurrentColors = new HashSet<>();
    public static final HashSet<ModInfo> CurrentOrigins = new HashSet<>();
    public static final HashSet<EUITooltip> CurrentFilters = new HashSet<>();
    public static final HashSet<EUITooltip> CurrentNegateFilters = new HashSet<>();
    public static final HashSet<CostFilter> CurrentCosts = new HashSet<>();
    public static final HashSet<AbstractCard.CardRarity> CurrentRarities = new HashSet<>();
    public static final HashSet<AbstractCard.CardType> CurrentTypes = new HashSet<>();
    public static String CurrentName;
    public static CustomCardFilterModule CustomModule;
    protected final HashMap<EUITooltip, Integer> CurrentFilterCounts = new HashMap<>();
    protected final ArrayList<CardKeywordButton> FilterButtons = new ArrayList<>();
    protected int currentTotal;
    protected ArrayList<AbstractCard> referenceCards;
    protected ActionT1<CardKeywordButton> onClick;

    protected boolean invalidated;
    protected float draw_x;
    public final GUI_Dropdown<ModInfo> OriginsDropdown;
    public final GUI_Dropdown<CostFilter> CostDropdown;
    public final GUI_Dropdown<AbstractCard.CardRarity> RaritiesDropdown;
    public final GUI_Dropdown<AbstractCard.CardType> TypesDropdown;
    public final GUI_Dropdown<AbstractCard.CardColor> ColorsDropdown;
    public final GUI_TextBoxInput NameInput;
    public final GUI_Button closeButton;
    public final GUI_Button clearButton;
    public final GUI_Label currentTotalHeaderLabel;
    public final GUI_Label currentTotalLabel;
    public final GUI_Label keywordsSectionLabel;
    public final GUI_Toggle sortTypeToggle;
    public final GUI_Toggle sortDirectionToggle;
    public final AdvancedHitbox hb;

    protected boolean isAccessedFromCardPool;
    private boolean shouldSortByCount;
    private boolean sortDesc;

    public static void ToggleFilters()
    {
        if (EUI.CardFilters.isActive)
        {
            EUI.CardFilters.Close();
        }
        else
        {
            EUI.CardFilters.Open();
        }
    }

    public ArrayList<EUITooltip> GetAllTooltips(AbstractCard c)
    {
        ArrayList<EUITooltip> dynamicTooltips = new ArrayList<>();
        TooltipProvider eC = JavaUtils.SafeCast(c, TooltipProvider.class);
        if (eC != null)
        {
            eC.GenerateDynamicTooltips(dynamicTooltips);
            for (EUITooltip tip : eC.GetTips())
            {
                if (!dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
        }
        else
        {
            if (c.isInnate) {
                EUITooltip tip = EUITooltip.FindByID(GameDictionary.EXHAUST.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.isEthereal) {
                EUITooltip tip = EUITooltip.FindByID(GameDictionary.ETHEREAL.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.selfRetain) {
                EUITooltip tip = EUITooltip.FindByID(GameDictionary.RETAIN.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.exhaust) {
                EUITooltip tip = EUITooltip.FindByID(GameDictionary.EXHAUST.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            for (String sk : c.keywords)
            {
                EUITooltip tip = EUITooltip.FindByName(sk);
                if (tip != null && !dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c instanceof CustomCard) {
                List<TooltipInfo> infos = ((CustomCard) c).getCustomTooltips();
                ModInfo mi = EUIGameUtils.GetModInfo(c);
                if (infos != null && mi != null) {
                    for (TooltipInfo info : infos) {
                        EUITooltip tip = EUITooltip.FindByName(mi.ID.toLowerCase() + ":" + info.title);
                        if (tip != null && !dynamicTooltips.contains(tip))
                        {
                            dynamicTooltips.add(tip);
                        }
                    }
                }
            }
        }
        return dynamicTooltips;
    }

    public ArrayList<AbstractCard> ApplyFilters(ArrayList<AbstractCard> input)
    {
        return JavaUtils.Filter(input, c -> {
            //Name check
            if (CurrentName != null && !CurrentName.isEmpty()) {
                if (c.name == null || !c.name.toLowerCase().contains(CurrentName.toLowerCase())) {
                    return false;
                }
            }

            //Colors check
            if (!CurrentColors.isEmpty())
            {
                boolean passes = false;
                for (AbstractCard.CardColor co : CurrentColors)
                {
                    if (co == c.color)
                    {
                        passes = true;
                        break;
                    }
                }
                if (!passes)
                {
                    return false;
                }
            }

            //Origin check
            if (!CurrentOrigins.isEmpty())
            {
                boolean passes = false;
                for (ModInfo of : CurrentOrigins)
                {
                    if (EUIGameUtils.IsObjectFromMod(c, of))
                    {
                        passes = true;
                        break;
                    }
                }
                if (!passes)
                {
                    return false;
                }
            }

            //Tooltips check
            if (!CurrentFilters.isEmpty() && (!GetAllTooltips(c).containsAll(CurrentFilters)))
            {
                return false;
            }

            //Negate Tooltips check
            if (!CurrentNegateFilters.isEmpty() && (JavaUtils.Any(GetAllTooltips(c), CurrentNegateFilters::contains)))
            {
                return false;
            }

            //Rarities check
            if (!CurrentRarities.isEmpty() && !CurrentRarities.contains(c.rarity))
            {
                return false;
            }

            //Types check
            if (!CurrentTypes.isEmpty() && !CurrentTypes.contains(c.type))
            {
                return false;
            }

            //Module check
            if (CustomModule != null && !CustomModule.IsCardValid(c))
            {
                return false;
            }

            //Cost check
            if (!CurrentCosts.isEmpty())
            {
                boolean passes = false;
                for (CostFilter cf : CurrentCosts)
                {
                    if (c.cost >= cf.lowerBound && c.cost <= cf.upperBound)
                    {
                        passes = true;
                        break;
                    }
                }
                return passes;
            }

            return true;
        });
    }

    public boolean AreFiltersEmpty()
    {
        return (CurrentName == null || CurrentName.isEmpty()) && CurrentColors.isEmpty() && CurrentOrigins.isEmpty() && CurrentFilters.isEmpty() && CurrentNegateFilters.isEmpty() && CurrentCosts.isEmpty() && CurrentRarities.isEmpty() && CurrentTypes.isEmpty() && (CustomModule != null && CustomModule.IsEmpty());
    }

    public CardKeywordFilters()
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

        OriginsDropdown = new GUI_Dropdown<ModInfo>(new AdvancedHitbox(0, 0, Scale(240), Scale(48)), c -> c == null ? EUIRM.Strings.UI_BaseGame : c.Name)
                .SetOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .SetOnChange(costs -> {
                    CurrentOrigins.clear();
                    CurrentOrigins.addAll(costs);
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetLabelFunctionForButton(items -> {
                    if (items.size() == 0)
                    {
                        return EUIRM.Strings.UI_Any;
                    }
                    if (items.size() > 1)
                    {
                        return items.size() + " " + EUIRM.Strings.UI_ItemsSelected;
                    }
                    return StringUtils.join(JavaUtils.Map(items, item -> item == null ? EUIRM.Strings.UI_BaseGame : item.Name), ", ");
                }, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Origins)
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(Loader.MODINFOS);

        CostDropdown = new GUI_Dropdown<CostFilter>(new AdvancedHitbox(0, 0, Scale(160), Scale(48)), c -> c.name)
                .SetOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .SetOnChange(costs -> {
                    CurrentCosts.clear();
                    CurrentCosts.addAll(costs);
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetLabelFunctionForButton(items -> {
                    if (items.size() == 0)
                    {
                        return EUIRM.Strings.UI_Any;
                    }
                    if (items.size() > 1)
                    {
                        return items.size() + " " + EUIRM.Strings.UI_ItemsSelected;
                    }
                    return StringUtils.join(JavaUtils.Map(items, item -> item.name), ", ");
                }, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .SetIsMultiSelect(true)
                .SetCanAutosize(false, false)
                .SetItems(CostFilter.values());

        RaritiesDropdown = new GUI_Dropdown<AbstractCard.CardRarity>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::TextForRarity)
                .SetOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .SetOnChange(costs -> {
                    CurrentRarities.clear();
                    CurrentRarities.addAll(costs);
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetLabelFunctionForButton(items -> {
                    if (items.size() == 0)
                    {
                        return EUIRM.Strings.UI_Any;
                    }
                    if (items.size() > 1)
                    {
                        return items.size() + " " + EUIRM.Strings.UI_ItemsSelected;
                    }
                    return JavaUtils.JoinStrings(", ", JavaUtils.Map(items, EUIGameUtils::TextForRarity));
                }, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(AbstractCard.CardRarity.values());

        TypesDropdown = new GUI_Dropdown<AbstractCard.CardType>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::TextForType)
                .SetOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .SetOnChange(costs -> {
                    CurrentTypes.clear();
                    CurrentTypes.addAll(costs);
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetLabelFunctionForButton(items -> {
                    if (items.size() == 0)
                    {
                        return EUIRM.Strings.UI_Any;
                    }
                    if (items.size() > 1)
                    {
                        return items.size() + " " + EUIRM.Strings.UI_ItemsSelected;
                    }
                    return JavaUtils.JoinStrings(", ", JavaUtils.Map(items, EUIGameUtils::TextForType));
                }, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(AbstractCard.CardType.values());

        ColorsDropdown = new GUI_Dropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .SetOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .SetOnChange(colors -> {
                    CurrentColors.clear();
                    CurrentColors.addAll(colors);
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetLabelFunctionForButton(items -> {
                    if (items.size() == 0)
                    {
                        return EUIRM.Strings.UI_Any;
                    }
                    if (items.size() > 1)
                    {
                        return items.size() + " " + EUIRM.Strings.UI_ItemsSelected;
                    }
                    return StringUtils.join(JavaUtils.Map(items, item -> StringUtils.capitalize(item.toString().toLowerCase())), ", ");
                }, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Colors)
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true);
        NameInput = (GUI_TextBoxInput) new GUI_TextBoxInput(EUIRM.Images.RectangularButton.Texture(),
                new AdvancedHitbox(0, 0, Scale(240), Scale(40)).SetIsPopupCompatible(true))
                .SetOnComplete(s -> {
                    CurrentName = s;
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .SetHeaderSpacing(1f)
                .SetColors(Color.GRAY, Settings.CREAM_COLOR)
                .SetAlignment(0.5f, 0.1f)
                .SetFont(EUIFontHelper.CardTitleFont_Small, 0.8f)
                .SetBackgroundTexture(EUIRM.Images.RectangularButton.Texture());

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

    public CardKeywordFilters Initialize(ActionT1<CardKeywordButton> onClick, ArrayList<AbstractCard> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        Clear(false, true);
        CurrentFilterCounts.clear();
        FilterButtons.clear();
        currentTotal = 0;

        EUI.ActingColor = color;
        EUITooltip.UpdateTooltipIcons();
        CustomModule = EUI.GetCustomCardFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availableCosts = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractCard.CardRarity> availableRarities = new HashSet<>();
        HashSet<AbstractCard.CardType> availableTypes = new HashSet<>();

        this.onClick = onClick;
        referenceCards = cards;
        if (referenceCards != null)
        {
            currentTotal = (referenceCards.size() == 1 && referenceCards.get(0) instanceof FakeLibraryCard) ? 0 : referenceCards.size();
            for (AbstractCard card : referenceCards)
            {
                for (EUITooltip tooltip : GetAllTooltips(card))
                {
                    if (tooltip.canFilter) {
                        CurrentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.GetModInfo(card));
                availableRarities.add(card.rarity);
                availableTypes.add(card.type);
                availableCosts.add(Mathf.Clamp(card.cost, CostFilter.Unplayable.upperBound, CostFilter.Cost3Plus.lowerBound));
                availableColors.add(card.color);
            }
            if (CustomModule != null)
            {
                CustomModule.InitializeSelection(referenceCards);
            }
        }

        for (Map.Entry<EUITooltip, Integer> filter : CurrentFilterCounts.entrySet())
        {
            int cardCount = filter.getValue();
            FilterButtons.add(new CardKeywordButton(hb, filter.getKey()).SetOnClick(onClick).SetCardCount(cardCount));
        }
        currentTotalLabel.SetText(currentTotal);

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        OriginsDropdown.SetItems(modInfos);

        ArrayList<AbstractCard.CardRarity> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        RaritiesDropdown.SetItems(rarityItems);

        ArrayList<AbstractCard.CardType> typesItems = new ArrayList<>(availableTypes);
        typesItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        TypesDropdown.SetItems(typesItems);

        ArrayList<CostFilter> costItems = new ArrayList<>();
        for (CostFilter c : CostFilter.values())
        {
            if (availableCosts.contains(c.lowerBound) || availableCosts.contains(c.upperBound))
            {
                costItems.add(c);
            }
        }
        CostDropdown.SetItems(costItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        ColorsDropdown.SetItems(colorsItems);
        if (isAccessedFromCardPool)
        {
            ColorsDropdown.SetSelection(JavaUtils.Filter(colorsItems, c -> c != AbstractCard.CardColor.COLORLESS && c != AbstractCard.CardColor.CURSE), true);
        }

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
        if (shouldClearColors)
        {
            CurrentColors.clear();
        }
        CurrentOrigins.clear();
        CurrentFilters.clear();
        CurrentNegateFilters.clear();
        CurrentCosts.clear();
        CurrentRarities.clear();
        CurrentTypes.clear();
        CurrentName = null;
        CostDropdown.SetSelectionIndices(null, false);
        OriginsDropdown.SetSelectionIndices(null, false);
        TypesDropdown.SetSelectionIndices(null, false);
        RaritiesDropdown.SetSelectionIndices(null, false);
        NameInput.SetText("");
        if (CustomModule != null)
        {
            CustomModule.Reset();
        }
        if (shouldInvoke && onClick != null)
        {
            onClick.Invoke(null);
        }
    }

    public void Refresh(ArrayList<AbstractCard> cards)
    {
        referenceCards = cards;
        invalidated = true;
    }

    public void RefreshButtons()
    {
        CurrentFilterCounts.clear();
        currentTotal = 0;

        if (referenceCards != null)
        {
            currentTotal = (referenceCards.size() == 1 && referenceCards.get(0) instanceof FakeLibraryCard) ? 0 : referenceCards.size();
            for (AbstractCard card : referenceCards)
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
        OriginsDropdown.SetPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta);
        CostDropdown.SetPosition(OriginsDropdown.hb.x + OriginsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta);
        RaritiesDropdown.SetPosition(CostDropdown.hb.x + CostDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta);
        TypesDropdown.SetPosition(RaritiesDropdown.hb.x + RaritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta);
        NameInput.SetPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3);
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

        OriginsDropdown.TryUpdate();
        CostDropdown.TryUpdate();
        RaritiesDropdown.TryUpdate();
        TypesDropdown.TryUpdate();
        NameInput.TryUpdate();

        if (CustomModule != null)
        {
            CustomModule.TryUpdate();
        }
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
        OriginsDropdown.TryRender(sb);
        CostDropdown.TryRender(sb);
        RaritiesDropdown.TryRender(sb);
        TypesDropdown.TryRender(sb);
        NameInput.TryRender(sb);

        if (CustomModule != null)
        {
            CustomModule.TryRender(sb);
        }
    }

    private void UpdateInput()
    {
        if (InputHelper.justClickedLeft)
        {
            if (closeButton.hb.hovered
                    || clearButton.hb.hovered
                    || sortTypeToggle.hb.hovered
                    || sortDirectionToggle.hb.hovered
                    || OriginsDropdown.AreAnyItemsHovered()
                    || CostDropdown.AreAnyItemsHovered()
                    || RaritiesDropdown.AreAnyItemsHovered()
                    || TypesDropdown.AreAnyItemsHovered()
                    || NameInput.hb.hovered
                    || (CustomModule != null && CustomModule.IsHovered()))
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
        return (referenceCards.size() == 1 && referenceCards.get(0) instanceof FakeLibraryCard) ? 0 : referenceCards.size();
    }
}
