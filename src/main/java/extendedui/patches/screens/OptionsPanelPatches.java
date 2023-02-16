package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;

public class OptionsPanelPatches
{
    public static EUILabel modSettings = new EUILabel(FontHelper.panelEndTurnFont, new EUIHitbox(Settings.WIDTH * 0.18F, Settings.HEIGHT * 0.021f, 300.0F * Settings.scale, 72.0F * Settings.scale)).setLabel(EUIRM.strings.miscExtrasettings);

    @SpirePatch(clz= OptionsPanel.class, method="update")
    public static class OptionsPanel_Update
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(OptionsPanel __instance)
        {
            if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && EUI.currentScreen == EUI.modSettingsScreen)
            {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void postfix(OptionsPanel __instance)
        {
            tryUpdate();
        }
    }

    @SpirePatch(clz= OptionsPanel.class, method="render", paramtypez = {SpriteBatch.class})
    public static class OptionsPanel_Render
    {

        @SpirePostfixPatch
        public static void postfix(OptionsPanel __instance, SpriteBatch sb)
        {
            modSettings.tryRender(sb);
        }
    }

    private static void tryUpdate()
    {
        modSettings.tryUpdate();
        if (modSettings.hb.hovered)
        {
            modSettings.setColor(Settings.GREEN_TEXT_COLOR);
            if (EUIInputManager.leftClick.isJustPressed())
            {
                EUI.modSettingsScreen.open();
            }
        }
        else
        {
            modSettings.setColor(Color.WHITE);
        }
    }
}
