package extendedui.patches.game;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.JavaUtils;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class AbstractDungeonPatches {
    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class AbstractDungeonPatches_CloseCurrentScreen
    {
        @SpirePrefixPatch
        public static void Prefix()
        {
            if (AbstractDungeon.screen == EUI_SCREEN)
            {
                EUI.Dispose();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "openPreviousScreen")
    public static class AbstractDungeonPatches_OpenPreviousScreen
    {
        @SpirePrefixPatch
        public static void Prefix(AbstractDungeon.CurrentScreen s)
        {
            if (EUI.CurrentScreen != null)
            {
                // closeCurrentScreen will set screen to NONE if the previous screen was null
                if (s == AbstractDungeon.CurrentScreen.NONE) {
                    AbstractDungeon.screen = EUI_SCREEN;
                }
                EUI.CurrentScreen.Reopen();
            }
        }
    }
}
