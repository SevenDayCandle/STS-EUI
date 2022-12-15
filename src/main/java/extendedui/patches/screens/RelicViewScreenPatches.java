package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
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
        public static SpireReturn<Void> postfix(RelicViewScreen screen)
        {
            reset();

            EUI.relicFilters.initialize(__ -> updateForFilters()
                    , allList
                    , AbstractCard.CardColor.COLORLESS
                    , false);
            updateForFilters();

            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz= RelicViewScreen.class, method="update")
    public static class RelicViewScreen_Update
    {

        @SpirePrefixPatch
        public static void prefix(RelicViewScreen __instance)
        {
            if (!EUI.relicFilters.isActive && EUI.openRelicFiltersButton != null) {
                EUI.openRelicFiltersButton.tryUpdate();
            }
            if (EUI.relicFilters.tryUpdate())
            {
                EUIClassUtils.setField(__instance, "grabbedScreen", false);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    editImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateControllerInput")
    public static class RelicViewScreen_UpdateControllerInput
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    editImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateScrolling")
    public static class RelicViewScreen_UpdateScrolling
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(RelicViewScreen __instance)
        {
            if (EUI.relicFilters.isActive) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz= RelicViewScreen.class, method="render", paramtypez = {SpriteBatch.class})
    public static class RelicViewScreen_Render
    {
        @SpirePrefixPatch
        public static void postfix(RelicViewScreen __instance, SpriteBatch sb)
        {
            if (!EUI.relicFilters.isActive && EUI.openRelicFiltersButton != null) {
                EUI.openRelicFiltersButton.tryRender(sb);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    editImpl(m);
                }
            };
        }
    }

    private static void updateForFilters() {

        if (EUI.relicFilters.areFiltersEmpty()) {
            reset();
        }
        else {
            starterList = EUI.relicFilters.applyFilters(RelicLibrary.starterList);
            commonList = EUI.relicFilters.applyFilters(RelicLibrary.commonList);
            uncommonList = EUI.relicFilters.applyFilters(RelicLibrary.uncommonList);
            rareList = EUI.relicFilters.applyFilters(RelicLibrary.rareList);
            bossList = EUI.relicFilters.applyFilters(RelicLibrary.bossList);
            specialList = EUI.relicFilters.applyFilters(RelicLibrary.specialList);
            shopList = EUI.relicFilters.applyFilters(RelicLibrary.shopList);
            resetAllList();
        }
        EUI.relicFilters.refresh(allList);
    }

    private static void reset()
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

        resetAllList();
    }

    private static void resetAllList()
    {
        allList = EUIUtils.flatten(starterList, commonList, uncommonList, rareList, bossList, specialList, shopList);
    }

    private static void editImpl(javassist.expr.FieldAccess m) throws CannotCompileException
    {
        if (m.getClassName().equals(RelicLibrary.class.getName()))
        {
            m.replace("{ $_ = extendedui.patches.screens.RelicViewScreenPatches." + m.getFieldName() + "; }");
        }
    }

}
