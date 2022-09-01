package extendedui.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIGameUtils;

public class RelicLibraryPatches
{
    // Register color for basegame relics
    @SpirePatch(clz = RelicLibrary.class,
            method = "addRed")
    public static class BaseMod_AddRed
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic)
        {
            EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.RED);
        }
    }

    @SpirePatch(clz = RelicLibrary.class,
            method = "addGreen")
    public static class BaseMod_AddGreen
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic)
        {
            EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.GREEN);
        }
    }

    @SpirePatch(clz = RelicLibrary.class,
            method = "addBlue")
    public static class BaseMod_AddBlue
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic)
        {
            EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.BLUE);
        }
    }

    @SpirePatch(clz = RelicLibrary.class,
            method = "addPurple")
    public static class BaseMod_AddPurple
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic)
        {
            EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.PURPLE);
        }
    }

    @SpirePatch(clz = RelicLibrary.class,
            method = "add")
    public static class BaseMod_Add
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic)
        {
            EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.COLORLESS);
        }
    }
}
