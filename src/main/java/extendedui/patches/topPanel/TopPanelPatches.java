package extendedui.patches.topPanel;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class TopPanelPatches {

    @SpirePatch(clz= TopPanel.class, method="update")
    public static class TopPanelPatches_Update
    {
        @SpirePrefixPatch
        public static SpireReturn method(TopPanel __instance)
        {
            // To simulate AbstractDungeon.screen == CurrentScreen.NO_INTERACT
            if (AbstractDungeon.screen == EUI_SCREEN && Settings.hideTopBar)
            {
                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz= TopPanel.class, method="updateRelics")
    public static class TopPanelPatches_UpdateRelics
    {
        @SpirePrefixPatch
        public static SpireReturn method(TopPanel __instance)
        {
            // To simulate AbstractDungeon.screen == CurrentScreen.NO_INTERACT
            if (AbstractDungeon.screen == EUI_SCREEN && Settings.hideRelics)
            {
                return SpireReturn.Return(null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz= TopPanel.class, method="renderPotionTips")
    public static class TopPanel_RenderPotionTips
    {
        @SpireInsertPatch(locator = Locator.class, localvars = {"p"})
        public static SpireReturn<Void> insert(TopPanel __instance, AbstractPotion p)
        {
            if (p instanceof TooltipProvider) {
                EUITooltip.queueTooltips((AbstractPotion & TooltipProvider) p);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "queuePowerTips");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "updateDeckViewButtonLogic")
    public static class MasterDeckViewScreen_UpdateDeckViewButtonLogic
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new SecondOnlyExprEditor();
        }

        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> insert(TopPanel __instance)
        {
            if (isCompendiumScreen())
            {
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.deckViewScreen.open();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
                int[] found = LineFinder.findAllInOrder(ctBehavior, matcher);
                return new int[]{ found[found.length-1] };
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "updateMapButtonLogic")
    public static class MasterDeckViewScreen_UpdateMapButtonLogic
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new SecondOnlyExprEditor();
        }

        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> insert(TopPanel __instance)
        {
            if (isCompendiumScreen())
            {
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.dungeonMapScreen.open(false);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
                int[] found = LineFinder.findAllInOrder(ctBehavior, matcher);
                return new int[]{ found[found.length-1] };
            }
        }
    }

    public static class SecondOnlyExprEditor extends ExprEditor
    {
        int count = 0;

        public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
        {
            if (m.getClassName().equals(AbstractDungeon.class.getName()) && m.getFieldName().equals("screen") && m.isReader())
            {
                count += 1;
                if (count == 2)
                {
                    m.replace("$_ = extendedui.patches.topPanel.TopPanelPatches.getScreenToCheck();");
                }
            }
        }
    }

    public static AbstractDungeon.CurrentScreen getScreenToCheck()
    {
        return isCompendiumScreen() ? AbstractDungeon.CurrentScreen.NONE : AbstractDungeon.screen;
    }

    // TODO more robust checks in case we need to have other combat screens
    public static boolean isCompendiumScreen()
    {
        return AbstractDungeon.screen == EUI_SCREEN;
    }
}
