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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;

import java.util.Comparator;
import java.util.HashMap;

public class CustomCardLibraryScreen extends AbstractMenuScreen {
    private static final float FILTERS_START_X = (float) Settings.WIDTH * 0.177f;
    private static final int VISIBLE_BUTTONS = 14;
    public static final HashMap<AbstractCard.CardColor, CardGroup> CardLists = new HashMap<>();
    public static final float CENTER_Y = Settings.HEIGHT * 0.88f;
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static CustomCardPoolModule customModule;
    protected final EUIButtonList colorButtons = new EUIButtonList();
    public final EUITextBoxInput quickSearch;
    public final EUIToggle upgradeToggle;
    public final MenuCancelButton cancelButton;
    private final Rectangle scissors;
    public final CardKeywordFilters.CardFilters savedFilters = new CardKeywordFilters.CardFilters();
    public EUICardGrid cardGrid;

    public CustomCardLibraryScreen() {
        final float y = Settings.HEIGHT * 0.92f - (VISIBLE_BUTTONS + 1) * scale(48);

        resetGrid();
        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(1450.0F * Settings.xScale, Settings.HEIGHT * 0.8f)
                .setFont(EUIFontHelper.cardTooltipTitleFontLarge, 1f)
                .setText(CardLibraryScreen.TEXT[7])
                .setOnToggle(this::toggleUpgrades);
        cancelButton = new MenuCancelButton();

        quickSearch = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(Settings.WIDTH * 0.42f, Settings.HEIGHT * 0.92f, scale(280), scale(48)))
                .setOnComplete((v) -> EUI.cardFilters.nameInput.setTextAndCommit(v))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.7f, Settings.GOLD_COLOR, EUIRM.strings.ui_nameSearch)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.7f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture())
                .setLabel("");
        quickSearch.header.setAlignment(0f, -0.51f);

        scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(0, 0, Settings.WIDTH, CENTER_Y);
        ScissorStack.calculateScissors(EUIGameUtils.getCamera(), EUIGameUtils.getSpriteBatch().getTransformMatrix(), clipBounds, scissors);
    }

    @Override
    public void close() {
        super.close();
        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.onClose();
        }
        if (customModule != null) {
            customModule.onClose();
        }
    }

    public void initialize(CardLibraryScreen screen) {
        // CardLibraryScreen needs to be re-initialized whenever the save slot changes
        CardLists.clear();
        colorButtons.clear();

        // Let's just re-use the hard sorting work that basemod and the base game has done for us :)
        CardLists.put(AbstractCard.CardColor.RED, EUIClassUtils.getField(screen, "redCards"));
        CardLists.put(AbstractCard.CardColor.GREEN, EUIClassUtils.getField(screen, "greenCards"));
        CardLists.put(AbstractCard.CardColor.BLUE, EUIClassUtils.getField(screen, "blueCards"));
        CardLists.put(AbstractCard.CardColor.PURPLE, EUIClassUtils.getField(screen, "purpleCards"));
        CardLists.put(AbstractCard.CardColor.CURSE, EUIClassUtils.getField(screen, "curseCards"));
        CardLists.put(AbstractCard.CardColor.COLORLESS, EUIClassUtils.getField(screen, "colorlessCards"));
        CardLists.putAll(EverythingFix.Fields.cardGroupMap);

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

    protected void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> setActiveColor(co), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

    public void open() {
        super.open();
        openImpl();
    }

    // Also called by the card filter component
    public void openImpl() {
        refreshGroups();
        EUI.toggleViewUpgrades(false);
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
        savedFilters.clear(true);
        setActiveColor(currentColor);
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);
    }

    protected void refreshGroups() {
        for (CardGroup group : CardLists.values()) {
            for (AbstractCard c : group.group) {
                if (UnlockTracker.isCardLocked(c.cardID)) {
                    c.setLocked();
                }
                else if (c.isLocked) {
                    c.unlock();
                }
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        colorButtons.tryRender(sb);
        cardGrid.renderWithScissors(sb, scissors);
        sb.setColor(EUIGameUtils.getColorColor(currentColor));
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float) Settings.WIDTH / 2.0F - 667.0F, CENTER_Y - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        upgradeToggle.renderImpl(sb);
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
        cardGrid = new EUICardGrid();
        cardGrid
                .setCanRenderUpgrades(true)
                .setVerticalStart(Settings.HEIGHT * 0.7f)
                .setOnRightClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.makeCardGroup());
                })
                .showScrollbar(true);
    }

    public void setActiveColor(AbstractCard.CardColor color, CardGroup cards, Object payload) {
        if (EUIConfiguration.saveFilterChoices.get()) {
            savedFilters.cloneFrom(EUI.cardFilters.filters);
        }

        EUI.actingColor = currentColor = color;
        cardGrid.clear();
        cardGrid.setCardGroup(cards);

        EUI.cardFilters.initializeForSort(cardGrid.group, __ -> {
            quickSearch.setLabel(EUI.cardFilters.filters.currentName != null ? EUI.cardFilters.filters.currentName : "");
            for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
                module.open(EUI.cardFilters.group.group, color, payload);
            }
            if (customModule != null) {
                customModule.open(EUI.cardFilters.group.group, color, payload);
            }
            cardGrid.moveToTop();
            cardGrid.forceUpdatePositions();
        }, color, FILTERS_START_X);

        if (EUIConfiguration.saveFilterChoices.get()) {
            EUI.cardFilters.setFrom(savedFilters);
        }

        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.open(cardGrid.group.group, color, payload);
        }
        customModule = EUI.getCustomCardLibraryModule(color);
        if (customModule != null) {
            customModule.open(cardGrid.group.group, color, payload);
        }

        //EUI.sortHeader.resetSort();
    }

    public void setActiveColor(AbstractCard.CardColor color) {
        setActiveColor(color, CardLists.getOrDefault(color, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED)), null);
    }

    protected void toggleUpgrades(boolean value) {
        EUI.toggleViewUpgrades(value);
        upgradeToggle.setToggle(value);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            colorButtons.tryUpdate();
            EUI.sortHeader.update();
            upgradeToggle.setPosition(upgradeToggle.hb.cX, CENTER_Y).updateImpl();
            quickSearch.tryUpdate();
            cardGrid.tryUpdate();
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
