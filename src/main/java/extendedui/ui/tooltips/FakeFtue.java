package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.ui.FtueTip;
import extendedui.EUIRM;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.Arrays;

public class FakeFtue extends FtueTip {

    protected static final float WIDTH = Settings.scale * 700;
    protected static final float HEIGHT = Settings.scale * 500;
    protected EUITutorial tutorial;

    public FakeFtue(EUITutorial tutorial) {
        this.tutorial = tutorial;
        openScreen();
    }

    public FakeFtue(String title, String... descriptions) {
        this(new EUITutorial(new AdvancedHitbox(Settings.WIDTH / 2.0F - WIDTH / 2, Settings.HEIGHT / 2.0F - HEIGHT / 2, WIDTH, HEIGHT), EUIRM.images.panelLarge.texture(), title, Arrays.asList(descriptions)));
    }

    public void openScreen() {
        AbstractDungeon.player.releaseCard();
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }

        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.FTUE;
        AbstractDungeon.overlayMenu.showBlackScreen();

        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
    }

    public void update() {
        tutorial.updateImpl();
        AbstractDungeon.overlayMenu.cancelButton.update();
        if (AbstractDungeon.overlayMenu.cancelButton.hb.clicked) {
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.closeCurrentScreen();
        }
    }

    public void render(SpriteBatch sb) {
        tutorial.renderImpl(sb);
    }
}
