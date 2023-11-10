package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.EUIHotkeys;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.ItemGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class GenericFilters<T, U extends GenericFiltersObject, V extends CustomFilterModule<T>> extends EUICanvasGrid {
    protected static final HashMap<String, EUIKeywordTooltip> TEMPORARY_TIPS = new HashMap<>(); // Because AbstractPotion HAS NO STANDARDIZED WAY OF ADDING TIPS, there's no way for us to infer where the tip came from
    protected static final Color FADE_COLOR = new Color(0f, 0f, 0f, 0.84f);
    public static final float DRAW_START_X = (float) Settings.WIDTH * 0.15f;
    public static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.87f;
    public static final float FILTERS_START_X = (float) Settings.WIDTH * 0.28f;
    public static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    public static final float PAD_Y = scale(45);
    public static final float SPACING = Settings.scale * 18f;
    public static final int ROW_SIZE = 8;
    public static final int THRESHOLD = ROW_SIZE * 3;
    protected final ArrayList<FilterKeywordButton> filterButtons = new ArrayList<>();
    protected final EUIHitbox hb;
    protected final HashMap<EUIKeywordTooltip, Integer> currentFilterCounts = new HashMap<>();
    public final EUIButton clearButton;
    public final EUIButton closeButton;
    public final EUILabel currentTotalHeaderLabel;
    public final EUILabel currentTotalLabel;
    public final EUILabel keywordsSectionLabel;
    public final EUITextBox keywordsInstructionLabel;
    public final EUITextBoxInput descriptionInput;
    public final EUITextBoxInput nameInput;
    public final EUIToggle sortDirectionToggle;
    public final EUIToggle sortTypeToggle;
    public final U filters;
    private FilterKeywordButton selectedButton;
    private boolean shouldSortByCount;
    private boolean sortDesc;
    protected ActionT1<FilterKeywordButton> onClick;
    protected ArrayList<T> originalGroup;
    protected Comparator<T> comparator;
    protected boolean invalidated;
    protected boolean isAccessedFromCardPool;
    protected float drawX;
    protected int currentTotal;
    public ItemGroup<T> group;
    public V customModule;
    public boolean isReverseOrder;

    public GenericFilters() {
        super(ROW_SIZE, PAD_Y);
        filters = getFilterObject();
        isActive = false;
        hb = new EUIHitbox(DRAW_START_X, DRAW_START_Y, scale(180), scale(70)).setIsPopupCompatible(true);
        closeButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).setText(CombatRewardScreen.TEXT[6])
                .setOnClick(this::close)
                .setColor(Color.GRAY);
        clearButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setColor(Color.FIREBRICK)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setText(EUIRM.strings.misc_clear)
                .setOnClick(() -> this.clear(true, isAccessedFromCardPool));

        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(0, 0, scale(320), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    filters.currentName = s;
                    if (onClick != null) {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.8f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture());
        descriptionInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(0, 0, scale(360), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    filters.currentDescription = s;
                    if (onClick != null) {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_descriptionSearch)
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.8f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture());

        keywordsSectionLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(0, 0, scale(48), scale(48)), 0.8f)
                .setLabel(EUIRM.strings.ui_keywords)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.0f, false);
        keywordsInstructionLabel = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.WIDTH * 0.4f, scale(64)), EUIFontHelper.cardTooltipFont, 0.85f)
                .setColors(Color.BLACK, Settings.CREAM_COLOR)
                .setLabel(EUIRM.strings.misc_keywordInstructions)
                .setAlignment(0.75f, 0.02f, true);
        currentTotalHeaderLabel = new EUILabel(EUIFontHelper.cardTitleFontNormal,
                new EUIHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.94f, scale(48), scale(48)), 1f)
                .setLabel(EUIRM.strings.ui_total)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.0f, false);
        currentTotalLabel = new EUILabel(EUIFontHelper.cardTitleFontNormal,
                new EUIHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.906f, scale(48), scale(48)), 1f)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setAlignment(0.5f, 0.0f, false);

        sortTypeToggle = new EUIToggle(new EUIHitbox(0, 0, scale(135), scale(32)).setIsPopupCompatible(true))
                .setBackground(EUIRM.images.rectangularButton.texture(), Color.DARK_GRAY)
                .setTickImage(null, null, 10)
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.7f)
                .setText(EUIRM.strings.misc_sortByCount)
                .setOnToggle(val -> {
                    shouldSortByCount = val;
                    refreshButtonOrder();
                });

        sortDirectionToggle = new EUIToggle(new EUIHitbox(0, 0, scale(48), scale(48)).setIsPopupCompatible(true))
                .setTickImage(new EUIImage(EUIRM.images.arrow.texture()), new EUIImage(EUIRM.images.arrow.texture()).setRotation(180f), 32)
                .setOnToggle(val -> {
                    sortDesc = val;
                    refreshButtonOrder();
                });
    }

    protected static EUIKeywordTooltip getTemporaryTip(PowerTip sk) {
        EUIKeywordTooltip tip = TEMPORARY_TIPS.get(sk.header);
        if (tip == null) {
            tip = new EUIKeywordTooltip(sk.header, sk.body);
            TEMPORARY_TIPS.put(sk.header, tip);
        }
        return tip;
    }

    public static <T> int rankByString(T a, T b, FuncT1<String, T> stringFunc) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(stringFunc.invoke(a), stringFunc.invoke(b)));
    }

    public void addManualKeyword(EUIKeywordTooltip tooltip, int count) {
        filterButtons.add(new FilterKeywordButton(this, tooltip).setOnToggle(onClick).setOnRightClick(this::buttonRightClick).setCardCount(count));
        currentFilterCounts.merge(tooltip, count, Integer::sum);
    }

    public ArrayList<T> applyFilters(ArrayList<T> input) {
        return EUIUtils.filter(input, this::evaluate);
    }

    public boolean areFiltersEmpty() {
        return filters.isEmpty()
                && EUIUtils.all(getGlobalFilters(), CustomFilterModule::isEmpty)
                && (customModule != null && customModule.isEmpty());
    }

    public void buttonRightClick(FilterKeywordButton button) {
        selectedButton = button;
        hideKeyword(!EUIConfiguration.getIsTipDescriptionHidden(button.keywordTooltip.ID));
    }

    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        filters.clear(shouldClearColors);
        doForFilters(CustomFilterModule::reset);
        if (shouldInvoke && onClick != null) {
            onClick.invoke(null);
        }
    }

    public final void close() {
        closeButton.hb.hovered = false;
        closeButton.hb.clicked = false;
        closeButton.hb.justHovered = false;
        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.isPopupOpen = false;
        setActive(false);
    }

    @Override
    public int currentSize() {
        return filterButtons.size();
    }

    public void doForFilters(ActionT1<V> filterAction) {
        for (V module : getGlobalFilters()) {
            filterAction.invoke(module);
        }
        if (customModule != null) {
            filterAction.invoke(customModule);
        }
    }

    // Shorthand function to be fed to all dropdown filters
    protected <K> boolean evaluateItem(Collection<K> set, FuncT1<Boolean, K> evalFunc) {
        boolean passes = true;
        if (!set.isEmpty()) {
            passes = false;
            for (K opt : set) {
                if (evalFunc.invoke(opt)) {
                    passes = true;
                    break;
                }
            }
        }
        return passes;
    }

    protected <K> boolean evaluateItem(K[] set, FuncT1<Boolean, K> evalFunc) {
        boolean passes = true;
        if (set.length > 0) {
            passes = false;
            for (K opt : set) {
                if (evalFunc.invoke(opt)) {
                    passes = true;
                    break;
                }
            }
        }
        return passes;
    }

    protected <K> boolean evaluateItem(HashSet<K> set, K evalValue) {
        boolean passes = true;
        if (!set.isEmpty()) {
            return (set.contains(evalValue));
        }
        return passes;
    }

    // Shorthand function to be fed to all dropdown filters
    protected <K> String filterNameFunction(List<K> items, FuncT1<String, K> originalFunction) {
        if (items.size() == 0) {
            return EUIRM.strings.ui_any;
        }
        if (items.size() > 1) {
            return items.size() + " " + EUIRM.strings.ui_itemsSelected;
        }
        return StringUtils.join(EUIUtils.map(items, originalFunction), ", ");
    }

    public ArrayList<T> getOriginalGroup() {
        return originalGroup;
    }

    private void hideKeyword(boolean value) {
        if (selectedButton != null) {
            EUIConfiguration.hideTipDescription(selectedButton.keywordTooltip.ID, value, true);
            selectedButton.afterToggleRight(value);
        }
    }

    public final GenericFilters<T, U, V> initialize(ActionT1<FilterKeywordButton> onClick, ArrayList<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        return initialize(onClick, new ItemGroup<>(items), items, color, isAccessedFromCardPool);
    }

    public final GenericFilters<T, U, V> initialize(ActionT1<FilterKeywordButton> onClick, ItemGroup<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        return initialize(onClick, items, new ArrayList<>(items.group), color, isAccessedFromCardPool);
    }

    protected final GenericFilters<T, U, V> initialize(ActionT1<FilterKeywordButton> onClick, ItemGroup<T> items, ArrayList<T> original, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        clear(false, true);
        TEMPORARY_TIPS.clear();
        currentFilterCounts.clear();
        filterButtons.clear();
        scrollDelta = 0;
        currentTotal = 0;
        EUI.actingColor = color;
        EUIKeywordTooltip.updateTooltipIcons();

        this.onClick = onClick;
        this.group = items;
        this.originalGroup = original;

        initializeCounters(onClick, original, color, isAccessedFromCardPool);

        // Update instructions according to current settings
        keywordsInstructionLabel.setLabel(EUIUtils.format(EUIRM.strings.misc_keywordInstructions, InputActionSet.peek.getKeyString()));

        return this;
    }

    protected final void initializeCounters(ActionT1<FilterKeywordButton> onClick, ArrayList<T> original, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        clear(false, true);
        TEMPORARY_TIPS.clear();
        currentFilterCounts.clear();
        filterButtons.clear();
        currentTotal = 0;
        initializeImpl(onClick, original, color, isAccessedFromCardPool);

        if (customModule != null) {
            customModule.processGroup(group);
        }

        // InitializeImpl should set up the CurrentFilterCounts set
        for (Map.Entry<EUIKeywordTooltip, Integer> filter : currentFilterCounts.entrySet()) {
            int cardCount = filter.getValue();
            filterButtons.add(new FilterKeywordButton(this, filter.getKey()).setOnToggle(onClick).setOnRightClick(this::buttonRightClick).setCardCount(cardCount));
        }
        currentTotalLabel.setLabel(currentTotal);
    }

    public GenericFilters<T, U, V> initializeForSort(ItemGroup<T> group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color) {
        return initializeForSort(group, onClick, color, FILTERS_START_X, false, false);
    }

    public GenericFilters<T, U, V> initializeForSort(ItemGroup<T> group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color, float startX) {
        return initializeForSort(group, onClick, color, startX, false, false);
    }

    public GenericFilters<T, U, V> initializeForSort(ItemGroup<T> group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color, float startX, boolean isAccessedFromCardPool, boolean snapToGroup) {
        initialize(button -> {
            refreshGroup();
            onClick.invoke(button);
        }, group, color, isAccessedFromCardPool);
        refreshGroup();
        EUI.sortHeader.setFilters(this, startX).snapToGroup(snapToGroup);
        EUI.openFiltersButton.setOnClick(this::toggleFilters);
        EUIExporter.exportButton.setOnClick(() -> getExportable().openAndPosition(this.group.group));
        return this;
    }

    public void invoke(FilterKeywordButton button) {
        if (onClick != null) {
            onClick.invoke(button);
        }
    }

    protected float makeToggle(FilterSortHeader header, Comparator<T> comparator, String title, float x) {
        FilterSortToggle toggle = new FilterSortToggle(x, title, header, (val) -> setSort(comparator, val));
        header.buttons.add(toggle);
        return x + toggle.getSize();
    }

    protected float makeToggle(FilterSortHeader header, ActionT1<Boolean> sort, String title, float x) {
        FilterSortToggle toggle = new FilterSortToggle(x, title, header, sort);
        header.buttons.add(toggle);
        return x + toggle.getSize();
    }

    public final void manualInvalidate(ArrayList<T> items) {
        if (this.group != null) {
            this.group.group = items;
            invalidated = true;
        }
    }

    // Shorthand function to be fed to all dropdown filters
    protected <K> void onFilterChanged(Collection<K> set, List<K> items) {
        EUIUtils.replaceContents(set, items);
        if (onClick != null) {
            onClick.invoke(null);
        }
    }

    public final void open() {
        CardCrawlGame.isPopupOpen = true;
        setActive(true);
    }

    public final void refreshButtonOrder() {
        sortTypeToggle.setText(EUIRM.strings.sortBy(shouldSortByCount ? EUIRM.strings.ui_amount : CardLibSortHeader.TEXT[2]));
        filterButtons.sort((a, b) -> (shouldSortByCount ? a.cardCount - b.cardCount : StringUtils.compare(a.tooltip.title, b.tooltip.title)) * (sortDesc ? -1 : 1));

        int index = 0;
        for (FilterKeywordButton c : filterButtons) {
            if (c.isActive) {
                c.setIndex(index);
                index += 1;
            }
        }
    }

    public final void refreshButtons() {
        currentFilterCounts.clear();
        currentTotal = 0;

        if (group != null) {
            currentTotal = group.group.size();
            for (T card : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(card)) {
                    currentFilterCounts.merge(tooltip, 1, Integer::sum);
                }
            }
        }
        for (FilterKeywordButton c : filterButtons) {
            c.setCardCount(currentFilterCounts.getOrDefault(c.keywordTooltip, 0));
        }

        currentTotalLabel.setLabel(currentTotal);

        refreshButtonOrder();
    }

    public void refreshGroup() {
        if (this.group != null) {
            if (areFiltersEmpty()) {
                this.group.group = originalGroup;
            }
            else {
                this.group.group = applyFilters(originalGroup);
            }
            sort();
            invalidated = true;
        }
    }

    @Override
    public void refreshOffset() {
        sizeCache = currentSize();
        upperScrollBound = 0;
        lowerScrollBound = 0;

        if (sizeCache > THRESHOLD) {
            int offset = (sizeCache - THRESHOLD - 1) / rowSize;
            upperScrollBound += padY * (offset + 2);
            //lowerScrollBound -= padY * (offset - 1);
        }
    }

    @Override
    public final void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        sb.setColor(FADE_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        hb.render(sb);
        closeButton.tryRender(sb);
        clearButton.tryRender(sb);
        keywordsSectionLabel.renderImpl(sb);
        keywordsInstructionLabel.renderImpl(sb);
        currentTotalHeaderLabel.renderImpl(sb);
        currentTotalLabel.renderImpl(sb);
        sortTypeToggle.tryRender(sb);
        sortDirectionToggle.tryRender(sb);

        for (FilterKeywordButton c : filterButtons) {
            c.tryRender(sb);
        }

        renderFilters(sb);
    }

    public final void setSort(Comparator<T> comparator) {
        setSort(comparator, false);
    }

    public final void setSort(Comparator<T> comparator, boolean order) {
        this.comparator = comparator;
        isReverseOrder = order;
        sort();
    }

    public final void setSortDirection(boolean order) {
        isReverseOrder = order;
        sort();
    }

    public final void sort() {
        if (comparator != null) {
            this.group.sort(isReverseOrder ? comparator.reversed() : comparator);
        }
        else {
            defaultSort();
        }
    }

    public void toggleFilters() {
        if (isActive) {
            close();
        }
        else {
            open();
        }
    }

    @Override
    public final boolean tryUpdate() {
        super.tryUpdate();
        if (EUIHotkeys.toggleFilters.isJustPressed()) {
            toggleFilters();
        }
        return isActive;
    }

    // Shorthand function to be fed to all dropdown filters
    protected void updateActive(boolean whatever) {
        CardCrawlGame.isPopupOpen = this.isActive;
    }

    public final float updateDropdown(EUIHoverable element, float xPos) {
        element.setPosition(xPos, DRAW_START_Y + scrollDelta).tryUpdate();
        return element.hb.x + element.hb.width + SPACING * 2;
    }

    @Override
    public final void updateImpl() {
        super.updateImpl();
        hb.y = DRAW_START_Y + scrollDelta - SPACING * 10;
        keywordsSectionLabel.setPosition(hb.x - SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 7).updateImpl();
        keywordsInstructionLabel.setPosition(Settings.WIDTH * 0.172f + hb.x - SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 10f).updateImpl();
        sortTypeToggle.setPosition(keywordsSectionLabel.hb.x + SPACING * 11, DRAW_START_Y + scrollDelta - SPACING * 7).tryUpdate();
        sortDirectionToggle.setPosition(sortTypeToggle.hb.x + sortTypeToggle.hb.width + SPACING * 1.4f, DRAW_START_Y + scrollDelta - SPACING * 7).tryUpdate();
        currentTotalHeaderLabel.updateImpl();
        currentTotalLabel.updateImpl();
        hb.update();
        closeButton.tryUpdate();
        clearButton.tryUpdate();
        if (invalidated) {
            invalidated = false;
            refreshButtons();
        }

        if (!EUI.doesActiveElementExist()) {
            for (FilterKeywordButton c : filterButtons) {
                c.tryUpdate();
            }

            updateInput();
        }

        updateFilters();
    }

    private void updateInput() {
        if (InputHelper.justClickedLeft) {
            if (closeButton.hb.hovered
                    || clearButton.hb.hovered
                    || sortTypeToggle.hb.hovered
                    || sortDirectionToggle.hb.hovered
                    || isHoveredImpl()) {
                return;
            }
            for (FilterKeywordButton c : filterButtons) {
                if (c.backgroundButton.hb.hovered) {
                    return;
                }
            }
            close();
            InputHelper.justClickedLeft = false;
        }
        else if (InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            InputHelper.pressedEscape = false;
            close();
        }
    }

    abstract public void cloneFrom(U filters);

    abstract public void defaultSort();

    abstract public boolean evaluate(T item);

    abstract public ArrayList<V> getGlobalFilters();

    abstract public float getFirstY();

    abstract protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool);

    abstract public void updateFilters();

    abstract public List<EUIKeywordTooltip> getAllTooltips(T c);

    abstract public boolean isHoveredImpl();

    abstract public void renderFilters(SpriteBatch sb);

    abstract protected void setupSortHeader(FilterSortHeader header, float startX);

    abstract public EUIExporter.Exportable<T> getExportable();

    abstract protected U getFilterObject();

}
