package extendedui.ui.screens;

import basemod.abstracts.CustomScreen;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.patches.game.AbstractDungeonPatches;

public abstract class EUIPoolScreen extends CustomScreen {

    public void switchScreen() {
        Settings.hideRelics = false;
        EUI.disableInteract = false;
        genericScreenOverlayReset();
        AbstractDungeon.overlayMenu.cancelButton.hide();
        AbstractDungeon.overlayMenu.hideBlackScreen();
        AbstractDungeon.isScreenUp = false;
    }

    @Override
    public void close() {
        switchScreen();
    }

    @Override
    public void openingDeck() {
        switchScreen();
    }

    @Override
    public void openingMap() {
        switchScreen();
    }

    @Override
    public void openingSettings() {
        switchScreen();
    }

    @Override
    public void reopen() {
        Settings.hideRelics = true;
        EUI.disableInteract = true;
        AbstractDungeon.isScreenUp = true;
        if (AbstractDungeonPatches.coolerPreviousScreen == null) {
            AbstractDungeonPatches.coolerPreviousScreen = isScreenNonEmpty(AbstractDungeon.previousScreen) ? AbstractDungeon.previousScreen
                    : isScreenValid(AbstractDungeon.screen) ? AbstractDungeon.screen : null;
        }
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.overlayMenu.showBlackScreen(0.7f);
        AbstractDungeon.dungeonMapScreen.map.hideInstantly(); // Because the map won't be hidden properly otherwise
        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
    }

    public void open() {
        reopen();
    }

    public boolean allowOpenDeck() {
        return true;
    }

    public boolean allowOpenMap() {
        return true;
    }

    public boolean isScreenNonEmpty(AbstractDungeon.CurrentScreen s) {
        return !(s == null || s == AbstractDungeon.CurrentScreen.NONE);
    }

    public boolean isScreenValid(AbstractDungeon.CurrentScreen s) {
        switch (s) {
            case MAP:
            case MASTER_DECK_VIEW:
            case SETTINGS:
            case INPUT_SETTINGS:
            case NONE:
                return false;
        }
        return s != CardPoolScreen.CARD_POOL_SCREEN && s != RelicPoolScreen.RELIC_POOL_SCREEN && s != PotionPoolScreen.POTION_POOL_SCREEN;
    }
}
