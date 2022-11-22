package extendedui.ui.cardFilter;

import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.FakeLibraryCard;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CardKeywordFilters extends GenericFilters<AbstractCard>
{
    public enum CostFilter
    {
        CostX("X", -1, -1),
        Cost0("0", 0, 0),
        Cost1("1", 1, 1),
        Cost2("2", 2, 2),
        Cost3Plus("3+", 3, 9999),
        Unplayable(GameDictionary.UNPLAYABLE.NAMES[0], -9999, -2);

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
    public final EUIDropdown<ModInfo> OriginsDropdown;
    public final EUIDropdown<CostFilter> CostDropdown;
    public final EUIDropdown<AbstractCard.CardRarity> RaritiesDropdown;
    public final EUIDropdown<AbstractCard.CardType> TypesDropdown;
    public final EUIDropdown<AbstractCard.CardColor> ColorsDropdown;
    public final EUITextBoxInput DescriptionInput;
    public final EUITextBoxInput NameInput;
    public String CurrentDescription;
    public String CurrentName;

    public CardKeywordFilters()
    {
        super();

        OriginsDropdown = new EUIDropdown<ModInfo>(new AdvancedHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.Strings.UI_BaseGame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        CostDropdown = new EUIDropdown<CostFilter>(new AdvancedHitbox(0, 0, scale(160), scale(48)), c -> c.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentCosts, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .setIsMultiSelect(true)
                .setCanAutosize(false, false)
                .setItems(CostFilter.values());

        RaritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardRarity.values());

        TypesDropdown = new EUIDropdown<AbstractCard.CardType>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForType)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentTypes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardType.values());

        ColorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Colors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        NameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.Images.RectangularButton.texture(),
                new AdvancedHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    CurrentName = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .setBackgroundTexture(EUIRM.Images.RectangularButton.texture());
        DescriptionInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.Images.RectangularButton.texture(),
                new AdvancedHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    CurrentDescription = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_DescriptionSearch)
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .setBackgroundTexture(EUIRM.Images.RectangularButton.texture());
    }

    @Override
    public int getReferenceCount()
    {
        return (referenceItems.size() == 1 && referenceItems.get(0) instanceof FakeLibraryCard) ? 0 : referenceItems.size();
    }

    @Override
    public boolean areFiltersEmpty()
    {
        return (CurrentName == null || CurrentName.isEmpty())
                && (CurrentDescription == null || CurrentDescription.isEmpty())
                && CurrentColors.isEmpty() && CurrentOrigins.isEmpty()
                && CurrentFilters.isEmpty() && CurrentNegateFilters.isEmpty()
                && CurrentCosts.isEmpty() && CurrentRarities.isEmpty()
                && CurrentTypes.isEmpty() && (CustomModule != null && CustomModule.isEmpty());
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractCard> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        CustomModule = EUI.getCustomCardFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availableCosts = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractCard.CardRarity> availableRarities = new HashSet<>();
        HashSet<AbstractCard.CardType> availableTypes = new HashSet<>();
        if (referenceItems != null)
        {
            currentTotal = getReferenceCount();
            for (AbstractCard card : referenceItems)
            {
                for (EUITooltip tooltip : getAllTooltips(card))
                {
                    if (tooltip.canFilter) {
                        CurrentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(card));
                availableRarities.add(card.rarity);
                availableTypes.add(card.type);
                availableCosts.add(MathUtils.clamp(card.cost, CostFilter.Unplayable.upperBound, CostFilter.Cost3Plus.lowerBound));
                availableColors.add(card.color);
            }
            if (CustomModule != null)
            {
                CustomModule.initializeSelection(referenceItems);
            }
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        OriginsDropdown.setItems(modInfos);

        ArrayList<AbstractCard.CardRarity> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        RaritiesDropdown.setItems(rarityItems);

        ArrayList<AbstractCard.CardType> typesItems = new ArrayList<>(availableTypes);
        typesItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        TypesDropdown.setItems(typesItems);

        ArrayList<CostFilter> costItems = new ArrayList<>();
        for (CostFilter c : CostFilter.values())
        {
            if (availableCosts.contains(c.lowerBound) || availableCosts.contains(c.upperBound))
            {
                costItems.add(c);
            }
        }
        CostDropdown.setItems(costItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        ColorsDropdown.setItems(colorsItems);
        if (isAccessedFromCardPool)
        {
            ColorsDropdown.setSelection(EUIUtils.filter(colorsItems, c -> c != AbstractCard.CardColor.COLORLESS && c != AbstractCard.CardColor.CURSE), true);
        }
    }

    @Override
    public boolean isHoveredImpl()
    {
        return OriginsDropdown.areAnyItemsHovered()
                || CostDropdown.areAnyItemsHovered()
                || RaritiesDropdown.areAnyItemsHovered()
                || TypesDropdown.areAnyItemsHovered()
                || NameInput.hb.hovered
                || DescriptionInput.hb.hovered
                || (CustomModule != null && CustomModule.isHovered());
    }

    @Override
    public void clearFilters(boolean shouldInvoke, boolean shouldClearColors)
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
        CurrentDescription = null;
        CostDropdown.setSelectionIndices(null, false);
        OriginsDropdown.setSelectionIndices(null, false);
        TypesDropdown.setSelectionIndices(null, false);
        RaritiesDropdown.setSelectionIndices(null, false);
        NameInput.setLabel("");
        DescriptionInput.setLabel("");
        if (CustomModule != null)
        {
            CustomModule.reset();
        }
    }

    @Override
    public void renderFilters(SpriteBatch sb)
    {
        OriginsDropdown.tryRender(sb);
        CostDropdown.tryRender(sb);
        RaritiesDropdown.tryRender(sb);
        TypesDropdown.tryRender(sb);
        NameInput.tryRender(sb);
        DescriptionInput.tryRender(sb);

        if (CustomModule != null)
        {
            CustomModule.tryRender(sb);
        }
    }

    @Override
    public void updateFilters()
    {
        OriginsDropdown.setPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        CostDropdown.setPosition(OriginsDropdown.hb.x + OriginsDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        RaritiesDropdown.setPosition(CostDropdown.hb.x + CostDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        TypesDropdown.setPosition(RaritiesDropdown.hb.x + RaritiesDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        NameInput.setPosition(hb.x + SPACING * 2.05f, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();
        DescriptionInput.setPosition(NameInput.hb.cX + NameInput.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();

        if (CustomModule != null)
        {
            CustomModule.tryUpdate();
        }
    }

    public ArrayList<EUITooltip> getAllTooltips(AbstractCard c)
    {
        ArrayList<EUITooltip> dynamicTooltips = new ArrayList<>();
        TooltipProvider eC = EUIUtils.safeCast(c, TooltipProvider.class);
        if (eC != null)
        {
            eC.generateDynamicTooltips(dynamicTooltips);
            for (EUITooltip tip : eC.getTipsForFilters())
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
                EUITooltip tip = EUITooltip.findByID(GameDictionary.EXHAUST.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.isEthereal) {
                EUITooltip tip = EUITooltip.findByID(GameDictionary.ETHEREAL.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.selfRetain) {
                EUITooltip tip = EUITooltip.findByID(GameDictionary.RETAIN.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c.exhaust) {
                EUITooltip tip = EUITooltip.findByID(GameDictionary.EXHAUST.NAMES[0]);
                if (tip != null)
                {
                    dynamicTooltips.add(tip);
                }
            }
            for (String sk : c.keywords)
            {
                EUITooltip tip = EUITooltip.findByName(sk);
                if (tip != null && !dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
            if (c instanceof CustomCard) {
                List<TooltipInfo> infos = ((CustomCard) c).getCustomTooltips();
                ModInfo mi = EUIGameUtils.getModInfo(c);
                if (infos != null && mi != null) {
                    for (TooltipInfo info : infos) {
                        EUITooltip tip = EUITooltip.findByName(mi.ID.toLowerCase() + ":" + info.title);
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

    public ArrayList<AbstractCard> applyFilters(ArrayList<AbstractCard> input)
    {
        return EUIUtils.filter(input, c -> {
            //Name check
            if (CurrentName != null && !CurrentName.isEmpty()) {
                if (c.name == null || !c.name.toLowerCase().contains(CurrentName.toLowerCase())) {
                    return false;
                }
            }

            //Description check
            if (CurrentDescription != null && !CurrentDescription.isEmpty()) {
                if (c.rawDescription == null || !c.rawDescription.toLowerCase().contains(CurrentDescription.toLowerCase())) {
                    return false;
                }
            }

            //Colors check
            if (!CurrentColors.isEmpty() && !CurrentColors.contains(c.color))
            {
                return false;
            }

            //Origin check
            if (!evaluateItem(CurrentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt)))
            {
                return false;
            }

            //Tooltips check
            if (!CurrentFilters.isEmpty() && (!getAllTooltips(c).containsAll(CurrentFilters)))
            {
                return false;
            }

            //Negate Tooltips check
            if (!CurrentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), CurrentNegateFilters::contains)))
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
            if (CustomModule != null && !CustomModule.isCardValid(c))
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

    public void toggleFilters()
    {
        if (isActive)
        {
            close();
        }
        else
        {
            open();
        }
    }
}
