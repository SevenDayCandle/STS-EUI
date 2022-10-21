package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class OptionsPanelPatches
{
    public static EUILabel ModSettings = new EUILabel(FontHelper.panelEndTurnFont, new AdvancedHitbox(Settings.WIDTH * 0.18F, Settings.HEIGHT * 0.021f, 300.0F * Settings.scale, 72.0F * Settings.scale)).SetText(EUIRM.Strings.Misc_ModSettings);

    @SpirePatch(clz= OptionsPanel.class, method="update")
    public static class OptionsPanel_Update
    {

        @SpirePostfixPatch
        public static void Postfix(OptionsPanel __instance)
        {
            TryUpdate();
        }
    }

    @SpirePatch(clz= OptionsPanel.class, method="render", paramtypez = {SpriteBatch.class})
    public static class OptionsPanel_Render
    {

        @SpirePostfixPatch
        public static void Postfix(OptionsPanel __instance, SpriteBatch sb)
        {
            ModSettings.TryRender(sb);
        }
    }

    private static void TryUpdate()
    {
        ModSettings.TryUpdate();
        if (ModSettings.hb.hovered)
        {
            ModSettings.SetColor(Settings.GREEN_TEXT_COLOR);
            if (EUIInputManager.LeftClick.IsJustPressed())
            {
                EUI.ModSettingsScreen.Open();
            }
        }
        else
        {
            ModSettings.SetColor(Color.WHITE);
        }
    }
}
