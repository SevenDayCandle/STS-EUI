package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.controls.GUI_Label;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.ClassUtils;

import static extendedui.ui.GUI_Base.*;

public class SettingsScreenPatches
{
    public static GUI_Label ModSettings = new GUI_Label(FontHelper.panelEndTurnFont, new AdvancedHitbox(Settings.WIDTH * 0.17F, Settings.HEIGHT * 0.023f, 300.0F * Settings.scale, 72.0F * Settings.scale)).SetText(EUIRM.Strings.Misc_ModSettings);

    @SpirePatch(clz= SettingsScreen.class, method="update")
    public static class SettingsScreen_Update
    {

        @SpirePostfixPatch
        public static void Postfix(SettingsScreen __instance)
        {
            TryUpdate();
        }
    }

    @SpirePatch(clz= SettingsScreen.class, method="render", paramtypez = {SpriteBatch.class})
    public static class SettingsScreen_Render
    {

        @SpirePostfixPatch
        public static void Postfix(SettingsScreen __instance, SpriteBatch sb)
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
