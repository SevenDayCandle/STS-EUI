package extendedui.ui.screens;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;

public abstract class EUIPoolScreen extends EUIDungeonScreen {

    public boolean allowOpenDeck() {
        return true;
    }

    public boolean allowOpenMap() {
        return true;
    }

    @Override
    public void openingDeck() {
        switchScreen();
    }

    @Override
    public void openingMap() {
        switchScreen();
    }

    public void open() {
        reopen();
    }

    @Override
    public void reopen() {
        super.reopen();
        AbstractDungeon.dungeonMapScreen.map.hideInstantly(); // Because the map won't be hidden properly otherwise
        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
    }

    @Override
    public void close() {
        switchScreen();
    }

    @Override
    public void openingSettings() {
        switchScreen();
    }

    public void switchScreen() {
        super.switchScreen();
        AbstractDungeon.overlayMenu.hideBlackScreen();
    }
}
