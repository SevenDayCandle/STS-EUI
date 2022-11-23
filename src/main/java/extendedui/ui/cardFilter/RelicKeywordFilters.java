package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class RelicKeywordFilters extends GenericFilters<AbstractRelic>
{
    public enum SeenValue
    {
        Seen(EUIRM.Strings.uiSeen, UnlockTracker::isRelicSeen),
        Unseen(EUIRM.Strings.uiUnseen, c -> !UnlockTracker.isRelicSeen(c) && !UnlockTracker.isRelicLocked(c)),
        Locked(SingleRelicViewPopup.TEXT[8], UnlockTracker::isRelicLocked);

        public final FuncT1<Boolean, String> evalFunc;
        public final String name;

        SeenValue(String name, FuncT1<Boolean, String> evalFunc)
        {
            this.evalFunc = evalFunc;
            this.name = name;
        }

        public boolean evaluate(String relicID)
        {
            return evalFunc.invoke(relicID);
        }
    }

    public static CustomRelicFilterModule CustomModule;
    public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<AbstractRelic.RelicTier> currentRarities = new HashSet<>();
    public final HashSet<SeenValue> currentSeen = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<AbstractRelic.RelicTier> raritiesDropdown;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUIDropdown<SeenValue> seenDropdown;
    public final EUITextBoxInput nameInput;
    public String currentName;

    public RelicKeywordFilters()
    {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new AdvancedHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.Strings.uiBasegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.uiOrigins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractRelic.RelicTier>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicTier)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.RelicTier.values());

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.uiColors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        seenDropdown = new EUIDropdown<SeenValue>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , item -> item.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentSeen, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.uiSeen)
                .setItems(SeenValue.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.Images.rectangularButton.texture(),
                new AdvancedHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    currentName = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.CardTitleFont_Small, 0.8f)
                .setBackgroundTexture(EUIRM.Images.rectangularButton.texture());
    }

    @Override
    public boolean areFiltersEmpty()
    {
        return (currentName == null || currentName.isEmpty())
                && currentColors.isEmpty() && currentOrigins.isEmpty() && currentRarities.isEmpty() && currentSeen.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty() && (CustomModule != null && CustomModule.isEmpty());
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractRelic> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        CustomModule = EUI.getCustomRelicFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractRelic.RelicTier> availableRarities = new HashSet<>();
        if (referenceItems != null)
        {
            currentTotal = getReferenceCount();
            for (AbstractRelic relic : referenceItems)
            {
                for (EUITooltip tooltip : getAllTooltips(relic))
                {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(relic));
                availableRarities.add(relic.tier);
                availableColors.add(EUIGameUtils.getRelicColor(relic.relicId));
            }
            if (CustomModule != null)
            {
                CustomModule.initializeSelection(referenceItems);
            }
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);

        ArrayList<AbstractRelic.RelicTier> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        raritiesDropdown.setItems(rarityItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
    }

    @Override
    public boolean isHoveredImpl()
    {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || seenDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || (CustomModule != null && CustomModule.isHovered());
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
        currentRarities.clear();
        currentName = null;
        originsDropdown.setSelectionIndices(null, false);
        raritiesDropdown.setSelectionIndices(null, false);
        colorsDropdown.setSelectionIndices(null, false);
        seenDropdown.setSelectionIndices(null, false);
        nameInput.setLabel("");
        if (CustomModule != null)
        {
            CustomModule.reset();
        }
    }

    @Override
    public void renderFilters(SpriteBatch sb)
    {
        originsDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        colorsDropdown.tryRender(sb);
        seenDropdown.tryRender(sb);
        nameInput.tryRender(sb);

        if (CustomModule != null)
        {
            CustomModule.tryRender(sb);
        }
    }

    @Override
    public void updateFilters()
    {
        originsDropdown.setPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        raritiesDropdown.setPosition(originsDropdown.hb.x + originsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        colorsDropdown.setPosition(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        seenDropdown.setPosition(colorsDropdown.hb.x + colorsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        nameInput.setPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();

        if (CustomModule != null)
        {
            CustomModule.tryUpdate();
        }
    }

    public ArrayList<EUITooltip> getAllTooltips(AbstractRelic c)
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
            for (PowerTip sk : c.tips)
            {
                EUITooltip tip = EUITooltip.findByName(sk.header);
                if (tip != null && !dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
        }
        return dynamicTooltips;
    }

    public ArrayList<AbstractRelic> applyFilters(ArrayList<AbstractRelic> input)
    {
        return EUIUtils.filter(input, this::evaluateRelic);
    }

    public ArrayList<EUIRelicGrid.RelicInfo> applyInfoFilters(ArrayList<EUIRelicGrid.RelicInfo> input)
    {
        return EUIUtils.filter(input, info -> evaluateRelic(info.relic));
    }

    protected boolean evaluateRelic(AbstractRelic c)
    {
        //Name check
        if (currentName != null && !currentName.isEmpty()) {
            if (c.name == null || !c.name.toLowerCase().contains(currentName.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(currentColors, (opt) -> opt == EUIGameUtils.getRelicColor(c.relicId)))
        {
            return false;
        }

        //Origin check
        if (!evaluateItem(currentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt)))
        {
            return false;
        }

        //Seen check
        if (!evaluateItem(currentSeen, (opt) -> opt.evaluate(c.relicId)))
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
        if (!currentRarities.isEmpty() && !currentRarities.contains(c.tier))
        {
            return false;
        }

        //Module check
        if (CustomModule != null && !CustomModule.isRelicValid(c))
        {
            return false;
        }

        return true;
    }

    public void toggleFilters()
    {
        if (EUI.RelicFilters.isActive)
        {
            EUI.RelicFilters.close();
        }
        else
        {
            EUI.RelicFilters.open();
        }
    }
}
