package extendedui.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;
import extendedui.ui.tooltips.EUITourTooltip;

// Variant of AbstractScreen to be used for screens that can be opened in menus
public abstract class AbstractMenuScreen extends EUIBase {

    public static MainMenuScreen.CurScreen previousMainScreen;

    @SpireEnum
    public static MainMenuScreen.CurScreen EUI_MENU;

    public void close() {
        CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        EUITourTooltip.clearTutorialQueue();
        if (EUI.currentScreen == this) {
            dispose();
        }
    }

    public void dispose() {
        EUI.currentScreen = null;
        EUITourTooltip.clearTutorialQueue();

        if (!EUIGameUtils.inGame() && CardCrawlGame.mainMenuScreen != null) {
            CardCrawlGame.mainMenuScreen.screen = previousMainScreen;
        }
    }

    public void open() {
        EUI.currentScreen = this;
        EUITourTooltip.clearTutorialQueue();
        if (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen != EUI_MENU) {
            previousMainScreen = CardCrawlGame.mainMenuScreen.screen;
            CardCrawlGame.mainMenuScreen.screen = EUI_MENU;
        }
    }

    public void preRender(SpriteBatch sb) {

    }

    public void renderImpl(SpriteBatch sb) {

    }

    public void reopen() {

    }

    public void updateImpl() {
        if (EUIInputManager.tryEscape()) {
            close();
        }
    }

}
