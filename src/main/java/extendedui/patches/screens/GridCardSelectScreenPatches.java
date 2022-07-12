package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import extendedui.ui.GridCardSelectScreenHelper;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GridCardSelectScreenPatches {

    @SpirePatch(clz= GridCardSelectScreen.class, method="calculateScrollBounds")
    public static class GridCardSelectScreen_CalculateScrollBounds
    {
        @SpirePrefixPatch
        public static SpireReturn Prefix(GridCardSelectScreen __instance)
        {
            if (GridCardSelectScreenHelper.CalculateScrollBounds(__instance))
            {
                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz= GridCardSelectScreen.class, method="callOnOpen")
    public static class GridCardSelectScreen_CallOnOpen
    {
        @SpirePostfixPatch
        public static void Postfix(GridCardSelectScreen __instance)
        {
            GridCardSelectScreenHelper.Open(__instance);
        }
    }

    @SpirePatch(clz= GridCardSelectScreen.class, method="updateCardPositionsAndHoverLogic")
    public static class GridCardSelectScreen_UpdateCardPositionsAndHoverLogic
    {
        @SpirePrefixPatch
        public static SpireReturn Prefix(GridCardSelectScreen __instance)
        {
            if (GridCardSelectScreenHelper.UpdateCardPositionAndHover(__instance))
            {
                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GridUpdate {
        public GridUpdate() {
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert2(GridCardSelectScreen __instance) {
            if (__instance.anyNumber) {
                __instance.confirmButton.isDisabled = !GridCardSelectScreenHelper.IsConditionMet();
            }
        }

        private static class Locator extends SpireInsertLocator {
            private Locator() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridSelectConfirmButton.class, "update");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]};
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class
        )
        public static void Insert3(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.InvokeOnClick(__instance);
        }

        private static class Locator2 extends SpireInsertLocator {
            private Locator2() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "contains");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[0] - 1};
            }
        }

        @SpirePostfixPatch
        public static void Postfix(GridCardSelectScreen __instance)
        {
            GridCardSelectScreenHelper.UpdateDynamicString();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
    )
    public static class GridRender {
        public GridRender() {
        }

        @SpirePostfixPatch
        public static void Postfix(GridCardSelectScreen __instance, SpriteBatch sb)
        {
            GridCardSelectScreenHelper.RenderDynamicString(sb);
        }
    }
}
