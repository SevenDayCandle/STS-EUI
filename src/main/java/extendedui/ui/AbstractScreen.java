package extendedui.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public abstract class AbstractScreen extends EUIBase
{
    @SpireEnum
    public static AbstractDungeon.CurrentScreen EUI_SCREEN;
    @SpireEnum
    public static MainMenuScreen.CurScreen EUI_MENU;

    protected static MainMenuScreen.CurScreen previousMainScreen;

    public boolean canOpen()
    {
        return isNullOrNone(AbstractDungeon.previousScreen) && !CardCrawlGame.isPopupOpen;
    }

    protected void open() {
        open(true, true);
    }

    protected void open(boolean hideTopBar, boolean hideRelics)
    {

        updateDungeonScreen();

        Settings.hideTopBar = hideTopBar;
        Settings.hideRelics = hideRelics;
        EUI.CurrentScreen = this;

        AbstractDungeon.isScreenUp = true;

        if (EUIGameUtils.inBattle())
        {
            AbstractDungeon.player.releaseCard();
            AbstractDungeon.overlayMenu.hideCombatPanels();
        }

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.topPanel.unhoverHitboxes();
            AbstractDungeon.topPanel.potionUi.isHidden = true;

            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.proceedButton.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.overlayMenu.showBlackScreen(0.7f);
        }
        else if (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen != EUI_MENU) {
            previousMainScreen = CardCrawlGame.mainMenuScreen.screen;
            CardCrawlGame.mainMenuScreen.screen = EUI_MENU;
        }

    }

    protected void updateDungeonScreen()
    {
        if (AbstractDungeon.screen != EUI_SCREEN) {

            // These screens should not be recorded as the previous screen
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SETTINGS
                    && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.INPUT_SETTINGS
                    && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP
                    && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }

            AbstractDungeon.screen = EUI_SCREEN;
        }
    }

    public void reopen() {

    }

    public void dispose()
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

        if (AbstractDungeon.player == null || !EUIGameUtils.inGame())
        {
            AbstractDungeon.isScreenUp = !isNullOrNone(previous);
            if (CardCrawlGame.mainMenuScreen != null) {
                CardCrawlGame.mainMenuScreen.screen = previousMainScreen;
            }
            return;
        }

        if (isNullOrNone(previous) || previous == EUI_SCREEN)
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
        if (EUIGameUtils.inBattle())
        {
            AbstractDungeon.overlayMenu.showCombatPanels();
        }

    }

    public void updateImpl()
    {
    }

    public void preRender(SpriteBatch sb)
    {

    }

    public void renderImpl(SpriteBatch sb)
    {

    }

    public static EUIButton createHexagonalButton(float x, float y, float width, float height)
    {
        final Texture buttonTexture = EUIRM.Images.HexagonalButton.texture();
        final Texture buttonBorderTexture = EUIRM.Images.HexagonalButtonBorder.texture();
        return new EUIButton(buttonTexture, x, y)
        .setBorder(buttonBorderTexture, Color.WHITE)
        .setClickDelay(0.25f)
        .setDimensions(width, height);
    }

    private static boolean isNullOrNone(AbstractDungeon.CurrentScreen screen)
    {
        return screen == null || screen == AbstractDungeon.CurrentScreen.NONE;
    }
}
