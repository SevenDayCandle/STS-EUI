package extendedui.patches.game;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import extendedui.EUI;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CtBehavior;

public class TopPanelPatches {

    @SpirePatch(clz = TopPanel.class, method = "update")
    public static class TopPanelPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(TopPanel __instance) {
            // To simulate AbstractDungeon.screen == CurrentScreen.NO_INTERACT
            if (EUI.disableInteract && Settings.hideTopBar) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "updateRelics")
    public static class TopPanelPatches_UpdateRelics {
        @SpirePrefixPatch
        public static SpireReturn<Void> method(TopPanel __instance) {
            // To simulate AbstractDungeon.screen == CurrentScreen.NO_INTERACT
            if (EUI.disableInteract && Settings.hideRelics) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "renderPotionTips")
    public static class TopPanel_RenderPotionTips {
        @SpireInsertPatch(locator = Locator.class, localvars = {"p"})
        public static SpireReturn<Void> insert(TopPanel __instance, AbstractPotion p) {
            if (p instanceof TooltipProvider) {
                EUITooltip.queueTooltips(p);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "queuePowerTips");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
