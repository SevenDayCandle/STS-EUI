package extendedui.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
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
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;

import java.util.ArrayList;

import static extendedui.EUIGameUtils.scale;

public class PotionPoolScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen POTION_POOL_SCREEN;

    public static CustomPoolModule<PotionInfo> customModule;
    protected final EUIContextMenu<PotionPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapRelicScreen;
    private AbstractPotion selected;
    public EUIPotionGrid potionGrid;

    public PotionPoolScreen() {
        potionGrid = (EUIPotionGrid) new EUIPotionGrid()
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

        swapRelicScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, EUIRM.strings.uipool_viewRelicPool)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.relicScreen.openScreen(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()));

        contextMenu = (EUIContextMenu<PotionPoolScreen.DebugOption>) new EUIContextMenu<PotionPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (PotionPoolScreen.DebugOption o : options) {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.tooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    public static ArrayList<PotionPoolScreen.DebugOption> getOptions(AbstractPotion p) {
        return EUIUtils.arrayList(DebugOption.obtain);
    }

    @Override
    public void switchScreen() {
        super.switchScreen();
        for (CustomPoolModule<PotionInfo> module : EUI.globalCustomPotionPoolModules) {
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

    protected void obtain(AbstractPotion c) {
        if (c != null && AbstractDungeon.player != null) {
            AbstractDungeon.player.obtainPotion(c.makeCopy());
        }
    }

    protected void onRightClick(PotionInfo c) {
        if (EUIConfiguration.enableCardPoolDebug.get()) {
            selected = c.potion;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.setItems(getOptions(c.potion));
            contextMenu.openOrCloseMenu();
        }
    }

    public void openScreen(AbstractPlayer player, ArrayList<AbstractPotion> potions) {
        super.reopen();
        boolean canSeeAllColors = EUIGameUtils.canReceiveAnyColorCard();
        AbstractCard.CardColor color = player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS;
        boolean isAll = player == null || canSeeAllColors;

        potionGrid.clear();
        potionGrid.setItems(potions, PotionInfo::new);

        EUI.potionFilters.initializeForSort(potionGrid.group, __ -> {
            for (CustomPoolModule<PotionInfo> module : EUI.globalCustomPotionPoolModules) {
                module.open(EUI.potionFilters.group.group, color, isAll, null);
            }
            if (customModule != null) {
                customModule.open(EUI.potionFilters.group.group, color, isAll, null);
            }
            potionGrid.forceUpdatePositions();
        }, color, GenericFilters.FILTERS_START_X, true, false);

        EUI.potionCounters.open(potionGrid.group.group, f -> EUI.potionFilters.setSort(f.type));

        for (CustomPoolModule<PotionInfo> module : EUI.globalCustomPotionPoolModules) {
            module.open(potionGrid.group.group, color, isAll, null);
        }
        customModule = EUI.getCustomPotionPoolModule(player);
        if (customModule != null) {
            customModule.open(potionGrid.group.group, color, isAll, null);
        }

        potionGrid.scrollBar.scroll(potionGrid.scrollBar.currentScrollPercent, true);
    }

    @Override
    public void render(SpriteBatch sb) {
        potionGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapRelicScreen.renderImpl(sb);
        EUI.sortHeader.renderImpl(sb);
        EUI.potionCounters.tryRender(sb);
        if (!EUI.potionFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomPoolModule<PotionInfo> module : EUI.globalCustomPotionPoolModules) {
            module.render(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    @Override
    public void update() {
        if (!EUI.potionFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            potionGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapRelicScreen.updateImpl();
            EUI.sortHeader.updateImpl();
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            EUI.potionCounters.tryUpdate();
            for (CustomPoolModule<PotionInfo> module : EUI.globalCustomPotionPoolModules) {
                module.update();
            }
            if (customModule != null) {
                customModule.update();
            }
        }
        contextMenu.tryUpdate();
        EUIExporter.exportDropdown.tryUpdate();
    }

    public static class DebugOption {
        public static PotionPoolScreen.DebugOption obtain = new PotionPoolScreen.DebugOption(EUIRM.strings.uipool_obtainPotion, PotionPoolScreen::obtain);

        public final String name;
        public final ActionT2<PotionPoolScreen, AbstractPotion> onSelect;

        public DebugOption(String name, ActionT2<PotionPoolScreen, AbstractPotion> onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}