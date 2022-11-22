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
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
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
        Seen(EUIRM.Strings.UI_Seen, UnlockTracker::isRelicSeen),
        Unseen(EUIRM.Strings.UI_Unseen, c -> !UnlockTracker.isRelicSeen(c) && !UnlockTracker.isRelicLocked(c)),
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
    public final HashSet<AbstractCard.CardColor> CurrentColors = new HashSet<>();
    public final HashSet<ModInfo> CurrentOrigins = new HashSet<>();
    public final HashSet<AbstractRelic.RelicTier> CurrentRarities = new HashSet<>();
    public final HashSet<SeenValue> CurrentSeen = new HashSet<>();
    public final EUIDropdown<ModInfo> OriginsDropdown;
    public final EUIDropdown<AbstractRelic.RelicTier> RaritiesDropdown;
    public final EUIDropdown<AbstractCard.CardColor> ColorsDropdown;
    public final EUIDropdown<SeenValue> SeenDropdown;
    public final EUITextBoxInput NameInput;
    public String CurrentName;

    public RelicKeywordFilters()
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

        RaritiesDropdown = new EUIDropdown<AbstractRelic.RelicTier>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicTier)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.RelicTier.values());

        ColorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Colors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        SeenDropdown = new EUIDropdown<SeenValue>(new AdvancedHitbox(0, 0, scale(240), scale(48))
                , item -> item.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(CurrentSeen, costs))
                .setLabelFunctionForButton(this::filterNameFunction, null, false)
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Seen)
                .setItems(SeenValue.values())
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
                .setFont(EUIFontHelper.CardTitleFont_Small, 0.8f)
                .setBackgroundTexture(EUIRM.Images.RectangularButton.texture());
    }

    @Override
    public boolean areFiltersEmpty()
    {
        return (CurrentName == null || CurrentName.isEmpty())
                && CurrentColors.isEmpty() && CurrentOrigins.isEmpty() && CurrentRarities.isEmpty() && CurrentSeen.isEmpty()
                && CurrentFilters.isEmpty() && CurrentNegateFilters.isEmpty() && (CustomModule != null && CustomModule.isEmpty());
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
                        CurrentFilterCounts.merge(tooltip, 1, Integer::sum);
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
        OriginsDropdown.setItems(modInfos);

        ArrayList<AbstractRelic.RelicTier> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        RaritiesDropdown.setItems(rarityItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        ColorsDropdown.setItems(colorsItems);
    }

    @Override
    public boolean isHoveredImpl()
    {
        return OriginsDropdown.areAnyItemsHovered()
                || RaritiesDropdown.areAnyItemsHovered()
                || ColorsDropdown.areAnyItemsHovered()
                || SeenDropdown.areAnyItemsHovered()
                || NameInput.hb.hovered
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
        CurrentRarities.clear();
        CurrentName = null;
        OriginsDropdown.setSelectionIndices(null, false);
        RaritiesDropdown.setSelectionIndices(null, false);
        ColorsDropdown.setSelectionIndices(null, false);
        SeenDropdown.setSelectionIndices(null, false);
        NameInput.setLabel("");
        if (CustomModule != null)
        {
            CustomModule.reset();
        }
    }

    @Override
    public void renderFilters(SpriteBatch sb)
    {
        OriginsDropdown.tryRender(sb);
        RaritiesDropdown.tryRender(sb);
        ColorsDropdown.tryRender(sb);
        SeenDropdown.tryRender(sb);
        NameInput.tryRender(sb);

        if (CustomModule != null)
        {
            CustomModule.tryRender(sb);
        }
    }

    @Override
    public void updateFilters()
    {
        OriginsDropdown.setPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        RaritiesDropdown.setPosition(OriginsDropdown.hb.x + OriginsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        ColorsDropdown.setPosition(RaritiesDropdown.hb.x + RaritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        SeenDropdown.setPosition(ColorsDropdown.hb.x + ColorsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        NameInput.setPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();

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
        if (CurrentName != null && !CurrentName.isEmpty()) {
            if (c.name == null || !c.name.toLowerCase().contains(CurrentName.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(CurrentColors, (opt) -> opt == EUIGameUtils.getRelicColor(c.relicId)))
        {
            return false;
        }

        //Origin check
        if (!evaluateItem(CurrentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt)))
        {
            return false;
        }

        //Seen check
        if (!evaluateItem(CurrentSeen, (opt) -> opt.evaluate(c.relicId)))
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
        if (!CurrentRarities.isEmpty() && !CurrentRarities.contains(c.tier))
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
