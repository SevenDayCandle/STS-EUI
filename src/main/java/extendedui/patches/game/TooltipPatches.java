package extendedui.patches.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class TooltipPatches {
    private static ArrayList<EUITooltip> currentTips;
    private static EUITooltip currentSingleTip;
    private static Object lastTips;
    private static String capturedKeyword; // God dammit

    public static void clearTips() {
        lastTips = null;
        currentTips = null;
        currentSingleTip = null;
    }

    public static ArrayList<String> getFilteredKeywords(ArrayList<String> original) {
        return EUIUtils.filter(original, o -> !EUIConfiguration.getIsTipDescriptionHiddenByName(o));
    }

    public static boolean useEUIForPowers() {
        return EUIConfiguration.useEUITooltips.get();
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractMonster_RenderTip {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractMonster __instance, SpriteBatch sb) {
            if (useEUIForPowers()) {
                if (__instance.reticleAlpha == 0) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "renderPowerTips", paramtypez = {SpriteBatch.class})
    public static class AbstractPlayer_RenderPowerTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractPlayer __instance, SpriteBatch sb) {
            if (useEUIForPowers()) {
                if (EUITooltip.canRenderTooltips()) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractBlight.class, method = "initializeTips")
    public static class AbstractBlight_InitializeTips {

        @SpireInsertPatch(locator = Locator.class, localvars = {"s"})
        public static void method(AbstractBlight __instance, String s) {
            capturedKeyword = s;
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void method2(AbstractBlight __instance) {
            PowerTip_Keyword.value.set(__instance.tips.get(__instance.tips.size() - 1), capturedKeyword);
        }
    }

    @SpirePatch(clz = AbstractBlight.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractBlight_RenderTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractBlight __instance, SpriteBatch sb) {
            if (EUIConfiguration.useEUITooltips.get()) {
                if (EUITooltip.canRenderTooltips()) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPotion.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, AbstractPotion.PotionRarity.class, AbstractPotion.PotionSize.class, AbstractPotion.PotionColor.class})
    @SpirePatch(clz = AbstractPotion.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, AbstractPotion.PotionRarity.class, AbstractPotion.PotionSize.class, AbstractPotion.PotionEffect.class, Color.class, Color.class, Color.class})
    public static class AbstractPotion_Ctor {
        @SpirePostfixPatch
        public static void postfix(AbstractPotion __instance) {
            // Skip the first tip
            // Assign the headers as keywords since we know these are keywords. Not the best solution but we have no other way of getting keywords since tip additions are hardcoded -_-
            for (int i = 1; i < __instance.tips.size(); i++) {
                PowerTip sk = __instance.tips.get(i);
                PowerTip_Keyword.value.set(__instance.tips.get(__instance.tips.size() - 1), sk.header.toLowerCase());
            }
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "initializeTips")
    public static class AbstractRelic_InitializeTips {

        @SpireInsertPatch(locator = Locator.class, localvars = {"s"})
        public static void method(AbstractRelic __instance, String s) {
            capturedKeyword = s;
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void method2(AbstractRelic __instance) {
            PowerTip_Keyword.value.set(__instance.tips.get(__instance.tips.size() - 1), capturedKeyword);
            capturedKeyword = null;
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractRelic_RenderTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractRelic __instance, SpriteBatch sb) {
            if (EUIConfiguration.useEUITooltips.get()) {
                if (EUITooltip.canRenderTooltips()) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TipHelper.class, method = "queuePowerTips")
    public static class TipHelper_QueuePowerTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(float x, float y, ArrayList<PowerTip> powerTips) {
            if (EUIConfiguration.useEUITooltips.get()) {
                if (lastTips != powerTips) {
                    lastTips = powerTips;
                    currentTips = EUIUtils.mapAsNonnull(powerTips, tip -> {
                        EUIKeywordTooltip kTip = null;
                        String key = TooltipPatches.PowerTip_Keyword.value.get(tip);
                        if (key != null) {
                            kTip = EUIKeywordTooltip.findByName(key);
                            if (kTip != null) {
                                return kTip.isRenderable() ? kTip : null;
                            }
                        }
                        kTip = new EUIKeywordTooltip(tip.header, tip.body);
                        if (tip.img != null) {
                            kTip.setIcon(tip.img);
                        }
                        return kTip;
                    });
                }
                EUITooltip.queueTooltips(currentTips);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TipHelper.class, method = "renderGenericTip")
    public static class TipHelper_RenderGenericTip {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(float x, float y, String header, String body) {
            if (EUIConfiguration.useEUITooltips.get()) {
                // Intentional pointer comparison with header object
                if (lastTips != header) {
                    lastTips = header;
                    currentSingleTip = new EUITooltip(header, body);
                }
                EUITooltip.queueTooltip(currentSingleTip);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TipHelper.class, method = "renderTipForCard")
    public static class TipHelper_RenderTipForCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard c, SpriteBatch sb) {
            if (EUIConfiguration.useEUITooltips.get()) {
                if (lastTips != c) {
                    lastTips = c;
                    currentTips = EUIUtils.mapAsNonnull(c.keywords, k -> {
                        EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(k);
                        return tip != null && tip.isRenderable() ? tip : null;
                    });
                }
                EUITooltip.queueTooltips(currentTips);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    // Make a proxy arraylist that only renders tooltips that can be seen
    // TODO edit renderKeywords instead
    @SpirePatch(clz = TipHelper.class, method = "render")
    public static class TipHelperPatches_Render {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getFieldName().equals("KEYWORDS")) {
                        m.replace("{ $_ = extendedui.patches.game.TooltipPatches.getFilteredKeywords(KEYWORDS); }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = TipHelper.class, method = "renderPowerTips")
    public static class TipHelperPatches_RenderPowerTips {

        // Ensure that the tip region is always rendered at Settings.scale * 32f regardless of the actual image size
        // Needed to prevent image from rendering outside of the tip if the region is of the wrong size (e.g. from Fabricate dynamic power regions)
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    String fieldName = m.getFieldName();
                    if (fieldName.equals("packedWidth") || fieldName.equals("packedHeight")) {
                        m.replace("{ $_ = 32f; }");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = PowerTip.class,
            method = "<class>"
    )
    public static class PowerTip_Keyword {
        // Records the keyword used to create this tip, to be used for reverse lookups in the EUI filter menu
        public static SpireField<String> value = new SpireField<>(() -> {
            return null;
        });
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameDictionary.class, "parentWord");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "capitalize");
            return new int[]{LineFinder.findInOrder(ctMethodToPatch, finalMatcher)[0] + 1};
        }
    }
}
