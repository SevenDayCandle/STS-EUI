package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.interfaces.markers.CustomFilterable;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.TargetFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PotionKeywordFilters extends GenericFilters<PotionInfo, PotionKeywordFilters.PotionFilters, CustomFilterModule<PotionInfo>> {
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUIDropdown<AbstractPotion.PotionEffect> vfxDropdown;
    public final EUIDropdown<AbstractPotion.PotionRarity> raritiesDropdown;
    public final EUIDropdown<AbstractPotion.PotionSize> sizesDropdown;
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<TargetFilter> targetsDropdown;

    public PotionKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractPotion.PotionRarity>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionRarity.values());

        sizesDropdown = new EUIDropdown<AbstractPotion.PotionSize>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionSize)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentSizes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_size)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionSize.values());

        vfxDropdown = new EUIDropdown<AbstractPotion.PotionEffect>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionEffect)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentVfx, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_visualEffect)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionEffect.values());

        targetsDropdown = new EUIDropdown<TargetFilter>(new EUIHitbox(0, 0, scale(240), scale(48))
                , t -> t.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTargets, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_target)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(TargetFilter.None);

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_colors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(AbstractPotion c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getDescriptionForSort();
        }
        return c.description;
    }

    public static String getNameForSort(AbstractPotion c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getNameForSort();
        }
        return c.name;
    }

    public static int rankByAmount(PotionInfo a, PotionInfo b) {
        return (a == null ? -1 : b == null ? 1 : a.potion.getPotency() - b.potion.getPotency());
    }

    public static int rankByColor(PotionInfo a, PotionInfo b) {
        return (a == null ? -1 : b == null ? 1 : a.potionColor.ordinal() - b.potionColor.ordinal());
    }

    public static int rankByName(PotionInfo a, PotionInfo b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.potion.name, b.potion.name));
    }

    public static int rankByRarity(PotionInfo a, PotionInfo b) {
        return (a == null ? -1 : b == null ? 1 : a.potion.rarity.ordinal() - b.potion.rarity.ordinal());
    }

    public ArrayList<AbstractPotion> applyFiltersToPotions(ArrayList<AbstractPotion> input) {
        return EUIUtils.filter(input, this::evaluate);
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        sizesDropdown.setSelectionIndices((int[]) null, false);
        vfxDropdown.setSelectionIndices((int[]) null, false);
        targetsDropdown.setSelectionIndices((int[]) null, false);
        colorsDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(PotionFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        colorsDropdown.setSelection(filters.currentColors, true);
        raritiesDropdown.setSelection(filters.currentRarities, true);
        sizesDropdown.setSelection(filters.currentSizes, true);
        vfxDropdown.setSelection(filters.currentVfx, true);
        targetsDropdown.setSelection(filters.currentTargets, true);
    }

    @Override
    public void defaultSort() {
        this.group.sort(PotionKeywordFilters::rankByName, PotionKeywordFilters::rankByRarity, PotionKeywordFilters::rankByColor);
    }

    public boolean evaluate(PotionInfo c) {
        //Name check
        if (filters.currentName != null && !filters.currentName.isEmpty()) {
            String name = getNameForSort(c.potion);
            if (name == null || !name.toLowerCase().contains(filters.currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (filters.currentDescription != null && !filters.currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c.potion);
            if (desc == null || !desc.toLowerCase().contains(filters.currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(filters.currentColors, c.potionColor)) {
            return false;
        }

        //Origin check
        if (!evaluateItem(filters.currentOrigins, EUIGameUtils.getModInfo(c.potion))) {
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
        if (!evaluateItem(filters.currentRarities, c.potion.rarity)) {
            return false;
        }

        //Size check
        if (!evaluateItem(filters.currentSizes, c.potion.size)) {
            return false;
        }

        //Vfx check
        if (!evaluateItem(filters.currentVfx, c.potion.p_effect)) {
            return false;
        }

        //Module check
        for (CustomFilterModule<PotionInfo> module : EUI.globalCustomPotionFilters) {
            if (!module.isItemValid(c)) {
                return false;
            }
        }

        return customModule == null || customModule.isItemValid(c);
    }

    public boolean evaluate(AbstractPotion c) {
        return evaluate(new PotionInfo(c));
    }

    public List<EUIKeywordTooltip> getAllTooltips(PotionInfo c) {
        KeywordProvider eC = EUIUtils.safeCast(c.potion, KeywordProvider.class);
        if (eC != null) {
            return eC.getTipsForFilters();
        }

        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();
        // Skip the first tip
        for (int i = 1; i < c.potion.tips.size(); i++) {
            PowerTip sk = c.potion.tips.get(i);
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
    public EUIExporter.Exportable<PotionInfo> getExportable() {
        return EUIExporter.potionExportable;
    }

    @Override
    protected PotionFilters getFilterObject() {
        return new PotionFilters();
    }

    @Override
    public float getFirstY() {
        return this.group.group.get(0).potion.posY;
    }

    @Override
    public ArrayList<CustomFilterModule<PotionInfo>> getGlobalFilters() {
        return EUI.globalCustomPotionFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<PotionInfo> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        customModule = EUI.getCustomPotionFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractPotion.PotionRarity> availableRarities = new HashSet<>();
        HashSet<AbstractPotion.PotionSize> availableSizes = new HashSet<>();
        HashSet<AbstractPotion.PotionEffect> availableVfx = new HashSet<>();
        HashSet<TargetFilter> availableTargets = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (PotionInfo info : originalGroup) {
                AbstractPotion potion = info.potion;
                for (EUIKeywordTooltip tooltip : getAllTooltips(info)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(potion));
                availableRarities.add(potion.rarity);
                availableColors.add(EUIGameUtils.getPotionColor(potion.ID));
                availableSizes.add(potion.size);
                availableVfx.add(potion.p_effect);
                availableTargets.add(TargetFilter.forPotion(potion));
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);

        ArrayList<AbstractPotion.PotionRarity> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        raritiesDropdown.setItems(rarityItems);

        sizesDropdown.setItems(availableSizes).sortByLabel();

        ArrayList<AbstractPotion.PotionEffect> vfxItems = new ArrayList<>(availableVfx);
        vfxItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        vfxDropdown.setItems(vfxItems);

        ArrayList<TargetFilter> targetItems = new ArrayList<>(availableTargets);
        targetItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.name, b.name));
        targetsDropdown.setItems(targetItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || sizesDropdown.areAnyItemsHovered()
                || vfxDropdown.areAnyItemsHovered()
                || targetsDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        targetsDropdown.tryRender(sb);
        sizesDropdown.tryRender(sb);
        vfxDropdown.tryRender(sb);
        colorsDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {

        startX = makeToggle(header, PotionKeywordFilters::rankByRarity, CardLibSortHeader.TEXT[0], startX);
        startX = makeToggle(header, PotionKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, PotionKeywordFilters::rankByColor, EUIRM.strings.ui_colors, startX);
        startX = makeToggle(header, PotionKeywordFilters::rankByAmount, EUIRM.strings.ui_amount, startX);
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(colorsDropdown, xPos);
        xPos = updateDropdown(raritiesDropdown, xPos);
        xPos = updateDropdown(targetsDropdown, xPos);
        xPos = updateDropdown(sizesDropdown, xPos);
        xPos = updateDropdown(vfxDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<PotionInfo>::update);
    }

    public static class PotionFilters extends GenericFiltersObject {
        public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
        public final HashSet<AbstractPotion.PotionEffect> currentVfx = new HashSet<>();
        public final HashSet<AbstractPotion.PotionRarity> currentRarities = new HashSet<>();
        public final HashSet<AbstractPotion.PotionSize> currentSizes = new HashSet<>();
        public final HashSet<TargetFilter> currentTargets = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentRarities.clear();
            currentVfx.clear();
            currentSizes.clear();
            currentTargets.clear();
            if (shouldClearColors) {
                currentColors.clear();
            }
        }

        public void cloneFrom(PotionFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentColors, other.currentColors);
            EUIUtils.replaceContents(currentRarities, other.currentRarities);
            EUIUtils.replaceContents(currentSizes, other.currentSizes);
            EUIUtils.replaceContents(currentTargets, other.currentTargets);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentColors.isEmpty()
                    && currentVfx.isEmpty() && currentRarities.isEmpty() && currentSizes.isEmpty() && currentTargets.isEmpty();
        }
    }
}
