package extendedui.patches;


import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import com.megacrit.cardcrawl.screens.options.RemapInputElement;
import javassist.CtBehavior;
import extendedui.EUIRM;
import extendedui.configuration.EUIHotkeys;

import java.util.ArrayList;


public class HotkeyPatches
{

    @SpirePatch(
            clz=InputSettingsScreen.class,
            method="refreshData"
    )
    public static class RefreshData
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"elements"}
        )
        public static void Insert(InputSettingsScreen __instance, ArrayList<RemapInputElement> elements)
        {
            if (!Settings.isControllerMode) {
                elements.add(new RemapInputElement(__instance, EUIRM.Strings.Hotkey_Cycle, EUIHotkeys.cycle));
                elements.add(new RemapInputElement(__instance, EUIRM.Strings.Hotkey_Toggle, EUIHotkeys.toggleFilters));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputSettingsScreen.class, "maxScrollAmount");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz= InputActionSet.class,
            method="load"
    )
    public static class Load
    {
        public static void Prefix()
        {
            EUIHotkeys.load();
        }
    }

    @SpirePatch(
            clz=InputActionSet.class,
            method="save"
    )
    public static class Save
    {
        public static void Prefix()
        {
            EUIHotkeys.save();
        }
    }

    @SpirePatch(
            clz=InputActionSet.class,
            method="resetToDefaults"
    )
    public static class Reset
    {
        public static void Prefix()
        {
            EUIHotkeys.resetToDefaults();
        }
    }
}