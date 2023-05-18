package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;

public abstract class GenericFilters<T> extends EUICanvasGrid {
    public static final float DRAW_START_X = (float) Settings.WIDTH * 0.15f;
    public static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.87f;
    public static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    public static final float PAD_Y = scale(45);
    public static final float SPACING = Settings.scale * 22.5f;
    public static final int ROW_SIZE = 8;
    protected static final Color FADE_COLOR = new Color(0f, 0f, 0f, 0.84f);
    public final EUIButton closeButton;
    public final EUIButton clearButton;
    public final EUIContextMenu<TooltipOption> contextMenu;
    public final EUILabel currentTotalHeaderLabel;
    public final EUILabel currentTotalLabel;
    public final EUILabel keywordsSectionLabel;
    public final EUITextBox keywordsInstructionLabel;
    public final EUIToggle sortTypeToggle;
    public final EUIToggle sortDirectionToggle;
    public final HashSet<EUIKeywordTooltip> currentFilters = new HashSet<>();
    public final HashSet<EUIKeywordTooltip> currentNegateFilters = new HashSet<>();
    protected final HashMap<EUIKeywordTooltip, Integer> currentFilterCounts = new HashMap<>();
    protected final ArrayList<FilterKeywordButton> filterButtons = new ArrayList<>();
    protected final EUIHitbox hb;
    protected int currentTotal;
    protected ActionT1<FilterKeywordButton> onClick;
    protected ArrayList<T> referenceItems;
    protected float drawX;
    protected boolean invalidated;
    protected boolean isAccessedFromCardPool;
    private FilterKeywordButton selectedButton;
    private boolean shouldSortByCount;
    private boolean sortDesc;

    public GenericFilters() {
        super(ROW_SIZE, PAD_Y);
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
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.13f)
                .setText(EUIRM.strings.misc_clear)
                .setOnClick(() -> this.clear(true, isAccessedFromCardPool));

