package extendedui.ui.cardFilter;

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
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.interfaces.markers.CustomPotionFilterModule;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;

import java.util.Comparator;
import java.util.HashMap;

import static extendedui.ui.cardFilter.CustomCardLibSortHeader.CENTER_Y;

public class CustomCardLibraryScreen extends AbstractMenuScreen {
    public static final int VISIBLE_BUTTONS = 14;
    public static final HashMap<AbstractCard.CardColor, CardGroup> CardLists = new HashMap<>();
    protected static final float ICON_SIZE = scale(40);
    public static AbstractCard.CardColor currentColor = AbstractCard.CardColor.COLORLESS;
    public static CustomCardPoolModule customModule;
    public final EUITextBoxInput quickSearch;
    public final EUIToggle upgradeToggle;
    public final MenuCancelButton cancelButton;
    protected final EUIButtonList colorButtons = new EUIButtonList();
    public EUICardGrid cardGrid;
    protected int topButtonIndex;
    protected Rectangle scissors;

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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.7f, Settings.GOLD_COLOR, EUIRM.strings.uiNamesearch)
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

    public void resetGrid() {
        cardGrid = EUIConfiguration.useSnapScrolling.get() ? new EUIStaticCardGrid() : new EUICardGrid();
        cardGrid.showScrollbar(true)
                .canRenderUpgrades(true)
                .setVerticalStart(Settings.HEIGHT * 0.65f)
                .setCardScale(0.6f, 0.75f)
                .setOnCardRightClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.cards);
                });
    }

    protected void toggleUpgrades(boolean value) {
        EUI.toggleViewUpgrades(value);
        upgradeToggle.setToggle(value);
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

    public void setActiveColor(AbstractCard.CardColor color) {
        setActiveColor(color, CardLists.getOrDefault(color, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED)), null);
    }

    public void setActiveColor(AbstractCard.CardColor color, CardGroup cards, Object payload) {
        EUI.actingColor = currentColor = color;
        cardGrid.clear();
        cardGrid.setCardGroup(cards);

        EUI.cardFilters.initializeForCustomHeader(cards, __ -> {
            quickSearch.setLabel(EUI.cardFilters.currentName != null ? EUI.cardFilters.currentName : "");
            for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
                module.open(EUI.customHeader.group.group, color, payload);
            }
            if (customModule != null) {
                customModule.open(EUI.customHeader.group.group, color, payload);
            }
            cardGrid.moveToTop();
            cardGrid.forceUpdateCardPositions();
        }, color, false, true);

        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.open(cardGrid.cards.group, color, payload);
        }
        customModule = EUI.getCustomCardLibraryModule(color);
        if (customModule != null) {
            customModule.open(cardGrid.cards.group, color, payload);
        }

        EUI.customHeader.resetSort();
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
    public void updateImpl() {
        super.updateImpl();
        boolean shouldDoStandardUpdate = !EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            EUI.openCardFiltersButton.tryUpdate();
            colorButtons.tryUpdate();
            EUI.customHeader.update();
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
            if (EUI.customHeader.justSorted) {
                cardGrid.forceUpdateCardPositions();
                EUI.customHeader.justSorted = false;
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

        EUI.customHeader.render(sb);
        quickSearch.tryRender(sb);

        for (CustomCardPoolModule module : EUI.globalCustomCardLibraryModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        if (!EUI.cardFilters.isActive) {
            EUI.openCardFiltersButton.tryRender(sb);
        }
        cancelButton.render(sb);
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
}
