package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import extendedui.EUI;
import extendedui.ui.EUIBase;

public class EnergyPanelPatches {
    @SpirePatch(clz = EnergyPanel.class, method = "render")
    public static class EnergyPanel_Render {
        @SpirePostfixPatch
        public static void postfix(EnergyPanel __instance, SpriteBatch sb) {
            for (EUIBase s : EUI.battleSubscribers) {
                s.tryRender(sb);
            }
        }
    }
}