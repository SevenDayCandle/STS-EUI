package extendedui.patches;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIGameUtils;

public class BasemodPatches
{
    // Create EUI variants of Basemod keywords to show in the relic filter screen
    @SpirePatch(clz = BaseMod.class,
            method = "addKeyword",
            paramtypez = {String.class, String.class, String[].class, String.class})
    public static class BaseMod_AddKeyword
    {
        @SpirePostfixPatch
        public static void Postfix(String modID, String proper, String[] names, String description)
        {
            String title = BaseMod.getKeywordUnique(names[0]);
            if (title == null) {
                title = names[0];
            }
            EUI.TryRegisterTooltip(names[0], title, description, names);
        }
    }

    // Register colors for all custom relics added
    @SpirePatch(clz = BaseMod.class,
            method = "addRelic")
    public static class BaseMod_AddRelic
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic, RelicType type)
        {
            switch (type)
            {
                case RED:
                    EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.RED);
                    return;
                case GREEN:
                    EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.GREEN);
                    return;
                case BLUE:
                    EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.BLUE);
                    return;
                case PURPLE:
                    EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.PURPLE);
                    return;
                default:
                    EUIGameUtils.AddRelicColor(relic, AbstractCard.CardColor.COLORLESS);
            }
        }
    }

    // Register colors for all custom relics added
    @SpirePatch(clz = BaseMod.class,
            method = "addRelicToCustomPool")
    public static class BaseMod_AddRelicToCustomPool
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic relic, AbstractCard.CardColor color)
        {
            EUIGameUtils.AddRelicColor(relic, color);
        }
    }
}
