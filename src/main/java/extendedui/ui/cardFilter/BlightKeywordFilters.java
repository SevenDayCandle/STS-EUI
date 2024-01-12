package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
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
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.BlightTier;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlightKeywordFilters extends GenericFilters<AbstractBlight, BlightKeywordFilters.BlightFilters, CustomFilterModule<AbstractBlight>> {
    protected final static String[] TEXT = CardCrawlGame.languagePack.getUIString("ConfirmPopup").TEXT;
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<BlightTier> raritiesDropdown;
    public final EUIDropdown<UniqueValue> seenDropdown;

    public BlightKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<BlightTier>(new EUIHitbox(0, 0, scale(240), scale(48))
                , BlightTier::getName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(BlightTier.values());

        seenDropdown = new EUIDropdown<UniqueValue>(new EUIHitbox(0, 0, scale(240), scale(48))
                , item -> item.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentSeen, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_unique)
                .setItems(UniqueValue.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(AbstractBlight c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getDescriptionForSort();
        }
        return c.description;
    }

    public static String getNameForSort(AbstractBlight c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getNameForSort();
        }
        return c.name;
    }

    public static int rankByName(AbstractBlight a, AbstractBlight b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.name, b.name));
    }

    public static int rankByRarity(AbstractBlight a, AbstractBlight b) {
        return (a == null ? -1 : b == null ? 1 : BlightTier.getTier(a).ordinal() - BlightTier.getTier(b).ordinal());
    }

    public static int rankByUnique(AbstractBlight a, AbstractBlight b) {
        int aValue = a == null || a.unique ? 1 : 0;
        int bValue = b == null || b.unique ? 1 : 0;
        return aValue - bValue;
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        seenDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(BlightFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        raritiesDropdown.setSelection(filters.currentRarities, true);
        seenDropdown.setSelection(filters.currentSeen, true);
    }

    @Override
    public void defaultSort() {
        this.group.sort(BlightKeywordFilters::rankByName,
                BlightKeywordFilters::rankByRarity,
                BlightKeywordFilters::rankByUnique);
    }

    public boolean evaluate(AbstractBlight c) {
        //Name check
        if (filters.currentName != null && !filters.currentName.isEmpty()) {
            String name = getNameForSort(c);
            if (name == null || !name.toLowerCase().contains(filters.currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (filters.currentDescription != null && !filters.currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c);
            if (desc == null || !desc.toLowerCase().contains(filters.currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Origin check
        if (!evaluateItem(filters.currentOrigins, EUIGameUtils.getModInfo(c))) {
            return false;
        }

        //Seen check
        if (!evaluateItem(filters.currentSeen, (opt) -> opt.evaluate(c))) {
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
        if (!evaluateItem(filters.currentRarities, BlightTier.getTier(c))) {
            return false;
        }

        //Module check
        return customModule == null || customModule.isItemValid(c);
    }

    public List<EUIKeywordTooltip> getAllTooltips(AbstractBlight c) {
        KeywordProvider eC = EUIUtils.safeCast(c, KeywordProvider.class);
        if (eC != null) {
            return eC.getTipsForFilters();
        }

        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();
        // Skip the first tip
        for (int i = 1; i < c.tips.size(); i++) {
            PowerTip sk = c.tips.get(i);
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(StringUtils.lowerCase(sk.header));
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
    public EUIExporter.Exportable<AbstractBlight> getExportable() {
        return EUIExporter.blightExportable;
    }

    @Override
    protected BlightFilters getFilterObject() {
        return new BlightFilters();
    }

    @Override
    public float getFirstY() {
        return group.group.get(0).hb.y;
    }

    @Override
    public ArrayList<CustomFilterModule<AbstractBlight>> getGlobalFilters() {
        return EUI.globalCustomBlightFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractBlight> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        HashSet<ModInfo> availableMods = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (AbstractBlight relic : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(relic)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(relic));
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || seenDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        seenDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {
        startX = makeToggle(header, BlightKeywordFilters::rankByRarity, CardLibSortHeader.TEXT[0], startX);
        startX = makeToggle(header, BlightKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, BlightKeywordFilters::rankByUnique, EUIRM.strings.ui_unique, startX);
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(raritiesDropdown, xPos);
        xPos = updateDropdown(seenDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<AbstractBlight>::update);
    }

    public enum UniqueValue {
        Unique(TEXT[2], c -> c.unique),
        NonUnique(TEXT[3], c -> !c.unique);

        public final FuncT1<Boolean, AbstractBlight> evalFunc;
        public final String name;

        UniqueValue(String name, FuncT1<Boolean, AbstractBlight> evalFunc) {
            this.evalFunc = evalFunc;
            this.name = name;
        }

        public boolean evaluate(AbstractBlight blight) {
            return evalFunc.invoke(blight);
        }
    }

    public static class BlightFilters extends GenericFiltersObject {
        public final ArrayList<UniqueValue> currentSeen = new ArrayList<>();
        public final HashSet<BlightTier> currentRarities = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentRarities.clear();
            currentSeen.clear();
        }

        public void cloneFrom(BlightFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentRarities, other.currentRarities);
            EUIUtils.replaceContents(currentSeen, other.currentSeen);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentRarities.isEmpty() && currentSeen.isEmpty();
        }
    }
}
