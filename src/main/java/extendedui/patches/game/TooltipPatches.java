package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public class TooltipPatches {
    private static ArrayList<EUITooltip> currentTips;
    private static EUITooltip currentSingleTip;
    private static Object lastTips;

    public static void clearTips() {
        lastTips = null;
        currentTips = null;
        currentSingleTip = null;
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractMonster_RenderTip {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractMonster __instance, SpriteBatch sb) {
            if (EUIConfiguration.useEUITooltips.get()) {
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
            if (EUIConfiguration.useEUITooltips.get()) {
                if (EUITooltip.canRenderTooltips()) {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
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
                        EUIKeywordTooltip kTip = EUIKeywordTooltip.findByName(tip.header.toLowerCase());
                        if (kTip != null) {
                            return kTip.isRenderable() ? kTip : null;
                        }
                        return new EUIKeywordTooltip(tip.header, tip.body);
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
}
