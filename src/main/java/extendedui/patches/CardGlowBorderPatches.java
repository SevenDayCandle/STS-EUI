package extendedui.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import extendedui.JavaUtils;
import extendedui.utilities.ClassUtils;

public class CardGlowBorderPatches
{
    public static Color overrideColor;

    @SpirePatch(clz = CardGlowBorder.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, Color.class})
    public static class CardGlowBorderPatches_ctor
    {
        @SpirePostfixPatch
        public static void Method(CardGlowBorder __instance, AbstractCard card, Color letsHardcodeEverything)
        {
            if (overrideColor != null)
            {
                Color color = ClassUtils.GetField(__instance, "color");
                if (color != null)
                {
                    color.r = overrideColor.r;
                    color.g = overrideColor.g;
                    color.b = overrideColor.b;
                }
            }
        }
    }

    @SpirePatch(clz = CardGlowBorder.class, method = "update")
    public static class CardGlowBorderPatches_Update
    {
        @SpirePrefixPatch
        public static void Method(CardGlowBorder __instance)
        {
            AbstractCard card = ClassUtils.GetField(__instance, "card");
            if (card.transparency < 0.9f)
            {
                __instance.duration = 0f;
            }
        }
    }
}