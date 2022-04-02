package stseffekseer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import stseffekseer.EUI;
import stseffekseer.EUIGameUtils;
import stseffekseer.EUIRM;
import stseffekseer.ui.controls.GUI_Button;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public abstract class AbstractScreen extends GUI_Base
{
    @SpireEnum
    public static AbstractDungeon.CurrentScreen EUI_SCREEN;

    public boolean CanOpen()
    {
        return IsNullOrNone(AbstractDungeon.previousScreen) && !CardCrawlGame.isPopupOpen;
    }

    protected void Open()
    {
        EUI.CurrentScreen = this;
        Settings.hideTopBar = true;
        Settings.hideRelics = true;

        AbstractDungeon.previousScreen = AbstractDungeon.screen;
        AbstractDungeon.screen = EUI_SCREEN;
        AbstractDungeon.isScreenUp = true;

        if (EUIGameUtils.InBattle())
        {
            AbstractDungeon.player.releaseCard();
            AbstractDungeon.overlayMenu.hideCombatPanels();
        }

        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.topPanel.unhoverHitboxes();
            AbstractDungeon.topPanel.potionUi.isHidden = true;

            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.proceedButton.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.overlayMenu.showBlackScreen(0.7f);
        }

        CardCrawlGame.sound.play("DECK_OPEN");
    }

    public void Dispose()
    {
        // Modified Logic from AbstractDungeon.closeCurrentScreen and AbstractDungeon.genericScreenOverlayReset
        EUI.CurrentScreen = null;
        Settings.hideTopBar = false;
        Settings.hideRelics = false;

        AbstractDungeon.CurrentScreen previous = AbstractDungeon.previousScreen;
        if (previous == AbstractDungeon.CurrentScreen.NONE)
        {
            AbstractDungeon.previousScreen = null;
            AbstractDungeon.screen = previous;
        }

        if (AbstractDungeon.player == null || !EUIGameUtils.InGame())
        {
            AbstractDungeon.isScreenUp = !IsNullOrNone(previous);
            return;
        }

        if (IsNullOrNone(previous) || previous == EUI_SCREEN)
        {
            if (AbstractDungeon.player.isDead)
            {
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
            }
            else
            {
                AbstractDungeon.isScreenUp = false;
                AbstractDungeon.overlayMenu.hideBlackScreen();
            }
        }

        AbstractDungeon.overlayMenu.cancelButton.hide();

        if (EUIGameUtils.InBattle())
        {
            AbstractDungeon.overlayMenu.showCombatPanels();
        }
    }

    public void Update()
    {

    }

    public void Render(SpriteBatch sb)
    {

    }

    public static GUI_Button CreateHexagonalButton(float x, float y, float width, float height)
    {
        final Texture buttonTexture = EUIRM.Images.HexagonalButton.Texture();
        final Texture buttonBorderTexture = EUIRM.Images.HexagonalButtonBorder.Texture();
        return new GUI_Button(buttonTexture, x, y)
        .SetBorder(buttonBorderTexture, Color.WHITE)
        .SetClickDelay(0.25f)
        .SetDimensions(width, height);
    }

    private static boolean IsNullOrNone(AbstractDungeon.CurrentScreen screen)
    {
        return screen == null || screen == AbstractDungeon.CurrentScreen.NONE;
    }
}
