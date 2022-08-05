package extendedui.patches;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import extendedui.EUI;

public class BasemodPatches
{
    @SpirePatch(clz = BaseMod.class,
            method = "addKeyword",
            paramtypez = {String.class, String.class, String[].class, String.class})
    public static class AbstractPlayer_PreRender
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
}
