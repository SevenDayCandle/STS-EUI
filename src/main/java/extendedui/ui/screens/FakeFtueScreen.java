package extendedui.ui.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.controls.EUITutorial;

public class FakeFtueScreen extends EUIDungeonScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen FAKE_FTUE_SCREEN;

    public final MenuCancelButton button;
    protected EUITutorial current;
    protected ActionT0 onClose;

    public FakeFtueScreen() {
        super();
        button = new MenuCancelButton();
    }

    @Override
    public void close() {
        super.close();
        AbstractDungeon.isScreenUp = false;
        if (current != null) {
            current.close();
        }
        this.button.hide();

        if (onClose != null) {
            onClose.invoke();
        }
    }

    @Override
    public void openingSettings() {

    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return FAKE_FTUE_SCREEN;
    }

    @Override
    public void update() {
        if (current != null) {
            if (InputHelper.justClickedLeft && !current.isHovered() && !CardCrawlGame.isPopupOpen) {
                exitScreen();
                return;
            }
            current.updateImpl();
        }

        button.update();
        if (this.button.hb.clicked || EUIInputManager.tryEscape()) {
            exitScreen();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (current != null) {
            current.renderImpl(sb);
        }
        button.render(sb);
    }

    public void exitScreen() {
        this.button.hb.clicked = false;
        AbstractDungeon.closeCurrentScreen();
    }

    public void openScreen(EUITutorial ftue) {
        super.reopen();
        this.button.show(CardLibraryScreen.TEXT[0]);
        current = ftue;
    }

    public void openScreen(EUITutorial ftue, ActionT0 onClose) {
        openScreen(ftue);
        this.onClose = onClose;
    }
}
