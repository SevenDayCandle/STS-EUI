package extendedui.ui.screens;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;

public abstract class EUIPoolScreen extends EUIDungeonScreen {

    public boolean allowOpenDeck() {
        return true;
    }

    public boolean allowOpenMap() {
        return true;
    }

    @Override
    public void close() {
        switchScreen();
    }

    public void open() {
        reopen();
    }

    @Override
    public final void openingDeck() {
        switchScreen();
    }

    @Override
    public final void openingMap() {
        switchScreen();
    }

    @Override
    public final void openingSettings() {
        switchScreen();
    }

    @Override
    public void reopen() {
        super.reopen();
        EUI.cardFilters.close();
        EUI.relicFilters.close();
        EUI.potionFilters.close();
        AbstractDungeon.dungeonMapScreen.map.hideInstantly(); // Because the map won't be hidden properly otherwise
        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
    }

    @Override
    public void switchScreen() {
        super.switchScreen();
        EUI.cardFilters.close();
        EUI.relicFilters.close();
        EUI.potionFilters.close();
        AbstractDungeon.overlayMenu.hideBlackScreen();
    }
}
