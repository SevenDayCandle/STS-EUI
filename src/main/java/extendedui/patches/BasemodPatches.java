package extendedui.patches;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.ui.settings.ExtraModSettingsPanel;
import org.apache.commons.lang3.StringUtils;

public class BasemodPatches {
    // Create EUI variants of Basemod keywords to show in the filter screen
    @SpirePatch(clz = BaseMod.class,
            method = "addKeyword",
            paramtypez = {String.class, String.class, String[].class, String.class})
    public static class BaseMod_AddKeyword {
        @SpirePostfixPatch
        public static void postfix(String modID, String proper, String[] names, String description) {
            // The actual keyword as used in-text is in the format modID:title, with title capitalized and spaces replaced with underscores
            String title = BaseMod.getKeywordUnique(names[0]);
            String tipID = names[0];
            if (title == null) {
                title = names[0];
                tipID = EUIUtils.capitalize(title);
            }

            // ModIDs are prepended with colons
            if (!StringUtils.isEmpty(modID)) {
                tipID = modID + EUIUtils.capitalize(title).replace(" ", "_");
                EUI.tryRegisterTooltip(tipID, modID.substring(0, modID.length() - 1), title, description, names);
            }
            else {
                EUI.tryRegisterTooltip(tipID, null, title, description, names);
            }
        }
    }

    // Register colors for all custom relics added
    @SpirePatch(clz = BaseMod.class,
            method = "addRelic")
    public static class BaseMod_AddRelic {
        @SpirePostfixPatch
        public static void postfix(AbstractRelic relic, RelicType type) {
            switch (type) {
                case RED:
                    EUIGameUtils.addRelicColor(relic.relicId, AbstractCard.CardColor.RED);
                    return;
                case GREEN:
                    EUIGameUtils.addRelicColor(relic.relicId, AbstractCard.CardColor.GREEN);
                    return;
                case BLUE:
                    EUIGameUtils.addRelicColor(relic.relicId, AbstractCard.CardColor.BLUE);
                    return;
                case PURPLE:
                    EUIGameUtils.addRelicColor(relic.relicId, AbstractCard.CardColor.PURPLE);
                    return;
                default:
                    EUIGameUtils.addRelicColor(relic.relicId, AbstractCard.CardColor.COLORLESS);
            }
        }
    }

    // Register colors for all custom relics added
    @SpirePatch(clz = BaseMod.class,
            method = "addRelicToCustomPool")
    public static class BaseMod_AddRelicToCustomPool {
        @SpirePostfixPatch
        public static void postfix(AbstractRelic relic, AbstractCard.CardColor color) {
            EUIGameUtils.addRelicColor(relic.relicId, color);
        }
    }

    // Create mod options menu for extra settings menu
    @SpirePatch(clz = BaseMod.class,
            method = "registerModBadge")
    public static class BaseMod_RegisterModBadge {
        @SpirePostfixPatch
        public static void postfix(Texture t, String name, String author, String desc, ModPanel settingsPanel) {
            ExtraModSettingsPanel.addModList(new ExtraModSettingsPanel.Category(name), settingsPanel);
        }
    }
}
