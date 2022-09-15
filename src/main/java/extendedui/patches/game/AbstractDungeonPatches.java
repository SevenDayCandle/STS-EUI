package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import extendedui.EUI;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class AbstractDungeonPatches {
    private static boolean fromEUI;

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class AbstractDungeonPatches_CloseCurrentScreen
    {
        @SpirePrefixPatch
        public static void Prefix()
        {
            if (AbstractDungeon.screen == EUI_SCREEN)
            {
                EUI.Dispose();
                fromEUI = true;
            }
        }

        @SpirePostfixPatch
        public static void Postfix()
        {
            if (AbstractDungeon.screen != EUI_SCREEN)
            {
                EUI.PostDispose();
                // Dungeon map needs to be manually closed after returning to the main screen
                if (fromEUI)
                {
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP)
                    {
                        AbstractDungeon.dungeonMapScreen.map.hideInstantly();
                    }
                    Settings.hideTopBar = false;
                    Settings.hideRelics = false;
                    fromEUI = false;
                }
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
