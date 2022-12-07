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
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.EUIHitbox;
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

    public static CustomCardFilterModule customModule;
    public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<CostFilter> currentCosts = new HashSet<>();
    public final HashSet<AbstractCard.CardRarity> currentRarities = new HashSet<>();
    public final HashSet<AbstractCard.CardType> currentTypes = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<CostFilter> costDropdown;
    public final EUIDropdown<AbstractCard.CardRarity> raritiesDropdown;
    public final EUIDropdown<AbstractCard.CardType> typesDropdown;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUITextBoxInput descriptionInput;
    public final EUITextBoxInput nameInput;
    public String currentDescription;
    public String currentName;

    public CardKeywordFilters()
    {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.uiBasegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiOrigins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        costDropdown = new EUIDropdown<CostFilter>(new EUIHitbox(0, 0, scale(160), scale(48)), c -> c.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentCosts, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .setIsMultiSelect(true)
                .setCanAutosize(false, false)
                .setItems(CostFilter.values());

        raritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardRarity.values());

        typesDropdown = new EUIDropdown<AbstractCard.CardType>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForType)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentTypes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardType.values());

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiColors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    currentName = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.8f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture());
        descriptionInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    currentDescription = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiDescriptionsearch)
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.8f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture());
    }

    @Override
    public int getReferenceCount()
    {
        return (referenceItems.size() == 1 && referenceItems.get(0) instanceof FakeLibraryCard) ? 0 : referenceItems.size();
    }

    @Override
    public boolean areFiltersEmpty()
    {
        return (currentName == null || currentName.isEmpty())
                && (currentDescription == null || currentDescription.isEmpty())
                && currentColors.isEmpty() && currentOrigins.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty()
                && currentCosts.isEmpty() && currentRarities.isEmpty()
                && currentTypes.isEmpty() && (customModule != null && customModule.isEmpty());
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractCard> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        customModule = EUI.getCustomCardFilter(color);

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
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(card));
                availableRarities.add(card.rarity);
                availableTypes.add(card.type);
                availableCosts.add(MathUtils.clamp(card.cost, CostFilter.Unplayable.upperBound, CostFilter.Cost3Plus.lowerBound));
                availableColors.add(card.color);
            }
            if (customModule != null)
            {
                customModule.initializeSelection(referenceItems);
            }
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);

        ArrayList<AbstractCard.CardRarity> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        raritiesDropdown.setItems(rarityItems);

        ArrayList<AbstractCard.CardType> typesItems = new ArrayList<>(availableTypes);
        typesItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        typesDropdown.setItems(typesItems);

        ArrayList<CostFilter> costItems = new ArrayList<>();
        for (CostFilter c : CostFilter.values())
        {
            if (availableCosts.contains(c.lowerBound) || availableCosts.contains(c.upperBound))
            {
                costItems.add(c);
            }
        }
        costDropdown.setItems(costItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
        if (isAccessedFromCardPool)
        {
            colorsDropdown.setSelection(EUIUtils.filter(colorsItems, c -> c != AbstractCard.CardColor.COLORLESS && c != AbstractCard.CardColor.CURSE), true);
        }
    }

    @Override
    public boolean isHoveredImpl()
    {
        return originsDropdown.areAnyItemsHovered()
                || costDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || typesDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void clearFilters(boolean shouldInvoke, boolean shouldClearColors)
    {
        if (shouldClearColors)
        {
            currentColors.clear();
        }
        currentOrigins.clear();
        currentFilters.clear();
        currentNegateFilters.clear();
        currentCosts.clear();
        currentRarities.clear();
        currentTypes.clear();
        currentName = null;
        currentDescription = null;
        costDropdown.setSelectionIndices(null, false);
        originsDropdown.setSelectionIndices(null, false);
        typesDropdown.setSelectionIndices(null, false);
        raritiesDropdown.setSelectionIndices(null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
        if (customModule != null)
        {
            customModule.reset();
        }
    }

    @Override
    public void renderFilters(SpriteBatch sb)
    {
        originsDropdown.tryRender(sb);
        costDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        typesDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);

        if (customModule != null)
        {
            customModule.tryRender(sb);
        }
    }

    @Override
    public void updateFilters()
    {
        originsDropdown.setPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        costDropdown.setPosition(originsDropdown.hb.x + originsDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        raritiesDropdown.setPosition(costDropdown.hb.x + costDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        typesDropdown.setPosition(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta).tryUpdate();
        nameInput.setPosition(hb.x + SPACING * 2.05f, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();

        if (customModule != null)
        {
            customModule.tryUpdate();
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
            if (currentName != null && !currentName.isEmpty()) {
                if (c.name == null || !c.name.toLowerCase().contains(currentName.toLowerCase())) {
                    return false;
                }
            }

            //Description check
            if (currentDescription != null && !currentDescription.isEmpty()) {
                if (c.rawDescription == null || !c.rawDescription.toLowerCase().contains(currentDescription.toLowerCase())) {
                    return false;
                }
            }

            //Colors check
            if (!currentColors.isEmpty() && !currentColors.contains(c.color))
            {
                return false;
            }

            //Origin check
            if (!evaluateItem(currentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt)))
            {
                return false;
            }

            //Tooltips check
            if (!currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(currentFilters)))
            {
                return false;
            }

            //Negate Tooltips check
            if (!currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), currentNegateFilters::contains)))
            {
                return false;
            }

            //Rarities check
            if (!currentRarities.isEmpty() && !currentRarities.contains(c.rarity))
            {
                return false;
            }

            //Types check
            if (!currentTypes.isEmpty() && !currentTypes.contains(c.type))
            {
                return false;
            }

            //Module check
            if (customModule != null && !customModule.isCardValid(c))
            {
                return false;
            }

            //Cost check
            if (!currentCosts.isEmpty())
            {
                boolean passes = false;
                for (CostFilter cf : currentCosts)
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
