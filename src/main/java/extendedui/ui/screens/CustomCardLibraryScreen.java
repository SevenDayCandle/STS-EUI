package extendedui.ui.screens;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;

import java.util.*;

public class CustomCardLibraryScreen extends AbstractMenuScreen {
    private static final float FILTERS_START_X = (float) Settings.WIDTH * 0.177f;
    private static final int VISIBLE_BUTTONS = 14;
    private static final HashMap<AbstractCard.CardColor, ArrayList<AbstractCard>> CARD_LISTS = new HashMap<>();
    private static final float TOP_Y = Settings.HEIGHT * 0.88f;
    private static final float BAR_X = (float) Settings.WIDTH / 2.0F - 667.0F;
    private static final float BAR_Y = TOP_Y - 51;
    private static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    private static CustomCardPoolModule customModule;
    private static boolean isAll;
    private final EUIButtonList colorButtons;
    private final EUIButton allButton;
    private final Rectangle scissors;
    private final CardKeywordFilters.CardFilters savedFilters = new CardKeywordFilters.CardFilters();
    public final EUITextBoxInput quickSearch;
    public final EUIToggle upgradeToggle;
    public final EUIToggle betaToggle;
    public final MenuCancelButton cancelButton;
    public EUICardGrid cardGrid;
    private boolean betaToggleCache;

