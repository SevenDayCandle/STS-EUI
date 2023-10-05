package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import extendedui.EUI;

public class EnergyPanelPatches {
    @SpirePatch(clz = EnergyPanel.class, method = "render")
    public static class EnergyPanel_Render {
        @SpirePostfixPatch
        public static void postfix(EnergyPanel __instance, SpriteBatch sb) {
            EUI.renderBattleSubscribers(sb);
        }
    }
}