package extendedui.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.cardFilter.SimpleCardFilterModule;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class CompatibilityPatches {

    // Colored power tips
    @SpirePatch(cls = "coloredPowertips.ColoredPowertips", method = "receiveStartGame", requiredModId = "coloredpowertips", optional = true)
    public static class ColoredPowertips_ReceiveStartGame {

        @SpirePostfixPatch
        public static void postfix() {
            try {
                try {
                    EUITooltip.TIP_BUFF = EUIClassUtils.getRFieldStatic("coloredPowertips.patches.ColoredPowerPowertips", "BUFF_COL");
                    EUITooltip.TIP_DEBUFF = EUIClassUtils.getRFieldStatic("coloredPowertips.patches.ColoredPowerPowertips", "DEBUFF_COL");
                }
                catch (Exception e) {
                    EUIUtils.logWarning(EUITooltip.class, "OH NOES, loadColoredTipsCompatibility");
                    e.printStackTrace();
                    EUITooltip.TIP_BUFF = Color.WHITE;
                    EUITooltip.TIP_DEBUFF = Color.WHITE;
                }
            }
            catch (Exception e) {
                EUIUtils.logWarning(ColoredPowertips_ReceiveStartGame.class, "OH NOES");
                e.printStackTrace();
            }
        }
    }


    // Packmaster stuff

    @SpirePatch(cls = "thePackmaster.SpireAnniversary5Mod", method = "receivePostInitialize", requiredModId = "anniv5", optional = true)
    public static class PackmasterPatches_PostInitialize {

        @SpirePostfixPatch
        public static void postfix() {
            try {
                AbstractCard.CardColor packMasterColor = AbstractCard.CardColor.valueOf("PACKMASTER_RAINBOW");
                UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("anniv5:PackPreviewCard");
                EUI.setCustomCardFilter(packMasterColor,
                        new SimpleCardFilterModule<Object>(uiStrings.TEXT[1], t -> {
                            try {
                                return EUIClassUtils.getRField("thePackmaster.packs.AbstractCardPack", "name", t);
                            }
                            catch (Exception ignored) {
                                return "";
                            }
                        }, card -> {
                            try {
                                return EUIClassUtils.invokeR("thePackmaster.cards.AbstractPackmasterCard", "getParent", card);
                            }
                            catch (Exception ignored) {
                                return null;
                            }
                        }));
            }
            catch (Exception e) {
                EUIUtils.logWarning(PackmasterPatches_PostInitialize.class, "OH NOES");
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(cls = "thePackmaster.patches.RenderBaseGameCardPackTopTextPatches", method = "isInPackmasterCardLibraryScreen", requiredModId = "anniv5", optional = true)
    public static class PackmasterPatches_IsInPackmasterCardLibraryScreen {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Boolean> prefix() {
            try {
                AbstractCard.CardColor packMasterColor = AbstractCard.CardColor.valueOf("PACKMASTER_RAINBOW");
                if (CustomCardLibraryScreen.currentColor == packMasterColor && !EUIConfiguration.useVanillaCompendium.get()) {
                    return SpireReturn.Return(true);
                }
            }
            catch (Exception e) {
                EUIUtils.logWarning(PackmasterPatches_IsInPackmasterCardLibraryScreen.class, "OH NOES");
                e.printStackTrace();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ReflectionHacks.class, "getPrivate");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
