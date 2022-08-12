package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.ui.FtueTip;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIRM;
import extendedui.ui.controls.GUI_Tutorial;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.Arrays;
import java.util.Collection;

public class FakeFtue extends FtueTip {

    protected static final float WIDTH = Settings.scale * 700;
    protected static final float HEIGHT = Settings.scale * 500;
    protected GUI_Tutorial tutorial;

    public FakeFtue(GUI_Tutorial tutorial) {
        this.tutorial = tutorial;
        OpenScreen();
    }

    public FakeFtue(String title, String... descriptions) {
        this(new GUI_Tutorial(new AdvancedHitbox(Settings.WIDTH / 2.0F - WIDTH / 2, Settings.HEIGHT / 2.0F - HEIGHT / 2, WIDTH, HEIGHT), EUIRM.Images.Panel_Large.Texture(), title, Arrays.asList(descriptions)));
    }

    public void OpenScreen() {
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
        tutorial.Update();
        AbstractDungeon.overlayMenu.cancelButton.update();
        if (AbstractDungeon.overlayMenu.cancelButton.hb.clicked) {
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.closeCurrentScreen();
        }
    }

    public void render(SpriteBatch sb) {
        tutorial.Render(sb);
    }
}
