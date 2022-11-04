package extendedui.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.configuration.EUIConfiguration;
import extendedui.utilities.EUIFontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class FontHelperPatches
{
    @SpirePatch(
            clz= FontHelper.class,
            method="initialize"
    )
    public static class FontHelperPatches_Initialize
    {
        @SpireInsertPatch(locator = MainLocator.class)
        public static void InsertMain(FontHelper __instance)
        {
            if (EUIConfiguration.OverrideGameFont.Get())
            {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.GetCustomDefaultFontFile(Settings.language));
            }
        }

        private static class MainLocator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FreeTypeFontGenerator.FreeTypeBitmapFontData.class, "xChars");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInsertPatch(locator = BoldLocator1.class)
        public static void InsertBold(FontHelper __instance)
        {
            if (EUIConfiguration.OverrideGameFont.Get())
            {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.GetCustomBoldFontFile(Settings.language));
            }
        }

        private static class BoldLocator1 extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "energyNumFontRed");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInsertPatch(locator = BoldLocator2.class)
        public static void InsertBold2(FontHelper __instance)
        {
            if (EUIConfiguration.OverrideGameFont.Get())
            {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.GetCustomBoldFontFile(Settings.language));
            }
        }

        private static class BoldLocator2 extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "cardTypeFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpirePostfixPatch
        public static void Postfix()
        {
            EUIFontHelper.Initialize();
            if (EUIConfiguration.OverrideGameFont.Get())
            {
                EUIFontHelper.OverwriteBaseFonts();
            }
        }
    }
}
