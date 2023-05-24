package extendedui.ui.screens;

import basemod.abstracts.CustomScreen;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;

public abstract class EUIDungeonScreen extends CustomScreen {

    public void switchScreen() {
        Settings.hideTopBar = false;
        Settings.hideRelics = false;
        EUI.disableInteract = false;
        AbstractDungeon.isScreenUp = false;
        AbstractDungeon.overlayMenu.hideBlackScreen();
    }

    @Override
    public void close() {
        switchScreen();
    }

    @Override
    public void openingSettings() {
        switchScreen();
    }

    @Override
    public void reopen() {
        Settings.hideTopBar = true;
        Settings.hideRelics = true;
        EUI.disableInteract = true;
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.overlayMenu.showBlackScreen(0.7f);
    }

    public void open() {
        reopen();
    }

}