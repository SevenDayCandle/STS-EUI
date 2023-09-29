package extendedui.ui.cardFilter;

import basemod.abstracts.CustomCard;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomCardFilterModule;
import extendedui.interfaces.markers.CustomFilterable;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class CardKeywordFilters extends GenericFilters<AbstractCard, CardKeywordFilters.CardFilters, CustomCardFilterModule> {
    private static AbstractCard.CardColor packmasterColor;
    private static FuncT1<String, Object> getPackmasterPack;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUIDropdown<AbstractCard.CardRarity> raritiesDropdown;
    public final EUIDropdown<AbstractCard.CardType> typesDropdown;
    public final EUIDropdown<CostFilter> costDropdown;
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<SeenValue> seenDropdown;
    public final EUIDropdown<TargetFilter> targetsDropdown;

    public CardKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        costDropdown = new EUIDropdown<CostFilter>(new EUIHitbox(0, 0, scale(160), scale(48)), c -> c.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentCosts, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(CostFilter.values());

        raritiesDropdown = new EUIDropdown<AbstractCard.CardRarity>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardRarity.values());

        typesDropdown = new EUIDropdown<AbstractCard.CardType>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForType)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTypes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractCard.CardType.values());

        targetsDropdown = new EUIDropdown<TargetFilter>(new EUIHitbox(0, 0, scale(240), scale(48))
                , t -> t.name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTargets, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_target)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(TargetFilter.None);

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
                .setOnChange(costs -> this.onFilterChanged(filters.currentSeen, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_seen)
                .setItems(SeenValue.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(AbstractCard c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getDescriptionForSort();
        }
        return c.rawDescription != null ? CardModifierManager.onCreateDescription(c, c.rawDescription) : null;
    }

    public static String getNameForSort(AbstractCard c) {
        if (c instanceof CustomFilterable) {
            return ((CustomFilterable) c).getNameForSort();
        }
        return c.name != null ? CardModifierManager.onRenderTitle(c, c.name) : null;
    }

    public static int rankByAmount(AbstractCard a, AbstractCard b) {
        int aV = Math.max(a.baseDamage, a.baseBlock);
        int bV = Math.max(b.baseDamage, b.baseBlock);
        return aV - bV;
    }

    public static int rankByColor(AbstractCard a, AbstractCard b) {
        return (a == null ? -1 : b == null ? 1 : a.color.ordinal() - b.color.ordinal());
    }

    public static int rankByCost(AbstractCard a, AbstractCard b) {
        return a.cost - b.cost;
    }

    public static int rankByName(AbstractCard a, AbstractCard b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.name, b.name));
    }

    public static int rankByRarity(AbstractCard a, AbstractCard b) {
        return (a == null ? -1 : b == null ? 1 : a.rarity.ordinal() - b.rarity.ordinal());
    }

    public static int rankByStatus(AbstractCard a, AbstractCard b) {
        int c1Rank = 0;
        if (UnlockTracker.isCardLocked(a.cardID)) {
            c1Rank = 2;
        }
        else if (!UnlockTracker.isCardSeen(a.cardID)) {
            c1Rank = 1;
        }

        int c2Rank = 0;
        if (UnlockTracker.isCardLocked(b.cardID)) {
            c2Rank = 2;
        }
        else if (!UnlockTracker.isCardSeen(b.cardID)) {
            c2Rank = 1;
        }

        return c1Rank - c2Rank;
    }

    public static int rankByType(AbstractCard a, AbstractCard b) {
        return (a == null ? -1 : b == null ? 1 : a.type.ordinal() - b.type.ordinal());
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        costDropdown.setSelectionIndices((int[]) null, false);
        originsDropdown.setSelectionIndices((int[]) null, false);
        typesDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        targetsDropdown.setSelectionIndices((int[]) null, false);
        seenDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(CardFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        colorsDropdown.setSelection(filters.currentColors, true);
        costDropdown.setSelection(filters.currentCosts, true);
        raritiesDropdown.setSelection(filters.currentRarities, true);
        seenDropdown.setSelection(filters.currentSeen, true);
        targetsDropdown.setSelection(filters.currentTargets, true);
        typesDropdown.setSelection(filters.currentTypes, true);
    }

    @Override
    public void defaultSort() {
        this.group.sort(CardKeywordFilters::rankByName,
                CardKeywordFilters::rankByType,
                CardKeywordFilters::rankByRarity,
                CardKeywordFilters::rankByStatus,
                CardKeywordFilters::rankByColor);
    }

    @Override
    public boolean evaluate(AbstractCard c) {
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

        //Colors check
        if (!evaluateItem(filters.currentColors, c.color)) {
            return false;
        }

        //Origin check
        if (!evaluateItem(filters.currentOrigins, EUIGameUtils.getModInfo(c))) {
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
        if (!evaluateItem(filters.currentRarities, c.rarity)) {
            return false;
        }

        //Types check
        if (!evaluateItem(filters.currentTypes, c.type)) {
            return false;
        }

        //Target check
        if (!evaluateItem(filters.currentTargets, TargetFilter.forCard(c))) {
            return false;
        }

        //Seen check
        if (!evaluateItem(filters.currentSeen, (opt) -> opt.evaluate(c.cardID))) {
            return false;
        }


        for (CustomCardFilterModule module : EUI.globalCustomCardFilters) {
            if (!module.isItemValid(c)) {
                return false;
            }
        }

        //Module check
        if (customModule != null && !customModule.isItemValid(c)) {
            return false;
        }

        //Cost check
        if (!filters.currentCosts.isEmpty()) {
            boolean passes = false;
            for (CostFilter cf : filters.currentCosts) {
                if (cf.check(c)) {
                    passes = true;
                    break;
                }
            }
            return passes;
        }

        return true;
    }

    public List<EUIKeywordTooltip> getAllTooltips(AbstractCard c) {
        KeywordProvider eC = EUIUtils.safeCast(c, KeywordProvider.class);
        if (eC != null) {
            return eC.getTipsForFilters();
        }

        ArrayList<EUIKeywordTooltip> dynamicTooltips = new ArrayList<>();
        if (c.isInnate) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(GameDictionary.EXHAUST.NAMES[0]);
            if (tip != null) {
                dynamicTooltips.add(tip);
            }
        }
        if (c.isEthereal) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(GameDictionary.ETHEREAL.NAMES[0]);
            if (tip != null) {
                dynamicTooltips.add(tip);
            }
        }
        if (c.selfRetain) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(GameDictionary.RETAIN.NAMES[0]);
            if (tip != null) {
                dynamicTooltips.add(tip);
            }
        }
        if (c.exhaust) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(GameDictionary.EXHAUST.NAMES[0]);
            if (tip != null) {
                dynamicTooltips.add(tip);
            }
        }
        for (String sk : c.keywords) {
            EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(sk);
            if (tip != null && !dynamicTooltips.contains(tip)) {
                dynamicTooltips.add(tip);
            }
        }
        if (c instanceof CustomCard) {
            List<TooltipInfo> infos = ((CustomCard) c).getCustomTooltips();
            ModInfo mi = EUIGameUtils.getModInfo(c);
            if (infos != null && mi != null) {
                for (TooltipInfo info : infos) {
                    EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(mi.ID.toLowerCase() + ":" + info.title);
                    if (tip != null && !dynamicTooltips.contains(tip)) {
                        dynamicTooltips.add(tip);
                    }
                }
            }
        }
        return dynamicTooltips;
    }

    @Override
    public EUIExporter.Exportable<AbstractCard> getExportable() {
        return EUIExporter.cardExportable;
    }

    @Override
    protected CardFilters getFilterObject() {
        return new CardFilters();
    }

    @Override
    public float getFirstY() {
        return group.group.get(0).current_y;
    }

    @Override
    public ArrayList<CustomCardFilterModule> getGlobalFilters() {
        return EUI.globalCustomCardFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractCard> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        customModule = EUI.getCustomCardFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availableCosts = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractCard.CardRarity> availableRarities = new HashSet<>();
        HashSet<AbstractCard.CardType> availableTypes = new HashSet<>();
        HashSet<TargetFilter> availableTargets = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (AbstractCard card : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(card)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(card));
                availableRarities.add(card.rarity);
                availableTypes.add(card.type);
                availableTargets.add(TargetFilter.forCard(card));
                availableCosts.add(MathUtils.clamp(card.cost, CostFilter.Unplayable.upperBound, CostFilter.Cost4Plus.lowerBound));
                availableColors.add(card.color);
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
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

        ArrayList<TargetFilter> targetItems = new ArrayList<>(availableTargets);
        targetItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.name, b.name));
        targetsDropdown.setItems(targetItems);

        ArrayList<CostFilter> costItems = new ArrayList<>();
        for (CostFilter c : CostFilter.values()) {
            if (availableCosts.contains(c.lowerBound) || availableCosts.contains(c.upperBound)) {
                costItems.add(c);
            }
        }
        costDropdown.setItems(costItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
        if (isAccessedFromCardPool) {
            ArrayList<AbstractCard.CardColor> filteredColors = EUIUtils.filter(colorsItems, c -> c != AbstractCard.CardColor.COLORLESS && c != AbstractCard.CardColor.CURSE);
            colorsDropdown.setSelection(filteredColors, true).setActive(false);
        }
        else {
            colorsDropdown.setActive(colorsItems.size() > 1);
        }
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || costDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || typesDropdown.areAnyItemsHovered()
                || targetsDropdown.areAnyItemsHovered()
                || seenDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomCardFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        colorsDropdown.tryRender(sb);
        costDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        typesDropdown.tryRender(sb);
        targetsDropdown.tryRender(sb);
        seenDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);

        for (CustomCardFilterModule module : EUI.globalCustomCardFilters) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        doForFilters(m -> m.render(sb));
    }

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {
        startX = makeToggle(header, CardKeywordFilters::rankByRarity, CardLibSortHeader.TEXT[0], startX);
        startX = makeToggle(header, CardKeywordFilters::rankByType, CardLibSortHeader.TEXT[1], startX);
        startX = makeToggle(header, CardKeywordFilters::rankByCost, CardLibSortHeader.TEXT[3], startX);
        startX = makeToggle(header, CardKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, CardKeywordFilters::rankByAmount, EUIRM.strings.ui_amount, startX);
        startX = tryMakePackasterSort(header, startX);
    }

    protected float tryMakePackasterSort(FilterSortHeader header, float startX) {
        if (Loader.isModLoaded("anniv5")) {
            if (packmasterColor == null) {
                try {
                    packmasterColor = EUIClassUtils.getRFieldStatic("thePackmaster.ThePackmaster.Enums", "PACKMASTER_RAINBOW");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    EUIUtils.logError(this, "Failed to get Packmaster color:" + e.getLocalizedMessage());
                    packmasterColor = AbstractCard.CardColor.COLORLESS;
                }
            }
            if (EUI.actingColor == packmasterColor) {
                if (getPackmasterPack == null) {
                    try {
                        Class<?> targetClass = Class.forName("thePackmaster.patches.CompendiumPatches.CustomOrdering");
                        getPackmasterPack = FuncT1.get(String.class, targetClass, "getParnetNameFromObject", Object.class);
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                        EUIUtils.logError(this, "Failed to get Packmaster color:" + e.getLocalizedMessage());
                        getPackmasterPack = String::valueOf;
                    }
                }
                UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("anniv5:Compendium");
                return makeToggle(header, (a, b) -> GenericFilters.rankByString(a, b, getPackmasterPack), uiStrings.TEXT[0], startX);
            }
        }
        return startX;
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        if (colorsDropdown.isActive) {
            xPos = updateDropdown(colorsDropdown, xPos);
            xPos = updateDropdown(costDropdown, xPos);
        }
        else {
            xPos = updateDropdown(costDropdown, xPos);
        }
        xPos = updateDropdown(raritiesDropdown, xPos);
        xPos = updateDropdown(typesDropdown, xPos);
        xPos = updateDropdown(targetsDropdown, xPos);
        xPos = updateDropdown(seenDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomCardFilterModule::update);
    }

    public enum SeenValue {
        Seen(EUIRM.strings.ui_seen, UnlockTracker::isCardSeen),
        Unseen(EUIRM.strings.ui_unseen, c -> !UnlockTracker.isCardSeen(c) && !UnlockTracker.isCardLocked(c)),
        Locked(SingleRelicViewPopup.TEXT[8], UnlockTracker::isCardLocked);

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

    public static class CardFilters extends GenericFiltersObject {
        public final ArrayList<SeenValue> currentSeen = new ArrayList<>();
        public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
        public final HashSet<AbstractCard.CardRarity> currentRarities = new HashSet<>();
        public final HashSet<AbstractCard.CardType> currentTypes = new HashSet<>();
        public final HashSet<CostFilter> currentCosts = new HashSet<>();
        public final HashSet<TargetFilter> currentTargets = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentCosts.clear();
            currentRarities.clear();
            currentSeen.clear();
            currentTargets.clear();
            currentTypes.clear();
            if (shouldClearColors) {
                currentColors.clear();
            }
        }

        public void cloneFrom(CardFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentColors, other.currentColors);
            EUIUtils.replaceContents(currentCosts, other.currentCosts);
            EUIUtils.replaceContents(currentRarities, other.currentRarities);
            EUIUtils.replaceContents(currentSeen, other.currentSeen);
            EUIUtils.replaceContents(currentTargets, other.currentTargets);
            EUIUtils.replaceContents(currentTypes, other.currentTypes);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentColors.isEmpty()
                    && currentCosts.isEmpty() && currentRarities.isEmpty()
                    && currentTypes.isEmpty() && currentTargets.isEmpty() && currentSeen.isEmpty();
        }
    }
}
