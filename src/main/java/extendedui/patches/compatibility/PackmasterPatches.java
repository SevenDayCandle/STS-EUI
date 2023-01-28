package extendedui.patches.compatibility;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.UIStrings;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.SimpleCardFilterModule;
import extendedui.utilities.EUIClassUtils;

public class PackmasterPatches
{
    @SpirePatch(cls = "thePackmaster.SpireAnniversary5Mod", method = "receivePostInitialize", requiredModId = "anniv5", optional = true)
    public static class PackmasterPatches_PostInitialize
    {

        @SpirePostfixPatch
        public static void postfix()
        {
            try
            {
                AbstractCard.CardColor packMasterColor = AbstractCard.CardColor.valueOf("PACKMASTER_RAINBOW");
                UIStrings uiStrings = EUIClassUtils.getRFieldStatic("thePackmaster.packs.AbstractPackPreviewCard", "UI_STRINGS");
                EUI.setCustomCardFilter(packMasterColor,
                        new SimpleCardFilterModule<Object>(EUI.cardFilters, uiStrings.TEXT[1], t -> {
                            try
                            {
                                return EUIClassUtils.getRField("thePackmaster.packs.AbstractCardPack", "name", t);
                            }
                            catch (Exception ignored)
                            {
                                return "";
                            }
                        }, card -> {
                            try
                            {
                                return EUIClassUtils.invokeR("thePackmaster.cards.AbstractPackmasterCard", "getParent", card);
                            }
                            catch (Exception ignored)
                            {
                                return null;
                            }
                        }));
            }
            catch (Exception e)
            {
                EUIUtils.logWarning(null, "OH NOES");
                e.printStackTrace();
            }
        }
    }
}
