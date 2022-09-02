package extendedui.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CardCrawlGamePatches
{
    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_Render
    {
        @SpireInsertPatch(locator = Locator.class, localvars = {"sb"})
        public static void Insert(CardCrawlGame __instance, SpriteBatch sb)
        {
            EUI.Render(sb);
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DrawMaster.class, "draw");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_PostRender
    {
        @SpireInsertPatch(locator = Locator.class, localvars = {"sb"})
        public static void Insert(CardCrawlGame __instance, SpriteBatch sb)
        {
            EUI.RelicFilters.TryRender(sb);
            EUI.CardFilters.TryRender(sb);
            EUI.PostRender(sb);
            EUI.PriorityPostRender(sb);
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "update")
    public static class CardCrawlGame_Update
    {
        @SpirePrefixPatch
        public static void Prefix(CardCrawlGame __instance)
        {
            EUI.PreUpdate();
        }

        @SpirePostfixPatch
        public static void Postfix(CardCrawlGame __instance)
        {
            EUI.PostUpdate();
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(CardCrawlGame __instance)
        {
            EUI.Update();
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(InputHelper.class, "updateFirst");
                int[] res = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                res[0] += 1;
                return res;
            }
        }
    }
}