        contextMenu = (EUIContextMenu<TooltipOption>) new EUIContextMenu<TooltipOption>(new EUIHitbox(0, 0, 0, 0), t -> t.baseName)
                .setOnChange(options -> {
                    for (TooltipOption o : options) {
                        o.onAct.invoke(this);
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setItems(TooltipOption.values())
                .setCanAutosizeButton(true);

        keywordsSectionLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(0, 0, scale(48), scale(48)), 0.8f)
                .setLabel(EUIRM.strings.uiKeywords)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.0f, false);
        keywordsInstructionLabel = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(0, 0, Settings.WIDTH * 0.48f, scale(64)), EUIFontHelper.cardTooltipFont, 0.85f)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setLabel(EUIRM.strings.misc_keywordInstructions)
                .setAlignment(0.75f, 0.05f, true);
        currentTotalHeaderLabel = new EUILabel(EUIFontHelper.cardTitleFontNormal,
                new EUIHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.94f, scale(48), scale(48)), 1f)
                .setLabel(EUIRM.strings.uiTotal)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.0f, false);
        currentTotalLabel = new EUILabel(EUIFontHelper.cardTitleFontNormal,
                new EUIHitbox(Settings.WIDTH * 0.01f, Settings.HEIGHT * 0.906f, scale(48), scale(48)), 1f)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setAlignment(0.5f, 0.0f, false);

        sortTypeToggle = new EUIToggle(new EUIHitbox(0, 0, scale(170), scale(32)).setIsPopupCompatible(true))
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

    public final void close() {
        closeButton.hb.hovered = false;
        closeButton.hb.clicked = false;
        closeButton.hb.justHovered = false;
        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.isPopupOpen = false;
        setActive(false);
    }

    public final void clear(boolean shouldInvoke, boolean shouldClearColors) {
        clearFilters(shouldInvoke, shouldClearColors);
        if (shouldInvoke && onClick != null) {
            onClick.invoke(null);
        }
    }

    public final void refreshButtonOrder() {
        sortTypeToggle.setText(EUIRM.strings.sortBy(shouldSortByCount ? EUIRM.strings.uiAmount : CardLibSortHeader.TEXT[2]));
        filterButtons.sort((a, b) -> (shouldSortByCount ? a.cardCount - b.cardCount : StringUtils.compare(a.tooltip.title, b.tooltip.title)) * (sortDesc ? -1 : 1));

        int index = 0;
        for (FilterKeywordButton c : filterButtons) {
            if (c.isActive) {
                c.setIndex(index);
                index += 1;
            }
        }
    }

    abstract public void clearFilters(boolean shouldInvoke, boolean shouldClearColors);

    public void addManualKeyword(EUIKeywordTooltip tooltip, int count) {
        filterButtons.add(new FilterKeywordButton(this, tooltip).setOnToggle(onClick).setOnRightClick(this::buttonRightClick).setCardCount(count));
        currentFilterCounts.merge(tooltip, count, Integer::sum);
    }

    abstract public ArrayList<T> applyFilters(ArrayList<T> input);

    abstract public boolean areFiltersEmpty();

    @Override
    public int currentSize() {
        return filterButtons.size();
    }

    // Shorthand function to be fed to all dropdown filters
    protected <K> boolean evaluateItem(HashSet<K> set, FuncT1<Boolean, K> evalFunc) {
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

    // Shorthand function to be fed to all dropdown filters
    protected <K> String filterNameFunction(List<K> items, FuncT1<String, K> originalFunction) {
        if (items.size() == 0) {
            return EUIRM.strings.uiAny;
        }
        if (items.size() > 1) {
            return items.size() + " " + EUIRM.strings.uiItemsselected;
        }
        return StringUtils.join(EUIUtils.map(items, originalFunction), ", ");
    }

    private void hideKeyword(boolean value) {
        if (selectedButton != null) {
            EUIConfiguration.hideTipDescription(selectedButton.tooltip.ID, value, true);
            selectedButton.afterToggleRight();
        }
    }

    public final GenericFilters<T> initialize(ActionT1<FilterKeywordButton> onClick, ArrayList<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        clear(false, true);
        currentFilterCounts.clear();
        filterButtons.clear();
        currentTotal = 0;

        EUI.actingColor = color;
        EUIKeywordTooltip.updateTooltipIcons();
        this.onClick = onClick;
        referenceItems = items;

        initializeImpl(onClick, items, color, isAccessedFromCardPool);

        // InitializeImpl should set up the CurrentFilterCounts set
        for (Map.Entry<EUIKeywordTooltip, Integer> filter : currentFilterCounts.entrySet()) {
            int cardCount = filter.getValue();
            filterButtons.add(new FilterKeywordButton(this, filter.getKey()).setOnToggle(onClick).setOnRightClick(this::buttonRightClick).setCardCount(cardCount));
        }
        currentTotalLabel.setLabel(currentTotal);

        // Update instructions according to current settings
        keywordsInstructionLabel.setLabel(EUIUtils.format(EUIRM.strings.misc_keywordInstructions, InputActionSet.peek.getKeyString()));

        return this;
    }

    abstract protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<T> items, AbstractCard.CardColor color, boolean isAccessedFromCardPool);

    public void buttonRightClick(FilterKeywordButton button) {
        selectedButton = button;
        contextMenu.setItems(EUIConfiguration.getIsTipDescriptionHidden(button.tooltip.ID) ? TooltipOption.EnableTooltip : TooltipOption.DisableTooltip);
        float actualMX;
        float actualMY;
        if (CardCrawlGame.isPopupOpen) {
            actualMX = popupMX;
            actualMY = popupMY;
        }
        else {
            actualMX = InputHelper.mX;
            actualMY = InputHelper.mY;
        }
        contextMenu.setPosition(actualMX > Settings.WIDTH * 0.75f ? actualMX - contextMenu.hb.width : actualMX, actualMY);
        contextMenu.refreshText();
        contextMenu.openOrCloseMenu();
    }

    public void invoke(FilterKeywordButton button) {
        if (onClick != null) {
            onClick.invoke(button);
        }
    }

    // Shorthand function to be fed to all dropdown filters
    protected <K> void onFilterChanged(HashSet<K> set, List<K> items) {
        set.clear();
        set.addAll(items);
        if (onClick != null) {
            onClick.invoke(null);
        }
    }

    public final void open() {
        CardCrawlGame.isPopupOpen = true;
        setActive(true);
    }

    public final void refresh(ArrayList<T> items) {
        referenceItems = items;
        invalidated = true;
    }

    @Override
    public final boolean tryUpdate() {
        super.tryUpdate();
        if (EUIHotkeys.toggleFilters.isJustPressed()) {
            toggleFilters();
        }
        return isActive;
    }

    abstract public void toggleFilters();

    // Shorthand function to be fed to all dropdown filters
    protected void updateActive(boolean whatever) {
        CardCrawlGame.isPopupOpen = this.isActive;
    }

    @Override
    public final void updateImpl() {
        super.updateImpl();
        hb.y = DRAW_START_Y + scrollDelta - SPACING * 10;
        keywordsSectionLabel.setPosition(hb.x - SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 7).updateImpl();
        keywordsInstructionLabel.setPosition(Settings.WIDTH * 0.21f + hb.x - SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 9.1f).updateImpl();
        sortTypeToggle.setPosition(keywordsSectionLabel.hb.x + SPACING * 10, DRAW_START_Y + scrollDelta - SPACING * 7).tryUpdate();
        sortDirectionToggle.setPosition(sortTypeToggle.hb.x + SPACING * 7, DRAW_START_Y + scrollDelta - SPACING * 7).tryUpdate();
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
        contextMenu.tryUpdate();
    }

    public final void refreshButtons() {
        currentFilterCounts.clear();
        currentTotal = 0;

        if (referenceItems != null) {
            currentTotal = getReferenceCount();
            for (T card : referenceItems) {
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

    abstract public void updateFilters();

    public int getReferenceCount() {
        return referenceItems.size();
    }

    abstract public List<EUIKeywordTooltip> getAllTooltips(T c);

    abstract public boolean isHoveredImpl();

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
        contextMenu.tryRender(sb);
    }

    abstract public void renderFilters(SpriteBatch sb);

    public enum TooltipOption {
        DisableTooltip(EUIRM.strings.uiDisableTooltip, filters -> filters.hideKeyword(true)),
        EnableTooltip(EUIRM.strings.uiEnableTooltip, filters -> filters.hideKeyword(false));

        public final String baseName;
        public final ActionT1<GenericFilters<?>> onAct;

        TooltipOption(String name, ActionT1<GenericFilters<?>> onAct) {
            this.baseName = name;
            this.onAct = onAct;
        }
    }
}
