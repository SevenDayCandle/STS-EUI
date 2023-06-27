package extendedui.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.exporter.EUIExporter;
import extendedui.STSEffekseerManager;
import extendedui.ui.tooltips.EUITourTooltip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;

public class CardCrawlGamePatches {
    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_PreRender {
        @SpirePrefixPatch
        public static void preFix(CardCrawlGame __instance) {
            STSEffekseerManager.preUpdate();
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_Render {
        @SpireInsertPatch(locator = Locator.class, localvars = {"sb"})
        public static void insert(CardCrawlGame __instance, SpriteBatch sb) {
            EUI.render(sb);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DrawMaster.class, "draw");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_PostRender {
        @SpireInsertPatch(locator = Locator.class, localvars = {"sb"})
        public static void insert(CardCrawlGame __instance, SpriteBatch sb) {
            EUI.relicFilters.tryRender(sb);
            EUI.potionFilters.tryRender(sb);
            EUI.cardFilters.tryRender(sb);
            EUI.blightFilters.tryRender(sb);
            EUIExporter.exportDropdown.tryRender(sb);
            EUI.postRender(sb);
            EUI.priorityPostRender(sb);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "update")
    public static class CardCrawlGame_Update {
        @SpireInsertPatch(locator = Locator.class)
        public static void insert(CardCrawlGame __instance) {
            EUI.update();
        }

        @SpirePrefixPatch
        public static void prefix(CardCrawlGame __instance) {
            EUI.preUpdate();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(InputHelper.class, "updateFirst");
                int[] res = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                res[0] += 1;
                return res;
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "dispose")
    public static class CardCrawlGame_Dispose {
        @SpirePostfixPatch
        public static void postfix(CardCrawlGame __instance) {
            STSEffekseerManager.end();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Terminated STSEffekseerManager");
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "startOver")
    public static class CardCrawlGame_StartOver {
        @SpirePrefixPatch
        public static void prefix() {
            EUITourTooltip.clearTutorialQueue();
        }
    }
}
