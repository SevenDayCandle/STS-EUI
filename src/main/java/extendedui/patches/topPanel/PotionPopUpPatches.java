package extendedui.patches.topPanel;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import javassist.CtBehavior;
import extendedui.JavaUtils;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.FieldInfo;
import extendedui.utilities.abstracts.TooltipPotion;

public class PotionPopUpPatches {
    protected static final FieldInfo<AbstractPotion> _potion = JavaUtils.GetField("potion", PotionPopUp.class);
    protected static final FieldInfo<Float> _x = JavaUtils.GetField("x", PotionPopUp.class);
    protected static final FieldInfo<Float> _y = JavaUtils.GetField("y", PotionPopUp.class);

    @SpirePatch(clz= PotionPopUp.class, method="update")
    public static class PotionPopUp_Update
    {

        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(PotionPopUp __instance)
        {
            TooltipPotion p = JavaUtils.SafeCast(_potion.Get(__instance), TooltipPotion.class);
            if (p != null) {
                EUITooltip.QueueTooltips(p.GetTips(), _x.Get(__instance) + 180.0F * Settings.scale, _y.Get(__instance)  + 70.0F * Settings.scale);
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
