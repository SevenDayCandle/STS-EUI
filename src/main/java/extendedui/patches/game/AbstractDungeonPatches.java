package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import extendedui.EUI;
import extendedui.STSEffekseerManager;
import javassist.CannotCompileException;
import javassist.CtBehavior;


public class AbstractDungeonPatches {

    public static AbstractDungeon.CurrentScreen coolerPreviousScreen;

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class AbstractDungeonPatches_CloseCurrentScreen {

        @SpirePrefixPatch
        public static void prefix() {
            if (coolerPreviousScreen != null && coolerPreviousScreen != AbstractDungeon.previousScreen) {
                AbstractDungeon.previousScreen = coolerPreviousScreen;
                coolerPreviousScreen = null;
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class AbstractDungeon_Render {
        @SpireInsertPatch(locator = Locator.class)
        public static void insert(AbstractDungeon __instance, SpriteBatch sb) {
            EUI.preRender(sb);
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void insert2(AbstractDungeon __instance, SpriteBatch sb) {
            STSEffekseerManager.update();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DynamicBanner.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(OverlayMenu.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
