package extendedui.ui.cardFilter.filters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.*;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomFilterable;
import extendedui.interfaces.markers.CustomRelicFilterModule;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.cardFilter.FilterKeywordButton;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RelicKeywordFilters extends GenericFilters<AbstractRelic, CustomRelicFilterModule> {
    public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<AbstractRelic.RelicTier> currentRarities = new HashSet<>();
    public final HashSet<AbstractRelic.LandingSound> currentSfx = new HashSet<>();
    public final HashSet<SeenValue> currentSeen = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<AbstractRelic.RelicTier> raritiesDropdown;
    public final EUIDropdown<AbstractRelic.LandingSound> sfxDropdown;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUIDropdown<SeenValue> seenDropdown;

    public RelicKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractRelic.RelicTier>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicTier)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.RelicTier.values());

        sfxDropdown = new EUIDropdown<AbstractRelic.LandingSound>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicLandingSound)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentSfx, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.LandingSound.values());

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_colors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        seenDropdown = new EUIDropdown<SeenValue>(new EUIHitbox(0, 0, scale(240), scale(48))
                , item -> item.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentSeen, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_seen)
                .setItems(SeenValue.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(AbstractRelic c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getDescriptionForSort();
        }
        return c.description;
    }

    public static String getNameForSort(AbstractRelic c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getNameForSort();
        }
        return c.name;
    }

    public ArrayList<RelicGroup.RelicInfo> applyInfoFilters(ArrayList<RelicGroup.RelicInfo> input) {
        return EUIUtils.filter(input, info -> evaluateRelic(info.relic));
    }

    @Override
    public void clearFilters(boolean shouldInvoke, boolean shouldClearColors) {
        if (shouldClearColors) {
            currentColors.clear();
        }
        currentOrigins.clear();
        currentFilters.clear();
        currentNegateFilters.clear();
        currentRarities.clear();
        currentName = null;
        currentDescription = null;
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        sfxDropdown.setSelectionIndices((int[]) null, false);
        colorsDropdown.setSelectionIndices((int[]) null, false);
        seenDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
        doForFilters(CustomRelicFilterModule::reset);
    }

    public ArrayList<AbstractRelic> applyFilters(ArrayList<AbstractRelic> input) {
        return EUIUtils.filter(input, this::evaluateRelic);
    }

    @Override
    public boolean areFiltersEmpty() {
        return (currentName == null || currentName.isEmpty())
                && (currentDescription == null || currentDescription.isEmpty())
                && currentColors.isEmpty() && currentOrigins.isEmpty() && currentRarities.isEmpty()
                && currentSfx.isEmpty() && currentSeen.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty()
                && EUIUtils.all(getGlobalFilters(), CustomRelicFilterModule::isEmpty)
                && (customModule != null && customModule.isEmpty());
    }

    @Override
    public ArrayList<CustomRelicFilterModule> getGlobalFilters() {
        return EUI.globalCustomRelicFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractRelic> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        customModule = EUI.getCustomRelicFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractRelic.RelicTier> availableRarities = new HashSet<>();
        HashSet<AbstractRelic.LandingSound> availableSfx = new HashSet<>();
        if (referenceItems != null) {
            currentTotal = getReferenceCount();
            for (AbstractRelic relic : referenceItems) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(relic)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(relic));
                availableRarities.add(relic.tier);
                availableSfx.add(EUIGameUtils.getLandingSound(relic));
                availableColors.add(EUIGameUtils.getRelicColor(relic.relicId));
            }
            doForFilters(m -> m.initializeSelection(referenceItems));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);

        ArrayList<AbstractRelic.RelicTier> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        raritiesDropdown.setItems(rarityItems);

        sfxDropdown.setItems(availableSfx).sortByLabel();

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
    }

    public void toggleFilters() {
        if (EUI.relicFilters.isActive) {
            EUI.relicFilters.close();
        }
        else {
            EUI.relicFilters.open();
        }
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(colorsDropdown, xPos);
        xPos = updateDropdown(raritiesDropdown, xPos);
        xPos = updateDropdown(seenDropdown, xPos);
        xPos = updateDropdown(sfxDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomRelicFilterModule::update);
    }

    public List<EUIKeywordTooltip> getAllTooltips(AbstractRelic c) {
        KeywordProvider eC = EUIUtils.safeCast(c, KeywordProvider.class);
        if (eC != null) {
            return eC.getTipsForFilters();
        }

        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();
        for (PowerTip sk : c.tips) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(StringUtils.lowerCase(sk.header));
            if (tip != null && !dynamicTooltips.contains(tip)) {
                dynamicTooltips.add(tip);
            }
        }
        return dynamicTooltips;
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || seenDropdown.areAnyItemsHovered()
                || sfxDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomRelicFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        colorsDropdown.tryRender(sb);
        seenDropdown.tryRender(sb);
        sfxDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    protected boolean evaluateRelic(AbstractRelic c) {
        //Name check
        if (currentName != null && !currentName.isEmpty()) {
            String name = getNameForSort(c);
            if (name == null || !name.toLowerCase().contains(currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (currentDescription != null && !currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c);
            if (desc == null || !desc.toLowerCase().contains(currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(currentColors, (opt) -> opt == EUIGameUtils.getRelicColor(c.relicId))) {
            return false;
        }

        //Origin check
        if (!evaluateItem(currentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt))) {
            return false;
        }

        //Seen check
        if (!evaluateItem(currentSeen, (opt) -> opt.evaluate(c.relicId))) {
            return false;
        }

        //Tooltips check
        if (!currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(currentFilters))) {
            return false;
        }

        //Negate Tooltips check
        if (!currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), currentNegateFilters::contains))) {
            return false;
        }

        //Rarities check
        if (!currentRarities.isEmpty() && !currentRarities.contains(c.tier)) {
            return false;
        }

        //Sfx check
        if (!currentSfx.isEmpty() && !currentSfx.contains(EUIGameUtils.getLandingSound(c))) {
            return false;
        }

        //Module check
        return customModule == null || customModule.isItemValid(c);
    }

    public RelicKeywordFilters initializeForCustomHeader(RelicGroup group, AbstractCard.CardColor color, boolean isAccessedFromCardPool, boolean snapToGroup) {
        EUI.relicHeader.setGroup(group).snapToGroup(snapToGroup);
        EUI.relicFilters.initialize(button -> {
            EUI.relicHeader.updateForFilters();
            onClick.invoke(button);
        }, EUI.relicHeader.getOriginalRelics(), color, isAccessedFromCardPool);
        EUI.relicHeader.updateForFilters();
        EUIExporter.exportRelicButton.setOnClick(() -> EUIExporter.openForRelics(EUI.relicHeader.relicGroup.group));
        return this;
    }

    public RelicKeywordFilters initializeForCustomHeader(RelicGroup group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color, boolean isAccessedFromCardPool, boolean snapToGroup) {
        EUI.relicHeader.setGroup(group).snapToGroup(snapToGroup);
        EUI.relicFilters.initialize(button -> {
            EUI.relicHeader.updateForFilters();
            onClick.invoke(button);
        }, EUI.relicHeader.getOriginalRelics(), color, isAccessedFromCardPool);
        EUI.relicHeader.updateForFilters();
        EUIExporter.exportRelicButton.setOnClick(() -> EUIExporter.openForRelics(EUI.relicHeader.relicGroup.group));
        return this;
    }

    public enum SeenValue {
        Seen(EUIRM.strings.ui_seen, UnlockTracker::isRelicSeen),
        Unseen(EUIRM.strings.ui_unseen, c -> !UnlockTracker.isRelicSeen(c) && !UnlockTracker.isRelicLocked(c)),
        Locked(SingleRelicViewPopup.TEXT[8], UnlockTracker::isRelicLocked);

        public final FuncT1<Boolean, String> evalFunc;
        public final String name;

        SeenValue(String name, FuncT1<Boolean, String> evalFunc) {
            this.evalFunc = evalFunc;
            this.name = name;
        }

        public boolean evaluate(String relicID) {
            return evalFunc.invoke(relicID);
        }
    }
}
