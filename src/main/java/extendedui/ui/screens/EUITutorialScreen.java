package extendedui.ui.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.controls.EUITutorial;

public class EUITutorialScreen extends AbstractMenuScreen {
    public final MenuCancelButton button;
    protected EUITutorial current;
    protected ActionT0 onClose;

    public EUITutorialScreen() {
        super();
        button = new MenuCancelButton();
    }

    @Override
    public void close() {
        if (current != null) {
            current.close();
        }
        this.button.hide();
        super.close();

        if (onClose != null) {
            onClose.invoke();
        }
    }

    public void exitScreen() {
        this.button.hb.clicked = false;
        this.button.hide();
        close();
    }

    public void open(EUITutorial ftue) {
        super.open();
        current = ftue;
        this.button.show(CardLibraryScreen.TEXT[0]);
    }

    public void open(EUITutorial ftue, ActionT0 onClose) {
        open(ftue);
        this.onClose = onClose;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        if (current != null) {
            current.renderImpl(sb);
        }
        button.render(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (current != null) {
            if (InputHelper.justClickedLeft && !current.isHovered() && !CardCrawlGame.isPopupOpen) {
                exitScreen();
                return;
            }
            current.updateImpl();
        }

        button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            exitScreen();
        }
    }
}
