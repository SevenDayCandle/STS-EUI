package extendedui.ui.cardFilter;

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
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.interfaces.markers.CustomFilterable;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.patches.game.TooltipPatches;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RelicKeywordFilters extends GenericFilters<RelicInfo, RelicKeywordFilters.RelicFilters, CustomFilterModule<RelicInfo>> {
    public final ArrayList<SeenValue> currentSeen = new ArrayList<>();
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUIDropdown<AbstractRelic.LandingSound> sfxDropdown;
    public final EUIDropdown<AbstractRelic.RelicTier> raritiesDropdown;
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<SeenValue> seenDropdown;

    public RelicKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractRelic.RelicTier>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicTier)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.RelicTier.values());

        sfxDropdown = new EUIDropdown<AbstractRelic.LandingSound>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRelicLandingSound)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentSfx, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractRelic.LandingSound.values());

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentColors, costs))
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

    public static int rankByColor(RelicInfo a, RelicInfo b) {
        return (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal());
    }

    public static int rankByName(RelicInfo a, RelicInfo b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.relic.name, b.relic.name));
    }

    public static int rankByRarity(RelicInfo a, RelicInfo b) {
        return (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal());
    }

    public static int rankBySeen(RelicInfo a, RelicInfo b) {
        int aValue = a == null || a.locked ? 2 : a.relic.isSeen ? 1 : 0;
        int bValue = b == null || b.locked ? 2 : b.relic.isSeen ? 1 : 0;
        return aValue - bValue;
    }

    public ArrayList<AbstractRelic> applyFiltersToRelics(ArrayList<AbstractRelic> input) {
        return EUIUtils.filter(input, this::evaluate);
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        sfxDropdown.setSelectionIndices((int[]) null, false);
        colorsDropdown.setSelectionIndices((int[]) null, false);
        seenDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(RelicFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        colorsDropdown.setSelection(filters.currentColors, true);
        raritiesDropdown.setSelection(filters.currentRarities, true);
        seenDropdown.setSelection(filters.currentSeen, true);
        sfxDropdown.setSelection(filters.currentSfx, true);
    }

    @Override
    public void defaultSort() {
        this.group.sort(RelicKeywordFilters::rankByName);
        this.group.sort(RelicKeywordFilters::rankByRarity);
        this.group.sort(RelicKeywordFilters::rankByColor);
        this.group.sort(RelicKeywordFilters::rankBySeen);
    }

    public boolean evaluate(RelicInfo c) {
        //Name check
        if (filters.currentName != null && !filters.currentName.isEmpty()) {
            String name = getNameForSort(c.relic);
            if (name == null || !name.toLowerCase().contains(filters.currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (filters.currentDescription != null && !filters.currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c.relic);
            if (desc == null || !desc.toLowerCase().contains(filters.currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(filters.currentColors, c.relicColor)) {
            return false;
        }

        //Origin check
        if (!evaluateItem(filters.currentOrigins, EUIGameUtils.getModInfo(c.relic))) {
            return false;
        }

        //Seen check
        if (!evaluateItem(currentSeen, (opt) -> opt.evaluate(c.relic.relicId))) {
            return false;
        }

        //Tooltips check
        if (!filters.currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(filters.currentFilters))) {
            return false;
        }

        //Negate Tooltips check
        if (!filters.currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), filters.currentNegateFilters::contains))) {
            return false;
        }

        //Rarities check
        if (!evaluateItem(filters.currentRarities, c.relic.tier)) {
            return false;
        }

        //Sfx check
        if (!evaluateItem(filters.currentSfx, EUIGameUtils.getLandingSound(c.relic))) {
            return false;
        }

        //Module check
        return customModule == null || customModule.isItemValid(c);
    }

    public boolean evaluate(AbstractRelic c) {
        return evaluate(new RelicInfo(c));
    }

    public List<EUIKeywordTooltip> getAllTooltips(RelicInfo c) {
        KeywordProvider eC = EUIUtils.safeCast(c.relic, KeywordProvider.class);
        if (eC != null) {
            return eC.getTipsForFilters();
        }

        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();
        // Skip the first tip
        for (int i = 1; i < c.relic.tips.size(); i++) {
            PowerTip sk = c.relic.tips.get(i);
            String key = TooltipPatches.PowerTip_Keyword.value.get(sk);
            if (key == null) {
                key = StringUtils.lowerCase(sk.header);
            }
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(key);
            if (tip == null) {
                tip = getTemporaryTip(sk);
            }
            if (!dynamicTooltips.contains(tip)) {
                dynamicTooltips.add(tip);
            }
        }
        return dynamicTooltips;
    }

    @Override
    public EUIExporter.Exportable<RelicInfo> getExportable() {
        return EUIExporter.relicExportable;
    }

    @Override
    protected RelicFilters getFilterObject() {
        return new RelicFilters();
    }

    @Override
    public float getFirstY() {
        return group.group.get(0).relic.hb.y;
    }

    @Override
    public ArrayList<CustomFilterModule<RelicInfo>> getGlobalFilters() {
        return EUI.globalCustomRelicFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<RelicInfo> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        customModule = EUI.getCustomRelicFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractRelic.RelicTier> availableRarities = new HashSet<>();
        HashSet<AbstractRelic.LandingSound> availableSfx = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (RelicInfo relic : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(relic)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(relic.relic));
                availableRarities.add(relic.relic.tier);
                availableSfx.add(EUIGameUtils.getLandingSound(relic.relic));
                availableColors.add(EUIGameUtils.getRelicColor(relic.relic.relicId));
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
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

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || seenDropdown.areAnyItemsHovered()
                || sfxDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
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

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {

        startX = makeToggle(header, RelicKeywordFilters::rankByRarity, CardLibSortHeader.TEXT[0], startX);
        startX = makeToggle(header, RelicKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, RelicKeywordFilters::rankByColor, EUIRM.strings.ui_colors, startX);
        startX = makeToggle(header, RelicKeywordFilters::rankBySeen, EUIRM.strings.ui_seen, startX);
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(colorsDropdown, xPos);
        xPos = updateDropdown(raritiesDropdown, xPos);
        xPos = updateDropdown(sfxDropdown, xPos);
        xPos = updateDropdown(seenDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<RelicInfo>::update);
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

    public static class RelicFilters extends GenericFiltersObject {
        public final ArrayList<SeenValue> currentSeen = new ArrayList<>();
        public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
        public final HashSet<AbstractRelic.LandingSound> currentSfx = new HashSet<>();
        public final HashSet<AbstractRelic.RelicTier> currentRarities = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentRarities.clear();
            currentSeen.clear();
            currentSfx.clear();
            if (shouldClearColors) {
                currentColors.clear();
            }
        }

        public void cloneFrom(RelicFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentColors, other.currentColors);
            EUIUtils.replaceContents(currentRarities, other.currentRarities);
            EUIUtils.replaceContents(currentSeen, other.currentSeen);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentColors.isEmpty()
                    && currentSfx.isEmpty() && currentRarities.isEmpty() && currentSeen.isEmpty();
        }
    }
}
