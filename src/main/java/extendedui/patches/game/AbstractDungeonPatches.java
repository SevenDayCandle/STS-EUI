package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import extendedui.EUI;
import extendedui.JavaUtils;
import extendedui.patches.CardCrawlGamePatches;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class AbstractDungeonPatches {
    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class AbstractDungeonPatches_CloseCurrentScreen
    {
        @SpirePrefixPatch
        public static void Prefix()
        {
            if (AbstractDungeon.screen == EUI_SCREEN)
            {
                EUI.Dispose();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "openPreviousScreen")
    public static class AbstractDungeonPatches_OpenPreviousScreen
    {
        @SpirePrefixPatch
        public static void Prefix(AbstractDungeon.CurrentScreen s)
        {
            if (EUI.CurrentScreen != null)
            {
                // closeCurrentScreen will set screen to NONE if the previous screen was null
                if (s == AbstractDungeon.CurrentScreen.NONE) {
                    AbstractDungeon.screen = EUI_SCREEN;
                }
                EUI.CurrentScreen.Reopen();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class AbstractDungeon_Render
    {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractDungeon __instance, SpriteBatch sb)
        {
            EUI.PreRender(sb);
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DynamicBanner.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
