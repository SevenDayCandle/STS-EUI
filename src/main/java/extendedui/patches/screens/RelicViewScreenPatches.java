package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.RelicViewScreen;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

import java.util.ArrayList;

public class RelicViewScreenPatches
{
    private static ArrayList<AbstractRelic> allList = new ArrayList<>();
    public static ArrayList<AbstractRelic> starterList = new ArrayList<>();
    public static ArrayList<AbstractRelic> commonList = new ArrayList<>();
    public static ArrayList<AbstractRelic> uncommonList = new ArrayList<>();
    public static ArrayList<AbstractRelic> rareList = new ArrayList<>();
    public static ArrayList<AbstractRelic> bossList = new ArrayList<>();
    public static ArrayList<AbstractRelic> specialList = new ArrayList<>();
    public static ArrayList<AbstractRelic> shopList = new ArrayList<>();

    @SpirePatch(clz = RelicViewScreen.class, method = "open")
    public static class RelicViewScreen_Open
    {
        @SpirePostfixPatch
        public static SpireReturn<Void> Postfix(RelicViewScreen screen)
        {
            Reset();

            EUI.RelicFilters.Initialize(__ -> UpdateForFilters()
                    , allList
                    , AbstractCard.CardColor.COLORLESS
                    , false);
            UpdateForFilters();

            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz= RelicViewScreen.class, method="update")
    public static class RelicViewScreen_Update
    {

        @SpirePrefixPatch
        public static void Prefix(RelicViewScreen __instance)
        {
            if (!EUI.RelicFilters.isActive && EUI.OpenRelicFiltersButton != null) {
                EUI.OpenRelicFiltersButton.TryUpdate();
            }
            if (EUI.RelicFilters.TryUpdate())
            {
                EUIClassUtils.SetField(__instance, "grabbedScreen", false);
            }
        }

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    EditImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateControllerInput")
    public static class RelicViewScreen_UpdateControllerInput
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    EditImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateScrolling")
    public static class RelicViewScreen_UpdateScrolling
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> Prefix(RelicViewScreen __instance)
        {
            if (EUI.RelicFilters.isActive) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz= RelicViewScreen.class, method="render", paramtypez = {SpriteBatch.class})
    public static class RelicViewScreen_Render
    {
        @SpirePrefixPatch
        public static void Postfix(RelicViewScreen __instance, SpriteBatch sb)
        {
            if (!EUI.RelicFilters.isActive && EUI.OpenRelicFiltersButton != null) {
                EUI.OpenRelicFiltersButton.TryRender(sb);
            }
        }

        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    EditImpl(m);
                }
            };
        }
    }

    public static void UpdateForFilters() {

        if (EUI.RelicFilters.AreFiltersEmpty()) {
            Reset();
        }
        else {
            starterList = EUI.RelicFilters.ApplyFilters(RelicLibrary.starterList);
            commonList = EUI.RelicFilters.ApplyFilters(RelicLibrary.commonList);
            uncommonList = EUI.RelicFilters.ApplyFilters(RelicLibrary.uncommonList);
            rareList = EUI.RelicFilters.ApplyFilters(RelicLibrary.rareList);
            bossList = EUI.RelicFilters.ApplyFilters(RelicLibrary.bossList);
            specialList = EUI.RelicFilters.ApplyFilters(RelicLibrary.specialList);
            shopList = EUI.RelicFilters.ApplyFilters(RelicLibrary.shopList);
            ResetAllList();
        }
        EUI.RelicFilters.Refresh(allList);
    }

    private static void Reset()
    {
        starterList.clear();
        commonList.clear();
        uncommonList.clear();
        rareList.clear();
        bossList.clear();
        specialList.clear();
        shopList.clear();

        starterList.addAll(RelicLibrary.starterList);
        commonList.addAll(RelicLibrary.commonList);
        uncommonList.addAll(RelicLibrary.uncommonList);
        rareList.addAll(RelicLibrary.rareList);
        bossList.addAll(RelicLibrary.bossList);
        specialList.addAll(RelicLibrary.specialList);
        shopList.addAll(RelicLibrary.shopList);

        ResetAllList();
    }

    private static void ResetAllList()
    {
        allList = EUIUtils.Flatten(starterList, commonList, uncommonList, rareList, bossList, specialList, shopList);
    }

    private static void EditImpl(javassist.expr.FieldAccess m) throws CannotCompileException
    {
        if (m.getClassName().equals(RelicLibrary.class.getName()))
        {
            m.replace("{ $_ = extendedui.patches.screens.RelicViewScreenPatches." + m.getFieldName() + "; }");
        }
    }

}
