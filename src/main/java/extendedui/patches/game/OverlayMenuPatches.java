package extendedui.patches.game;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import extendedui.EUI;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class OverlayMenuPatches {

    @SpirePatch(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuPatches_Update {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> method(OverlayMenu __instance) {
            // To simulate AbstractDungeon.screen == CurrentScreen.NO_INTERACT
            if (EUI.disableInteract) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
