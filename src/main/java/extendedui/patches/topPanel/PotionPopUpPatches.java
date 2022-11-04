package extendedui.patches.topPanel;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ClassUtils;
import javassist.CtBehavior;

public class PotionPopUpPatches {

    @SpirePatch(clz= PotionPopUp.class, method="update")
    public static class PotionPopUp_Update
    {

        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(PotionPopUp __instance)
        {
            TooltipProvider p = EUIUtils.SafeCast(ClassUtils.GetField(__instance, "x"), TooltipProvider.class);
            if (p != null) {
                EUITooltip.QueueTooltips(p.GetTips(), ClassUtils.GetFieldAsType(__instance, "x", Float.class) + 180.0F * Settings.scale, ClassUtils.GetFieldAsType(__instance, "y", Float.class)  + 70.0F * Settings.scale);
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
