package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
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
import extendedui.ui.hitboxes.AdvancedHitbox;

public class OptionsPanelPatches
{
    public static EUILabel ModSettings = new EUILabel(FontHelper.panelEndTurnFont, new AdvancedHitbox(Settings.WIDTH * 0.18F, Settings.HEIGHT * 0.021f, 300.0F * Settings.scale, 72.0F * Settings.scale)).setLabel(EUIRM.Strings.Misc_ExtraSettings);

    @SpirePatch(clz= OptionsPanel.class, method="update")
    public static class OptionsPanel_Update
    {
        @SpirePostfixPatch
        public static SpireReturn<Void> prefix(OptionsPanel __instance)
        {
            if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && EUI.CurrentScreen == EUI.ModSettingsScreen)
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
            ModSettings.tryRender(sb);
        }
    }

    private static void tryUpdate()
    {
        ModSettings.tryUpdate();
        if (ModSettings.hb.hovered)
        {
            ModSettings.setColor(Settings.GREEN_TEXT_COLOR);
            if (EUIInputManager.LeftClick.isJustPressed())
            {
                EUI.ModSettingsScreen.open();
            }
        }
        else
        {
            ModSettings.setColor(Color.WHITE);
        }
    }
}
