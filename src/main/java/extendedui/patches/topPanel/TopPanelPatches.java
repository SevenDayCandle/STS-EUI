package extendedui.patches.topPanel;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CtBehavior;

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
}
