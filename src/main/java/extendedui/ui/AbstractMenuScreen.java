package extendedui.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;

// Variant of AbstractScreen to be used for screens that can be opened in menus
public abstract class AbstractMenuScreen extends EUIBase {

    public static MainMenuScreen.CurScreen previousMainScreen;

    @SpireEnum
    public static MainMenuScreen.CurScreen EUI_MENU;

    public void dispose() {
        EUI.currentScreen = null;

        if (!EUIGameUtils.inGame() && CardCrawlGame.mainMenuScreen != null) {
            CardCrawlGame.mainMenuScreen.screen = previousMainScreen;
        }
    }

    public void open() {
        EUI.currentScreen = this;
        if (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen != EUI_MENU) {
            previousMainScreen = CardCrawlGame.mainMenuScreen.screen;
            CardCrawlGame.mainMenuScreen.screen = EUI_MENU;
        }
    }

    public void reopen() {

    }

    public void close() {
        CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        if (EUI.currentScreen == this) {
            dispose();
        }
    }

    public void updateImpl() {
        if (EUIInputManager.tryEscape()) {
            close();
        }
    }

    public void preRender(SpriteBatch sb) {

    }

    public void renderImpl(SpriteBatch sb) {

    }

}