    public CustomCardLibraryScreen() {
        final float y = Settings.HEIGHT * 0.92f - (VISIBLE_BUTTONS + 1) * scale(48);

        resetGrid();
        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 180f, Settings.scale * 48f))
                .setPosition(1400.0F * Settings.xScale, Settings.HEIGHT * 0.88f)
                .setFont(EUIFontHelper.tooltipFont, 1f)
                .setText(CardLibraryScreen.TEXT[7])
                .setOnToggle(EUI::toggleViewUpgrades);
        betaToggle = new EUIToggle(new EUIHitbox(Settings.scale * 180f, Settings.scale * 48f))
                .setPosition(1600.0F * Settings.xScale, Settings.HEIGHT * 0.88f)
                .setFont(EUIFontHelper.tooltipFont, 1f)
                .setText(SingleCardViewPopup.TEXT[14])
                .setOnToggle(val -> {
                    EUI.toggleBetaArt(val);
                    betaToggleCache = val;
                });

        cancelButton = new MenuCancelButton();
        colorButtons = new EUIButtonList(EUIButtonList.DEFAULT_VISIBLE - 1, EUIButtonList.STARTING_X, Settings.HEIGHT * 0.9f, EUIButtonList.BUTTON_W, EUIButtonList.BUTTON_H);

        allButton = new EUIButton(ImageMaster.COLOR_TAB_BAR, new EUIHitbox(colorButtons.buttonWidth * 0.8f, colorButtons.buttonHeight))
                .setPosition(EUIButtonList.STARTING_X, Settings.HEIGHT * 0.95f)
                .setLabel(FontHelper.buttonLabelFont, 0.7f, EUIRM.strings.target_allCharacter)
                .setOnClick((ActionT0) this::setToAll);

        quickSearch = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(Settings.WIDTH * 0.42f, Settings.HEIGHT * 0.92f, scale(280), scale(48)))
                .setOnComplete((v) -> EUI.cardFilters.nameInput.setTextAndCommit(v))
                .setHeader(FontHelper.topPanelAmountFont, 0.7f, Settings.GOLD_COLOR, EUIRM.strings.ui_nameSearch)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(FontHelper.topPanelAmountFont, 0.7f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture())
                .setLabel("");
        quickSearch.header.setAlignment(0f, -0.51f);

        scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(0, 0, Settings.WIDTH, TOP_Y);
        ScissorStack.calculateScissors(EUIGameUtils.getCamera(), EUIGameUtils.getSpriteBatch().getTransformMatrix(), clipBounds, scissors);
    }

    public static Collection<ArrayList<AbstractCard>> getAllCardLists() {
        return CARD_LISTS.values();
    }

    public static Collection<AbstractCard.CardColor> getAllKeys() {
        return CARD_LISTS.keySet();
    }

    public static ArrayList<AbstractCard> getCards(AbstractCard.CardColor color) {
        return CARD_LISTS.get(color);
    }

    public static AbstractCard.CardColor getCurrentColor() {
        return currentColor;
    }

    public static boolean isAll() {
        return isAll;
    }

    @Override
    public void close() {
        super.close();
        betaToggleCache = betaToggle.toggled;
        EUI.toggleBetaArtReset();
        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.onClose();
        }
        if (customModule != null) {
            customModule.onClose();
        }
    }

    public void initialize(CardLibraryScreen screen) {
        // CardLibraryScreen needs to be re-initialized whenever the save slot changes
        CARD_LISTS.clear();
        colorButtons.clear();

        // Let's just re-use the hard sorting work that basemod and the base game has done for us :)
        CARD_LISTS.put(AbstractCard.CardColor.RED, ((CardGroup)EUIClassUtils.getField(screen, "redCards")).group);
        CARD_LISTS.put(AbstractCard.CardColor.GREEN, ((CardGroup)EUIClassUtils.getField(screen, "greenCards")).group);
        CARD_LISTS.put(AbstractCard.CardColor.BLUE, ((CardGroup)EUIClassUtils.getField(screen, "blueCards")).group);
        CARD_LISTS.put(AbstractCard.CardColor.PURPLE, ((CardGroup)EUIClassUtils.getField(screen, "purpleCards")).group);
        CARD_LISTS.put(AbstractCard.CardColor.CURSE, ((CardGroup)EUIClassUtils.getField(screen, "curseCards")).group);
        CARD_LISTS.put(AbstractCard.CardColor.COLORLESS, ((CardGroup)EUIClassUtils.getField(screen, "colorlessCards")).group);
        for (AbstractCard.CardColor co : EverythingFix.Fields.cardGroupMap.keySet()) {
            CARD_LISTS.put(co, EverythingFix.Fields.cardGroupMap.get(co).group);
        }

        // Add custom buttons. Base game colors come first.
        makeColorButton(AbstractCard.CardColor.COLORLESS);
        makeColorButton(AbstractCard.CardColor.CURSE);
        makeColorButton(AbstractCard.CardColor.RED);
        makeColorButton(AbstractCard.CardColor.GREEN);
        makeColorButton(AbstractCard.CardColor.BLUE);
        makeColorButton(AbstractCard.CardColor.PURPLE);

        // Mod colors are sorted alphabetically
        BaseMod.getCardColors().stream().sorted(Comparator.comparing(EUIGameUtils::getColorName)).forEach(this::makeColorButton);
    }

    // Only add the card button if this card color actually has cards
    protected void makeColorButton(AbstractCard.CardColor co) {
        ArrayList<AbstractCard> g = CARD_LISTS.get(co);
        if (g != null && !g.isEmpty()) {
            colorButtons.addButton(button -> setActiveColor(co), EUIGameUtils.getColorName(co))
                    .setColor(EUIGameUtils.getColorColor(co));
        }
    }

    public void open() {
        super.open();
        openImpl();
    }

    // Also called by the card filter component
    public void openImpl() {
        refreshGroups();
        savedFilters.clear(true);
        if (isAll) {
            setToAll();
        }
        else {
            setActiveColor(currentColor);
        }
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);
        betaToggle.toggleForce(betaToggleCache);
        upgradeToggle.toggleForce(SingleCardViewPopup.isViewingUpgrade);
    }

    public void refreshGroups() {
        for (ArrayList<AbstractCard> group : CARD_LISTS.values()) {
            for (AbstractCard c : group) {
                if (UnlockTracker.isCardLocked(c.cardID)) {
                    c.setLocked();
                }
                else if (c.isLocked) {
                    c.unlock();
                }
                c.isSeen = UnlockTracker.isCardSeen(c.cardID);
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        allButton.tryRender(sb);
        colorButtons.tryRender(sb);
        cardGrid.renderWithScissors(sb, scissors);
        sb.setColor(EUIGameUtils.getColorColor(currentColor));
        sb.draw(ImageMaster.COLOR_TAB_BAR, BAR_X, BAR_Y, 667.0F, 51.0F, 1400.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        upgradeToggle.renderImpl(sb);
        betaToggle.renderImpl(sb);
        cancelButton.render(sb);

        EUI.sortHeader.render(sb);
        quickSearch.tryRender(sb);

        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        if (!EUI.cardFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
    }

    public void resetGrid() {
        cardGrid = new EUICardGrid(0.38f);
        cardGrid
                .setCanRenderUpgrades(true)
                .setVerticalStart(Settings.HEIGHT * 0.7f)
                .setOnClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.makeCardGroup());
                })
                .setOnRightClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.makeCardGroup());
                })
                .showScrollbar(true);
    }

    public void setToAll() {
        setToAll(null);
    }

    public void setToAll(Object payload) {
        HashSet<AbstractCard> uniqueCards = new HashSet<>();
        for (ArrayList<AbstractCard> c : CARD_LISTS.values()) {
            uniqueCards.addAll(c);
        }
        setActiveColor(AbstractCard.CardColor.COLORLESS, uniqueCards, true, payload);
    }

    public void setActiveColor(AbstractCard.CardColor color, Collection<? extends AbstractCard> cards, boolean isAllNew, Object payload) {
        if (EUIConfiguration.saveFilterChoices.get()) {
            savedFilters.cloneFrom(EUI.cardFilters.filters);
        }

        EUI.actingColor = currentColor = color;
        isAll = isAllNew;
        cardGrid.clear();
        cardGrid.setItems(cards);

        EUI.cardFilters.initializeForSort(cardGrid.group, __ -> {
            quickSearch.setLabel(EUI.cardFilters.filters.currentName != null ? EUI.cardFilters.filters.currentName : "");
            for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
                module.open(EUI.cardFilters.group.group, color, isAll, payload);
            }
            if (customModule != null) {
                customModule.open(EUI.cardFilters.group.group, color, isAll, payload);
            }
            cardGrid.moveToTop();
            cardGrid.forceUpdatePositions();
        }, color, FILTERS_START_X);

        if (EUIConfiguration.saveFilterChoices.get()) {
            EUI.cardFilters.cloneFrom(savedFilters);
        }

        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.open(cardGrid.group.group, color, isAll, payload);
        }
        customModule = EUI.getCustomCardLibraryModule(color);
        if (customModule != null) {
            customModule.open(cardGrid.group.group, color, isAll, payload);
        }

        if (isAll) {
            allButton.label.setColor(Settings.GREEN_TEXT_COLOR);
            colorButtons.selectButton(null);
        }
        else {
            allButton.label.setColor(Color.WHITE);
            colorButtons.selectButton(EUIUtils.find(colorButtons.buttons, b -> b.label.text.equals(EUIGameUtils.getColorName(currentColor))));
        }
    }

    public void setActiveColor(AbstractCard.CardColor color) {
        setActiveColor(color, CARD_LISTS.getOrDefault(color, new ArrayList<>()), false, null);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        boolean wasFiltersOpen = EUI.cardFilters.isActive;
        EUI.cardFilters.tryUpdate();
        boolean shouldDoStandardUpdate = !wasFiltersOpen && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            allButton.tryUpdate();
            colorButtons.tryUpdate();
            EUI.sortHeader.update();
            upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();
            betaToggle.setToggle(Settings.PLAYTESTER_ART_MODE).updateImpl();
            quickSearch.updateImpl();
            cardGrid.updateImpl(InputHelper.mY < BAR_Y);
            if (cardGrid.hovered != null) {
                CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
            }
            cancelButton.update();
            if (this.cancelButton.hb.clicked) {
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                close();
            }
            for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
                module.update();
            }
            if (customModule != null) {
                customModule.update();
            }
        }
        EUIExporter.exportDropdown.tryUpdate();
    }
}
