package extendedui.ui.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.EUIBlightGrid;

public class BlightLibraryScreen extends AbstractMenuScreen {
    public final MenuCancelButton cancelButton;
    public EUIBlightGrid grid;

    public BlightLibraryScreen() {
        grid = (EUIBlightGrid) new EUIBlightGrid()
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);
        cancelButton = new MenuCancelButton();
    }

    @Override
    public void close() {
        super.close();
        for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
            module.onClose();
        }
    }

    @Override
    public void open() {
        super.open();
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);

        grid.clear();
        grid.add(EUIGameUtils.getAllBlights());

        EUI.blightFilters.initializeForSort(grid.group, __ -> {
            for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
                module.open(EUI.blightFilters.group.group, AbstractCard.CardColor.COLORLESS, null);
            }
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS);

        for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
            module.open(grid.group.group, AbstractCard.CardColor.COLORLESS, null);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        grid.tryRender(sb);
        cancelButton.render(sb);
        EUI.sortHeader.renderImpl(sb);
        if (!EUI.blightFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
            module.render(sb);
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        boolean shouldDoStandardUpdate = !EUI.blightFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            cancelButton.update();
            if (this.cancelButton.hb.clicked) {
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                close();
            }
            EUI.sortHeader.updateImpl();
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
                module.update();
            }
        }
        EUIExporter.exportDropdown.tryUpdate();
    }
}
