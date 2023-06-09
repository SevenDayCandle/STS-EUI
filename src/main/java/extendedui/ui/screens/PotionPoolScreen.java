package extendedui.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.interfaces.markers.CustomPotionPoolModule;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

import static extendedui.EUIGameUtils.scale;

public class PotionPoolScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen POTION_POOL_SCREEN;

    public static CustomPotionPoolModule customModule;
    protected final EUIContextMenu<PotionPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapRelicScreen;
    public EUIPotionGrid potionGrid;
    private AbstractPotion selected;

    public PotionPoolScreen() {
        potionGrid = (EUIPotionGrid) new EUIPotionGrid()
                .setOnPotionRightClick(this::onRightClick)
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);

        swapCardScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.9f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewCardPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.cardsScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllCards()));

        swapRelicScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewRelicPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.relicScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()));

        contextMenu = (EUIContextMenu<PotionPoolScreen.DebugOption>) new EUIContextMenu<PotionPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (PotionPoolScreen.DebugOption o : options) {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    @Override
    public void close() {
        super.close();
        for (CustomPotionPoolModule module : EUI.globalCustomPotionPoolModules) {
            module.onClose();
        }
        if (customModule != null) {
            customModule.onClose();
        }
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return POTION_POOL_SCREEN;
    }

    protected void onRightClick(AbstractPotion c) {
        if (EUIConfiguration.enableCardPoolDebug.get()) {
            selected = c;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.setItems(getOptions(c));
            contextMenu.openOrCloseMenu();
        }
    }

    public static ArrayList<PotionPoolScreen.DebugOption> getOptions(AbstractPotion p) {
        return EUIUtils.arrayList(DebugOption.obtain);
    }

    protected void obtain(AbstractPotion c) {
        if (c != null && AbstractDungeon.player != null) {
            AbstractDungeon.player.obtainPotion(c);
        }
    }

    public void openScreen(AbstractPlayer player, ArrayList<AbstractPotion> potions) {
        super.reopen();
        AbstractCard.CardColor color = player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS;

        potionGrid.clear();
        if (potions.isEmpty()) {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        potionGrid.setPotions(potions);

        EUI.potionFilters.initializeForCustomHeader(potionGrid.potionGroup, __ -> {
            ArrayList<AbstractPotion> headerPotions = EUI.potionHeader.getPotions();
            for (CustomPotionPoolModule module : EUI.globalCustomPotionPoolModules) {
                module.open(headerPotions, color, null);
            }
            if (customModule != null) {
                customModule.open(headerPotions, color, null);
            }
            potionGrid.forceUpdatePotionPositions();
        }, color, true, false);


        for (CustomPotionPoolModule module : EUI.globalCustomPotionPoolModules) {
            module.open(potions, color, null);
        }
        customModule = EUI.getCustomPotionPoolModule(player);
        if (customModule != null) {
            customModule.open(potions, color, null);
        }
    }

    @Override
    public void update() {
        if (!EUI.potionFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            potionGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapRelicScreen.updateImpl();
            EUI.potionHeader.updateImpl();
            EUI.openPotionFiltersButton.tryUpdate();
            for (CustomPotionPoolModule module : EUI.globalCustomPotionPoolModules) {
                module.update();
            }
            if (customModule != null) {
                customModule.update();
            }
        }
        contextMenu.tryUpdate();
    }

    @Override
    public void render(SpriteBatch sb) {
        potionGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapRelicScreen.renderImpl(sb);
        EUI.potionHeader.renderImpl(sb);
        if (!EUI.potionFilters.isActive) {
            EUI.openPotionFiltersButton.tryRender(sb);
        }
        for (CustomPotionPoolModule module : EUI.globalCustomPotionPoolModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    public static class DebugOption {
        public static PotionPoolScreen.DebugOption obtain = new PotionPoolScreen.DebugOption(EUIRM.strings.uipool_obtainPotion, PotionPoolScreen::obtain);

        public final String name;
        public final ActionT2<PotionPoolScreen, AbstractPotion> onSelect;

        DebugOption(String name, ActionT2<PotionPoolScreen, AbstractPotion> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}