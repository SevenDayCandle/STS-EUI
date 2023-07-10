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
    private static AbstractCard lastCard;
    private static ArrayList<EUITooltip> currentTips;
    private static Object lastTips;

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

    @SpirePatch(clz = AbstractPotion.class, method = "renderTip", paramtypez = {SpriteBatch.class})
    public static class AbstractPotion_RenderTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractPotion __instance, SpriteBatch sb) {
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
                    currentTips = EUIUtils.map(powerTips, tip -> new EUITooltip(tip.header, tip.body));
                }
                EUITooltip.queueTooltips(currentTips);
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
                if (lastCard != c) {
                    lastCard = c;
                    currentTips = EUIUtils.map(c.keywords, EUIKeywordTooltip::findByName);
                }
                EUITooltip.queueTooltips(currentTips);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}
