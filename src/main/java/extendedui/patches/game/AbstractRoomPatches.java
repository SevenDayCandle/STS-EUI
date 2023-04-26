package extendedui.patches.game;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.EUI;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class AbstractRoomPatches {
    @SpirePatch(clz = AbstractRoom.class, method = "update")
    public static class AbstractRoom_Update {
        @SpirePrefixPatch
        public static void prefix() {
            // Pressing Esc with the card filters open on the master deck screen will open the settings screen before the filters update, so we need a check here too
            if (EUI.cardFilters.tryUpdate() && (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW || AbstractDungeon.screen != EUI_SCREEN)) {
                EUI.cardFilters.close();
            }
        }
    }
}
