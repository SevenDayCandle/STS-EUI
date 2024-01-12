package extendedui.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.configuration.EUIConfiguration;
import extendedui.utilities.EUIFontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;

public class FontHelperPatches {
    // Do not apply linear filtering to the power font
    public static boolean forceFiltering(FileHandle file, boolean initValue) {
        return initValue || (EUIConfiguration.forceLinearFiltering.get() && !file.path().equals(EUIFontHelper.TINY_NUMBERS_FONT));
    }

    @SpirePatch(
            clz = FontHelper.class,
            method = "initialize"
    )
    public static class FontHelperPatches_Initialize {

        // Skip if separate fonts is not used
        @SpireInsertPatch(locator = BannerFontLocator.class)
        public static void insertBanner() {
            if (EUIConfiguration.overrideGameFont.get() && EUIConfiguration.useSeparateFonts.get()) {
                FileHandle fontFileBold = EUIFontHelper.getCustomBoldFontFile(Settings.language);
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomFont(EUIConfiguration.bannerFont, fontFileBold));
            }
        }

        // Skip if separate fonts is not used
        @SpireInsertPatch(locator = ButtonFontLocator.class)
        public static void insertButton() {
            if (EUIConfiguration.overrideGameFont.get() && EUIConfiguration.useSeparateFonts.get()) {
                FileHandle fontFile = EUIFontHelper.getCustomDefaultFontFile(Settings.language);
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomFont(EUIConfiguration.buttonFont, fontFile));
            }
        }

        // Default to bold
        @SpireInsertPatch(locator = CardTypeLocator.class)
        public static void insertCardType() {
            if (EUIConfiguration.overrideGameFont.get()) {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomBoldFontFile(Settings.language));
            }
        }

        // Default to bold
        @SpireInsertPatch(locator = EnergyFontLocator.class)
        public static void insertEnergy() {
            if (EUIConfiguration.overrideGameFont.get()) {
                FileHandle fontFileBold = EUIFontHelper.getCustomBoldFontFile(Settings.language);
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIConfiguration.useSeparateFonts.get() ? EUIFontHelper.getCustomFont(EUIConfiguration.energyFont, fontFileBold) : fontFileBold);
            }
        }

        @SpireInsertPatch(locator = MainLocator.class)
        public static void insertMain() {
            if (EUIConfiguration.overrideGameFont.get()) {
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomDefaultFontFile(Settings.language));
            }
        }

        // Skip if separate fonts is not used
        @SpireInsertPatch(locator = TipBodyLocator.class)
        public static void insertTipBody() {
            if (EUIConfiguration.overrideGameFont.get() && EUIConfiguration.useSeparateFonts.get()) {
                FileHandle fontFile = EUIFontHelper.getCustomDefaultFontFile(Settings.language);
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomFont(EUIConfiguration.tipDescFont, fontFile));
            }
        }

        // Skip if separate fonts is not used
        @SpireInsertPatch(locator = BannerFontLocator.class)
        public static void insertTipTitle() {
            if (EUIConfiguration.overrideGameFont.get() && EUIConfiguration.useSeparateFonts.get()) {
                FileHandle fontFileBold = EUIFontHelper.getCustomBoldFontFile(Settings.language);
                ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", EUIFontHelper.getCustomFont(EUIConfiguration.tipTitleFont, fontFileBold));
            }
        }

        @SpirePostfixPatch
        public static void postfix() {
            EUIFontHelper.initialize();
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("prepFont")) {
                        m.replace("{ $_ = $proceed($1, extendedui.patches.FontHelperPatches.forceFiltering(fontFile, $2)); }");
                    }
                }
            };
        }

        private static class BannerFontLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "dungeonTitleFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class ButtonFontLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "buttonLabelFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class CardTypeLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "cardTypeFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class EnergyFontLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "energyNumFontRed");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class MainLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FreeTypeFontGenerator.FreeTypeBitmapFontData.class, "xChars");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class TipBodyLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "tipBodyFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class TipTitleLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "tipHeaderFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
