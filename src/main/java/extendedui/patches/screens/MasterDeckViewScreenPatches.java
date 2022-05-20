package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.cardFilter.CardPoolScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

import java.util.ArrayList;

import static extendedui.ui.cardFilter.CustomCardLibSortHeader.GetFakeGroup;

public class MasterDeckViewScreenPatches
{
    public static final CardGroup fakeMasterDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "open")
    public static class MasterDeckViewScreen_Open
    {
        @SpirePrefixPatch
        public static void Prefix(MasterDeckViewScreen __instance)
        {
            GetFakeMasterDeck();
            EUI.CardFilters.Initialize(__ -> {
                UpdateForFilters();
                if (CardPoolScreen.CustomModule != null) {
                    CardPoolScreen.CustomModule.Open(fakeMasterDeck.group);
                }
            }, fakeMasterDeck.group, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, false);
            UpdateForFilters();
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "update")
    public static class MasterDeckViewScreen_Update
    {
        @SpirePrefixPatch
        public static void Prefix(MasterDeckViewScreen __instance)
        {
            if (!EUI.CardFilters.TryUpdate() && EUI.OpenCardFiltersButton != null) {
                EUI.OpenCardFiltersButton.TryUpdate();
            }
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class MasterDeckViewScreen_Render
    {
        @SpirePrefixPatch
        public static void Prefix(MasterDeckViewScreen __instance, SpriteBatch sb)
        {
            if (!EUI.CardFilters.TryRender(sb)) {
                EUI.OpenCardFiltersButton.TryRender(sb);
            }
        }

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getFieldName().equals("masterDeck"))
                    {
                        m.replace("{ $_ = extendedui.patches.screens.MasterDeckViewScreenPatches.fakeMasterDeck; }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "hideCards")
    public static class MasterDeckViewScreen_HideCards
    {

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(CardGroup.class.getName()) && m.getFieldName().equals("group"))
                    {
                        m.replace("{ $_ = extendedui.patches.screens.MasterDeckViewScreenPatches.fakeMasterDeck.group; }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "updatePositions")
    public static class MasterDeckViewScreen_UpdatePositions
    {

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(CardGroup.class.getName()) && m.getFieldName().equals("group"))
                    {
                        m.replace("{ $_ = extendedui.patches.screens.MasterDeckViewScreenPatches.fakeMasterDeck.group; }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "updateControllerInput")
    public static class MasterDeckViewScreen_UpdateControllerInput
    {

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getFieldName().equals("masterDeck"))
                    {
                        m.replace("{ $_ = extendedui.patches.screens.MasterDeckViewScreenPatches.fakeMasterDeck; }");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = MasterDeckViewScreen.class, method = "updateClicking")
    public static class MasterDeckViewScreen_UpdateClicking
    {

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()) && m.getFieldName().equals("masterDeck"))
                    {
                        m.replace("{ $_ = extendedui.patches.screens.MasterDeckViewScreenPatches.fakeMasterDeck; }");
                    }
                }
            };
        }
    }

    public static CardGroup GetFakeMasterDeck() {
        fakeMasterDeck.clear();
        if (AbstractDungeon.player != null) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                fakeMasterDeck.addToBottom(c);
            }
        }
        return fakeMasterDeck;
    }

    public static void UpdateForFilters() {
        if (CardKeywordFilters.AreFiltersEmpty()) {
            GetFakeMasterDeck();
        }
        else {
            ArrayList<AbstractCard> tempGroup = CardKeywordFilters.ApplyFilters(AbstractDungeon.player.masterDeck.group);
            if (tempGroup.size() > 0) {
                fakeMasterDeck.group = tempGroup;
            }
            else if (!CardKeywordFilters.AreFiltersEmpty()) {
                CardKeywordFilters.CurrentFilters.clear();
                tempGroup = CardKeywordFilters.ApplyFilters(AbstractDungeon.player.masterDeck.group);
                fakeMasterDeck.group = tempGroup.size() > 0 ? tempGroup : GetFakeGroup();
            }
            else {
                fakeMasterDeck.group = GetFakeGroup();
            }
        }
        EUI.CardFilters.Refresh(fakeMasterDeck.group);
    }
}
