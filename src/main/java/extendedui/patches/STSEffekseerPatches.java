package extendedui.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.STSEffekseerManager;
import org.apache.logging.log4j.LogManager;

public class STSEffekseerPatches {
    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_Render
    {
        @SpirePrefixPatch
        public static void Prefix(CardCrawlGame __instance)
        {
            STSEffekseerManager.Update();
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "dispose")
    public static class CardCrawlGame_Dispose
    {
        @SpirePostfixPatch
        public static void Postfix(CardCrawlGame __instance)
        {
            STSEffekseerManager.End();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Terminated STSEffekseerManager");
        }
    }
}
