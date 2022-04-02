package extendedui.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;

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
}
