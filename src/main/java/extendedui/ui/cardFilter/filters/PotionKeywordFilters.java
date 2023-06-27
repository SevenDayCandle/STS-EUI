package extendedui.ui.cardFilter.filters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.*;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomFilterable;
import extendedui.interfaces.markers.CustomPotionFilterModule;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.cardFilter.FilterKeywordButton;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.ItemGroup;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.TargetFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PotionKeywordFilters extends GenericFilters<AbstractPotion, CustomPotionFilterModule> {
    public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<AbstractPotion.PotionRarity> currentRarities = new HashSet<>();
    public final HashSet<AbstractPotion.PotionSize> currentSizes = new HashSet<>();
    public final HashSet<AbstractPotion.PotionEffect> currentVfx = new HashSet<>();
    public final HashSet<TargetFilter> currentTargets = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<AbstractPotion.PotionRarity> raritiesDropdown;
    public final EUIDropdown<AbstractPotion.PotionSize> sizesDropdown;
    public final EUIDropdown<AbstractPotion.PotionEffect> vfxDropdown;
    public final EUIDropdown<TargetFilter> targetsDropdown;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;

    public PotionKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractPotion.PotionRarity>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionRarity.values());

        sizesDropdown = new EUIDropdown<AbstractPotion.PotionSize>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionSize)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentSizes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_size)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionSize.values());

        vfxDropdown = new EUIDropdown<AbstractPotion.PotionEffect>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionEffect)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentVfx, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.potion_size)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionEffect.values());

        targetsDropdown = new EUIDropdown<TargetFilter>(new EUIHitbox(0, 0, scale(240), scale(48))
                , t -> t.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentTargets, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_target)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(TargetFilter.None);

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_colors)
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

    public ArrayList<PotionInfo> applyInfoFilters(ArrayList<PotionInfo> input) {
        return EUIUtils.filter(input, info -> evaluatePotion(info.potion));
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
        currentSizes.clear();
        currentTargets.clear();
        currentVfx.clear();
        currentName = null;
        currentDescription = null;
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        sizesDropdown.setSelectionIndices((int[]) null, false);
        vfxDropdown.setSelectionIndices((int[]) null, false);
        targetsDropdown.setSelectionIndices((int[]) null, false);
        colorsDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
        doForFilters(CustomPotionFilterModule::reset);
    }

    public ArrayList<AbstractPotion> applyFilters(ArrayList<AbstractPotion> input) {
        return EUIUtils.filter(input, this::evaluatePotion);
    }

    @Override
    public boolean areFiltersEmpty() {
        return (currentName == null || currentName.isEmpty())
                && (currentDescription == null || currentDescription.isEmpty())
                && currentColors.isEmpty() && currentOrigins.isEmpty() && currentRarities.isEmpty() && currentSizes.isEmpty() && currentTargets.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty()
                && EUIUtils.all(getGlobalFilters(), CustomPotionFilterModule::isEmpty)
                && (customModule != null && customModule.isEmpty());
    }

    @Override
    public ArrayList<CustomPotionFilterModule> getGlobalFilters() {
        return EUI.globalCustomPotionFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractPotion> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        customModule = EUI.getCustomPotionFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractPotion.PotionRarity> availableRarities = new HashSet<>();
        HashSet<AbstractPotion.PotionSize> availableSizes = new HashSet<>();
        HashSet<AbstractPotion.PotionEffect> availableVfx = new HashSet<>();
        HashSet<TargetFilter> availableTargets = new HashSet<>();
        if (referenceItems != null) {
            currentTotal = getReferenceCount();
            for (AbstractPotion potion : referenceItems) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(potion)) {
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
            doForFilters(m -> m.initializeSelection(referenceItems));
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

    public void toggleFilters() {
        if (EUI.potionFilters.isActive) {
            EUI.potionFilters.close();
        }
        else {
            EUI.potionFilters.open();
        }
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
        doForFilters(CustomPotionFilterModule::update);
    }

    public List<EUIKeywordTooltip> getAllTooltips(AbstractPotion c) {
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
                || sizesDropdown.areAnyItemsHovered()
                || vfxDropdown.areAnyItemsHovered()
                || targetsDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomPotionFilterModule::isHovered)
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

    protected boolean evaluatePotion(AbstractPotion c) {
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
        if (!evaluateItem(currentColors, (opt) -> opt == EUIGameUtils.getPotionColor(c.ID))) {
            return false;
        }

        //Origin check
        if (!evaluateItem(currentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt))) {
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
        if (!currentRarities.isEmpty() && !currentRarities.contains(c.rarity)) {
            return false;
        }

        //Module check
        for (CustomPotionFilterModule module : EUI.globalCustomPotionFilters) {
            if (!module.isItemValid(c)) {
                return false;
            }
        }

        return customModule == null || customModule.isItemValid(c);
    }

    public PotionKeywordFilters initializeForCustomHeader(ItemGroup<PotionInfo> group, AbstractCard.CardColor color, boolean isAccessedFromCardPool, boolean snapToGroup) {
        EUI.potionHeader.setGroup(group).snapToGroup(snapToGroup);
        EUI.potionFilters.initialize(button -> {
            EUI.potionHeader.updateForFilters();
            onClick.invoke(button);
        }, EUI.potionHeader.getOriginalPotions(), color, isAccessedFromCardPool);
        EUI.potionHeader.updateForFilters();
        EUIExporter.exportPotionButton.setOnClick(() -> EUIExporter.openForPotions(EUI.potionHeader.group.group));
        return this;
    }

    public PotionKeywordFilters initializeForCustomHeader(ItemGroup<PotionInfo> group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color, boolean isAccessedFromCardPool, boolean snapToGroup) {
        EUI.potionHeader.setGroup(group).snapToGroup(snapToGroup);
        EUI.potionFilters.initialize(button -> {
            EUI.potionHeader.updateForFilters();
            onClick.invoke(button);
        }, EUI.potionHeader.getOriginalPotions(), color, isAccessedFromCardPool);
        EUI.potionHeader.updateForFilters();
        EUIExporter.exportPotionButton.setOnClick(() -> EUIExporter.openForPotions(EUI.potionHeader.group.group));
        return this;
    }
}
