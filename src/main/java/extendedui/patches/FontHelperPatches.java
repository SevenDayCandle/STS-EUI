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

public class FontHelperPatches {
    @SpirePatch(
            clz = FontHelper.class,
            method = "initialize"
    )
    public static class FontHelperPatches_Initialize {
        @SpireInsertPatch(locator = BoldLocator1.class)
        public static void insertBold() {
            if (EUIConfiguration.overrideGameFont.get()) {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomBoldFontFile(Settings.language));
            }
        }

        @SpireInsertPatch(locator = BoldLocator2.class)
        public static void insertBold2() {
            if (EUIConfiguration.overrideGameFont.get()) {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomBoldFontFile(Settings.language));
            }
        }

        @SpireInsertPatch(locator = MainLocator.class)
        public static void insertMain() {
            if (EUIConfiguration.overrideGameFont.get()) {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomDefaultFontFile(Settings.language));
            }
        }

        @SpirePostfixPatch
        public static void postfix() {
            EUIFontHelper.initialize();
            if (EUIConfiguration.overrideGameFont.get() && EUIConfiguration.useSeparateFonts.get()) {
                EUIFontHelper.overwriteBaseFonts();
            }
        }

        private static class MainLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FreeTypeFontGenerator.FreeTypeBitmapFontData.class, "xChars");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class BoldLocator1 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "energyNumFontRed");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class BoldLocator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "cardTypeFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
