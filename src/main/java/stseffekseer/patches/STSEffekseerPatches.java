package stseffekseer.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import stseffekseer.STSEffekseerManager;

public class STSEffekseerPatches {
    @SpirePatch(clz = CardCrawlGame.class, method = "render")
    public static class CardCrawlGame_PleaseDestroyThis
    {
        @SpirePrefixPatch
        public static void Prefix(CardCrawlGame __instance)
        {
            STSEffekseerManager.Update();
        }
    }
}
