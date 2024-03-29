package extendedui.patches.game;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;

public class SoulPatches {
    @SpirePatch(clz = Soul.class, method = "obtain", paramtypez = {AbstractCard.class})
    public static class SoulPatches_Obtain {
        @SpirePostfixPatch
        public static void postfix(Soul __instance, AbstractCard card) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW && EUI.cardCounters.isActive) {
                EUI.cardCounters.open(AbstractDungeon.player.masterDeck.group, f -> AbstractDungeon.player.masterDeck.group.sort(f.type));
            }
        }
    }
}
