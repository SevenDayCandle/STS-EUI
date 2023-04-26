package extendedui.ui.panelitems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUITutorial;

public class FakeFtueScreen extends AbstractScreen {
    public final MenuCancelButton button;
    protected EUITutorial current;
    protected ActionT0 onClose;

    public FakeFtueScreen() {
        super();
        button = new MenuCancelButton();
    }

    public void open(EUITutorial ftue, ActionT0 onClose) {
        open(ftue);
        this.onClose = onClose;
    }    @Override
    public void onEscape() {
        super.onEscape();
        if (EUIGameUtils.inGame()) {
            AbstractDungeon.closeCurrentScreen();
        }
        else {
            CardCrawlGame.mainMenuScreen.panelScreen.refresh();
            if (EUI.currentScreen == this) {
                dispose();
            }
        }
        EUI.setActiveElement(null);
        CardCrawlGame.isPopupOpen = false;

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public void open(EUITutorial ftue) {
        super.open();
        current = ftue;
        this.button.show(CardLibraryScreen.TEXT[0]);
    }    @Override
    public void updateImpl() {
        super.updateImpl();
        if (current != null) {
            current.updateImpl();
        }

        button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            this.button.hb.clicked = false;
            this.button.hide();
            if (current != null) {
                current.close();
            }
            onEscape();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        if (current != null) {
            current.renderImpl(sb);
        }
        button.render(sb);
    }






}
