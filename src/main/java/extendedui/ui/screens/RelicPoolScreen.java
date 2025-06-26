package extendedui.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;

import java.util.ArrayList;

import static extendedui.EUIGameUtils.scale;

public class RelicPoolScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen RELIC_POOL_SCREEN;

    public static CustomPoolModule<RelicInfo> customModule;
    protected final EUIContextMenu<RelicPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapPotionScreen;
    private AbstractRelic selected;
    public EUIRelicGrid relicGrid;

    public RelicPoolScreen() {
        relicGrid = (EUIRelicGrid) new EUIRelicGrid()
                .setOnClick(r -> this.openPopup(r.relic))
                .setOnRightClick(this::onRightClick)
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);

        swapCardScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.9f)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, EUIRM.strings.uipool_viewCardPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.cardsScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllCards()));

        swapPotionScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, EUIRM.strings.uipool_viewPotionPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.potionScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllPotions()));

        contextMenu = (EUIContextMenu<RelicPoolScreen.DebugOption>) new EUIContextMenu<RelicPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (RelicPoolScreen.DebugOption o : options) {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.tooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    public static ArrayList<RelicPoolScreen.DebugOption> getOptions(AbstractRelic r) {
        return EUIUtils.arrayList(DebugOption.enlarge, DebugOption.obtain, DebugOption.removeFromPool);
    }

    @Override
    public void switchScreen() {
        super.switchScreen();
        for (CustomPoolModule<RelicInfo> module : EUI.globalCustomRelicPoolModules) {
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

    protected void obtain(AbstractRelic c) {
        if (c != null) {
            AbstractRelic copy = c.makeCopy();
            copy.instantObtain();
        }
    }

    protected void onRightClick(RelicInfo c) {
        if (EUIConfiguration.enableCardPoolDebug.get()) {
            selected = c.relic;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.setItems(getOptions(c.relic));
            contextMenu.openOrCloseMenu();
        }
        else {
            openPopup(c.relic);
        }
    }

    protected void openPopup(AbstractRelic c) {
        c.hb.unhover();
        CardCrawlGame.relicPopup.open(c);
    }

    public void openScreen(AbstractPlayer player, ArrayList<AbstractRelic> relics) {
        super.reopen();
        boolean canSeeAllColors = EUIGameUtils.canReceiveAnyColorCard();
        AbstractCard.CardColor color = player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS;
        boolean isAll = player == null || canSeeAllColors;

        relicGrid.clear();
        relicGrid.setItems(relics, RelicInfo::new);

        EUI.relicFilters.initializeForSort(relicGrid.group, __ -> {
            for (CustomPoolModule<RelicInfo> module : EUI.globalCustomRelicPoolModules) {
                module.open(EUI.relicFilters.group.group, color, isAll, null);
            }
            if (customModule != null) {
                customModule.open(EUI.relicFilters.group.group, color, isAll, null);
            }
            relicGrid.forceUpdatePositions();
        }, color, GenericFilters.FILTERS_START_X, true, false);

        EUI.relicCounters.open(relicGrid.group.group, f -> EUI.relicFilters.setSort(f.type));

        for (CustomPoolModule<RelicInfo> module : EUI.globalCustomRelicPoolModules) {
            module.open(relicGrid.group.group, color, isAll, null);
        }
        customModule = EUI.getCustomRelicPoolModule(player);
        if (customModule != null) {
            customModule.open(relicGrid.group.group, color, isAll, null);
        }

        relicGrid.scrollBar.scroll(relicGrid.scrollBar.currentScrollPercent, true);
    }

    protected void removeRelicFromPool(AbstractRelic c) {
        for (ArrayList<String> relics : EUIGameUtils.getGameRelicPools()) {
            relics.remove(c.relicId);
        }
        relicGrid.remove(c);
        EUI.relicCounters.open(relicGrid.group.group, f -> EUI.relicFilters.setSort(f.type));
    }

    @Override
    public void render(SpriteBatch sb) {
        relicGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapPotionScreen.renderImpl(sb);
        EUI.sortHeader.renderImpl(sb);
        EUI.relicCounters.tryRender(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomPoolModule<RelicInfo> module : EUI.globalCustomRelicPoolModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    @Override
    public void update() {
        if (!EUI.relicFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            relicGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapPotionScreen.updateImpl();
            EUI.sortHeader.updateImpl();
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            EUI.relicCounters.tryUpdate();
            for (CustomPoolModule<RelicInfo> module : EUI.globalCustomRelicPoolModules) {
                module.update();
            }
            if (customModule != null) {
                customModule.update();
            }
            if (relicGrid.hovered != null) {
                CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
            }
        }
        contextMenu.tryUpdate();
        EUIExporter.exportDropdown.tryUpdate();
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