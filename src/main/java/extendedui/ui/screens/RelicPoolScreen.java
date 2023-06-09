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
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomRelicPoolModule;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

import static extendedui.EUIGameUtils.scale;

public class RelicPoolScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen RELIC_POOL_SCREEN;

    public static CustomRelicPoolModule customModule;
    protected final EUIContextMenu<RelicPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapPotionScreen;
    private AbstractRelic selected;
    public EUIRelicGrid relicGrid;

    public RelicPoolScreen() {
        relicGrid = (EUIRelicGrid) new EUIRelicGrid()
                .setOnRelicRightClick(this::onRightClick)
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);

        swapCardScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.9f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewCardPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.cardsScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllCards()));

        swapPotionScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.uipool_viewPotionPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.potionScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllPotions()));

        contextMenu = (EUIContextMenu<RelicPoolScreen.DebugOption>) new EUIContextMenu<RelicPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (RelicPoolScreen.DebugOption o : options) {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    public static ArrayList<RelicPoolScreen.DebugOption> getOptions(AbstractRelic r) {
        return EUIUtils.arrayList(DebugOption.enlarge, DebugOption.obtain, DebugOption.removeFromPool);
    }

    @Override
    public void close() {
        super.close();
        for (CustomRelicPoolModule module : EUI.globalCustomRelicPoolModules) {
            module.onClose();
        }
        if (customModule != null) {
            customModule.onClose();
        }
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return RELIC_POOL_SCREEN;
    }

    @Override
    public void update() {
        if (!EUI.relicFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            relicGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapPotionScreen.updateImpl();
            EUI.relicHeader.updateImpl();
            EUI.openRelicFiltersButton.tryUpdate();
            for (CustomRelicPoolModule module : EUI.globalCustomRelicPoolModules) {
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
        relicGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapPotionScreen.renderImpl(sb);
        EUI.relicHeader.renderImpl(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openRelicFiltersButton.tryRender(sb);
        }
        for (CustomRelicPoolModule module : EUI.globalCustomRelicPoolModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    protected void obtain(AbstractRelic c) {
        if (c != null) {
            AbstractRelic copy = c.makeCopy();
            copy.instantObtain();
        }
    }

    protected void onRightClick(AbstractRelic c) {
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

    protected void openPopup(AbstractRelic c) {
        c.hb.unhover();
        CardCrawlGame.relicPopup.open(c);
    }

    public void openScreen(AbstractPlayer player, ArrayList<AbstractRelic> relics) {
        super.reopen();
        AbstractCard.CardColor color = player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS;

        relicGrid.clear();
        if (relics.isEmpty()) {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        relicGrid.setRelics(relics);

        EUI.relicFilters.initializeForCustomHeader(relicGrid.relicGroup, __ -> {
            ArrayList<AbstractRelic> headerRelics = EUI.relicHeader.getRelics();
            for (CustomRelicPoolModule module : EUI.globalCustomRelicPoolModules) {
                module.open(headerRelics, color, null);
            }
            if (customModule != null) {
                customModule.open(headerRelics, color, null);
            }
            relicGrid.forceUpdateRelicPositions();
        }, color, true, false);

        for (CustomRelicPoolModule module : EUI.globalCustomRelicPoolModules) {
            module.open(relics, color, null);
        }
        customModule = EUI.getCustomRelicPoolModule(player);
        if (customModule != null) {
            customModule.open(relics, color, null);
        }

    }

    protected void removeRelicFromPool(AbstractRelic c) {
        for (ArrayList<String> relics : EUIGameUtils.getGameRelicPools()) {
            relics.remove(c.relicId);
        }
        relicGrid.removeRelic(c);
    }

    public static class DebugOption {
        public static RelicPoolScreen.DebugOption enlarge = new RelicPoolScreen.DebugOption(EUIRM.strings.uipool_enlarge, RelicPoolScreen::openPopup);
        public static RelicPoolScreen.DebugOption obtain = new RelicPoolScreen.DebugOption(EUIRM.strings.uipool_obtainRelic, RelicPoolScreen::obtain);
        public static RelicPoolScreen.DebugOption removeFromPool = new RelicPoolScreen.DebugOption(EUIRM.strings.uipool_removeFromPool, RelicPoolScreen::removeRelicFromPool);

        public final String name;
        public final ActionT2<RelicPoolScreen, AbstractRelic> onSelect;

        public DebugOption(String name, ActionT2<RelicPoolScreen, AbstractRelic> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}