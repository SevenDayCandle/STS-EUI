package extendedui.patches.game;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import extendedui.EUIGameUtils;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

public class CardGlowBorderPatches {

    @SpirePatch(clz = CardGlowBorder.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, Color.class})
    public static class CardGlowBorderPatches_ctor {
        // Because this crashes in the constructor outside of a run -_-
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractRoom.class.getName()) && m.getFieldName().equals("phase")) {
                        m.replace("{ $_ = extendedui.patches.game.CardGlowBorderPatches.pleaseStopAssumingGetCurrRoomIsNonnull(); }");
                    }
                }
            };
        }
    }

    public static AbstractRoom.RoomPhase pleaseStopAssumingGetCurrRoomIsNonnull() {
        AbstractRoom curRoom = AbstractDungeon.getCurrRoom();
        return curRoom != null ? curRoom.phase : AbstractRoom.RoomPhase.INCOMPLETE;
    }
}