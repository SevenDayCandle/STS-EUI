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
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.FakeLibraryCard;
import extendedui.utilities.Mathf;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CardKeywordFilters extends GenericFilters<AbstractCard>
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

    public static CustomCardFilterModule CustomModule;
    public final HashSet<AbstractCard.CardColor> CurrentColors = new HashSet<>();
    public final HashSet<ModInfo> CurrentOrigins = new HashSet<>();
    public final HashSet<CostFilter> CurrentCosts = new HashSet<>();
    public final HashSet<AbstractCard.CardRarity> CurrentRarities = new HashSet<>();
    public final HashSet<AbstractCard.CardType> CurrentTypes = new HashSet<>();
    public final GUI_Dropdown<ModInfo> OriginsDropdown;
    public final GUI_Dropdown<CostFilter> CostDropdown;
    public final GUI_Dropdown<AbstractCard.CardRarity> RaritiesDropdown;
    public final GUI_Dropdown<AbstractCard.CardType> TypesDropdown;
    public final GUI_Dropdown<AbstractCard.CardColor> ColorsDropdown;
    public final GUI_TextBoxInput NameInput;
    public String CurrentName;

    public CardKeywordFilters()
    {
        super();

        OriginsDropdown = new GUI_Dropdown<ModInfo>(new AdvancedHitbox(0, 0, Scale(240), Scale(48)), c -> c == null ? EUIRM.Strings.UI_BaseGame : c.Name)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentOrigins, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Origins)
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(Loader.MODINFOS);

        CostDropdown = new GUI_Dropdown<CostFilter>(new AdvancedHitbox(0, 0, Scale(160), Scale(48)), c -> c.name)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentCosts, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .SetIsMultiSelect(true)
                .SetCanAutosize(false, false)
                .SetItems(CostFilter.values());

        RaritiesDropdown = new GUI_Dropdown<AbstractCard.CardRarity>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::TextForRarity)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentRarities, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(AbstractCard.CardRarity.values());

        TypesDropdown = new GUI_Dropdown<AbstractCard.CardType>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::TextForType)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentTypes, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(AbstractCard.CardType.values());

        ColorsDropdown = new GUI_Dropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentColors, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
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
    }

    @Override
    public int GetReferenceCount()
    {
        return (referenceItems.size() == 1 && referenceItems.get(0) instanceof FakeLibraryCard) ? 0 : referenceItems.size();
    }

    @Override
    public boolean AreFiltersEmpty()
    {
        return (CurrentName == null || CurrentName.isEmpty()) && CurrentColors.isEmpty() && CurrentOrigins.isEmpty() && CurrentFilters.isEmpty() && CurrentNegateFilters.isEmpty() && CurrentCosts.isEmpty() && CurrentRarities.isEmpty() && CurrentTypes.isEmpty() && (CustomModule != null && CustomModule.IsEmpty());
    }

    @Override
    protected void InitializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractCard> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        CustomModule = EUI.GetCustomCardFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availableCosts = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractCard.CardRarity> availableRarities = new HashSet<>();
        HashSet<AbstractCard.CardType> availableTypes = new HashSet<>();
        if (referenceItems != null)
        {
            currentTotal = GetReferenceCount();
            for (AbstractCard card : referenceItems)
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
                CustomModule.InitializeSelection(referenceItems);
            }
        }

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
    }

    @Override
    public boolean IsHoveredImpl()
    {
        return OriginsDropdown.AreAnyItemsHovered()
                || CostDropdown.AreAnyItemsHovered()
                || RaritiesDropdown.AreAnyItemsHovered()
                || TypesDropdown.AreAnyItemsHovered()
                || NameInput.hb.hovered
                || (CustomModule != null && CustomModule.IsHovered());
    }

    @Override
    public void ClearImpl(boolean shouldInvoke, boolean shouldClearColors)
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
    }

    @Override
    public void RenderImpl(SpriteBatch sb)
    {
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

    @Override
    public void UpdateImpl()
    {
        OriginsDropdown.SetPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        CostDropdown.SetPosition(OriginsDropdown.hb.x + OriginsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        RaritiesDropdown.SetPosition(CostDropdown.hb.x + CostDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        TypesDropdown.SetPosition(RaritiesDropdown.hb.x + RaritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        NameInput.SetPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).TryUpdate();

        if (CustomModule != null)
        {
            CustomModule.TryUpdate();
        }
    }

    public ArrayList<EUITooltip> GetAllTooltips(AbstractCard c)
    {
        ArrayList<EUITooltip> dynamicTooltips = new ArrayList<>();
        TooltipProvider eC = JavaUtils.SafeCast(c, TooltipProvider.class);
        if (eC != null)
        {
            eC.GenerateDynamicTooltips(dynamicTooltips);
            for (EUITooltip tip : eC.GetTipsForFilters())
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
            if (!CurrentColors.isEmpty() && !CurrentColors.contains(c.color))
            {
                return false;
            }

            //Origin check
            if (!EvaluateItem(CurrentOrigins, (opt) -> EUIGameUtils.IsObjectFromMod(c, opt)))
            {
                return false;
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

    public void ToggleFilters()
    {
        if (isActive)
        {
            Close();
        }
        else
        {
            Open();
        }
    }
}
