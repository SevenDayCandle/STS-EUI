package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import extendedui.ui.GridCardSelectScreenHelper;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GridCardSelectScreenPatches {

    @SpirePatch(clz = GridCardSelectScreen.class, method = "calculateScrollBounds")
    public static class GridCardSelectScreen_CalculateScrollBounds {
        @SpirePrefixPatch
        public static SpireReturn prefix(GridCardSelectScreen __instance) {
            if (GridCardSelectScreenHelper.calculateScrollBounds(__instance)) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "callOnOpen")
    public static class GridCardSelectScreen_CallOnOpen {
        @SpirePostfixPatch
        public static void postfix(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.open(__instance);
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "updateCardPositionsAndHoverLogic")
    public static class GridCardSelectScreen_UpdateCardPositionsAndHoverLogic {
        @SpirePrefixPatch
        public static SpireReturn prefix(GridCardSelectScreen __instance) {
            if (GridCardSelectScreenHelper.updateCardPositionAndHover(__instance)) {
                return SpireReturn.Return(null);
            }
            else {
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
        public static void insert2(GridCardSelectScreen __instance) {
            if (__instance.anyNumber) {
                __instance.confirmButton.isDisabled = !GridCardSelectScreenHelper.isConditionMet();
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class
        )
        public static void insert3(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.invokeOnClick(__instance);
        }

        @SpirePostfixPatch
        public static void postfix(GridCardSelectScreen __instance) {
            GridCardSelectScreenHelper.updateDynamicString();
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

        private static class Locator2 extends SpireInsertLocator {
            private Locator2() {
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "contains");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[0] - 1};
            }
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
        public static void postfix(GridCardSelectScreen __instance, SpriteBatch sb) {
            GridCardSelectScreenHelper.renderDynamicString(sb);
        }
    }
}
