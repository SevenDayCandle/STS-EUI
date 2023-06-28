package extendedui.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

import static extendedui.EUIGameUtils.scale;

public class CardPoolScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen CARD_POOL_SCREEN;

    public static CustomCardPoolModule customModule;

    protected final EUIToggle upgradeToggle;
    protected final EUIToggle colorlessToggle;
    protected final EUIButton swapRelicScreen;
    protected final EUIButton swapPotionScreen;
    protected final EUIContextMenu<DebugOption> contextMenu;
    private AbstractCard selected;
    public EUICardGrid cardGrid;

    public CardPoolScreen() {
        resetGrid();

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.8f)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(EUI::toggleViewUpgrades);

        colorlessToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.75f)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(EUIRM.strings.uipool_showColorless)
                .setOnToggle(val -> {
                    EUI.cardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.COLORLESS, val, true);
                    EUI.cardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.CURSE, val, true);
                });

        swapRelicScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.9f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewRelicPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.relicScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()));

        swapPotionScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewPotionPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.potionScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllPotions()));

        contextMenu = (EUIContextMenu<DebugOption>) new EUIContextMenu<DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    if (selected != null) {
                        for (DebugOption o : options) {
                            o.onSelect.invoke(this, selected);
                        }
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    // This method can be patched to add additional debug options
    public static ArrayList<DebugOption> getOptions(AbstractCard c) {
        return EUIUtils.arrayList(DebugOption.enlargeCard, DebugOption.addToHand, DebugOption.addToDeck, DebugOption.removeFromPool);
    }

    protected void addCopyToDeck(AbstractCard c) {
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f));
    }

    protected void addCopyToHand(AbstractCard c) {
        if (EUIGameUtils.inBattle()) {
            AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(c.makeStatEquivalentCopy(), Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f));
        }
    }

    @Override
    public void close() {
        super.close();
        for (CustomCardPoolModule module : EUI.globalCustomCardPoolModules) {
            module.onClose();
        }
        if (customModule != null) {
            customModule.onClose();
        }
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return CARD_POOL_SCREEN;
    }

    @Override
    public void update() {
        if (!EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            cardGrid.tryUpdate();
            upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();
            colorlessToggle.update();
            swapRelicScreen.updateImpl();
            swapPotionScreen.updateImpl();
            EUI.customHeader.update();
            EUI.openCardFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            EUI.countingPanel.tryUpdate();
            for (CustomCardPoolModule module : EUI.globalCustomCardPoolModules) {
                module.update();
            }
            if (customModule != null) {
                customModule.update();
            }
            // TODO tie this to the custom header to ensure that the source grid is always updated instantly
            if (EUI.customHeader.justSorted) {
                cardGrid.forceUpdateCardPositions();
                EUI.customHeader.justSorted = false;
            }
        }
        contextMenu.tryUpdate();
        EUIExporter.exportDropdown.tryUpdate();
    }

    @Override
    public void render(SpriteBatch sb) {
        cardGrid.tryRender(sb);
        EUI.customHeader.render(sb);
        upgradeToggle.renderImpl(sb);
        colorlessToggle.render(sb);
        swapRelicScreen.renderImpl(sb);
        swapPotionScreen.renderImpl(sb);
        EUI.countingPanel.tryRender(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openCardFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomCardPoolModule module : EUI.globalCustomCardPoolModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    protected void onRightClick(AbstractCard c) {
        if (EUIConfiguration.enableCardPoolDebug.get()) {
            selected = c;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.setItems(getOptions(c));
            contextMenu.openOrCloseMenu();
        }
        else {
            openPopup(c);
        }
    }

    protected void openPopup(AbstractCard c) {
        c.unhover();
        CardCrawlGame.cardPopup.open(c, cardGrid.cards);
    }

    public void openScreen(AbstractPlayer player, CardGroup cards) {
        super.reopen();
        boolean canSeeAllColors = EUIGameUtils.canReceiveAnyColorCard();
        AbstractCard.CardColor color = player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS;

        cardGrid.clear();
        colorlessToggle.setToggle(false).setActive(!canSeeAllColors);
        if (cards.isEmpty()) {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        cardGrid.setCardGroup(cards);

        EUI.cardFilters.initializeForCustomHeader(cards, __ -> {
            for (CustomCardPoolModule module : EUI.globalCustomCardPoolModules) {
                module.open(EUI.customHeader.group.group, color, null);
            }
            if (customModule != null) {
                customModule.open(EUI.customHeader.group.group, color, null);
            }
            cardGrid.forceUpdateCardPositions();
        }, color, !canSeeAllColors, true);

        EUI.countingPanel.open(cardGrid.cards.group, color, null);

        for (CustomCardPoolModule module : EUI.globalCustomCardPoolModules) {
            module.open(cardGrid.cards.group, color, null);
        }

        customModule = EUI.getCustomCardPoolModule(player);
        if (customModule != null) {
            customModule.open(cardGrid.cards.group, color, null);
        }

    }

    protected void removeCardFromPool(AbstractCard c) {
        for (CardGroup group : EUIGameUtils.getGameCardPools()) {
            group.removeCard(c.cardID);
        }
        for (CardGroup group : EUIGameUtils.getSourceCardPools()) {
            group.removeCard(c.cardID);
        }
        cardGrid.removeCard(c);
        EUI.countingPanel.open(cardGrid.cards.group, EUI.actingColor, null);
    }

    public void resetGrid() {
        cardGrid = EUIConfiguration.useSnapScrolling.get() ? new EUIStaticCardGrid() : new EUICardGrid();
        cardGrid.showScrollbar(true)
                .canRenderUpgrades(true)
                .setOnCardRightClick(this::onRightClick)
                .setVerticalStart(Settings.HEIGHT * 0.66f);
    }

    public static class DebugOption {
        public static DebugOption enlargeCard = new DebugOption(EUIRM.strings.uipool_enlarge, CardPoolScreen::openPopup);
        public static DebugOption addToHand = new DebugOption(EUIRM.strings.uipool_addToHand, CardPoolScreen::addCopyToHand);
        public static DebugOption addToDeck = new DebugOption(EUIRM.strings.uipool_addToDeck, CardPoolScreen::addCopyToDeck);
        public static DebugOption removeFromPool = new DebugOption(EUIRM.strings.uipool_removeFromPool, CardPoolScreen::removeCardFromPool);

        public final String name;
        public final ActionT2<CardPoolScreen, AbstractCard> onSelect;

        public DebugOption(String name, ActionT2<CardPoolScreen, AbstractCard> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}