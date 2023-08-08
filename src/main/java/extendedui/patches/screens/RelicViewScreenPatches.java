package extendedui.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.RelicViewScreen;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.RelicInfo;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

import java.util.ArrayList;

public class RelicViewScreenPatches {
    // THESE NAMES MUST MATCH THE NAMES OF THE RELICLIBRARY LISTS OR THIS PATCH WILL GO BOOM
    private static ArrayList<RelicInfo> allList = new ArrayList<>();
    public static ArrayList<AbstractRelic> starterList = new ArrayList<>();
    public static ArrayList<AbstractRelic> commonList = new ArrayList<>();
    public static ArrayList<AbstractRelic> uncommonList = new ArrayList<>();
    public static ArrayList<AbstractRelic> rareList = new ArrayList<>();
    public static ArrayList<AbstractRelic> bossList = new ArrayList<>();
    public static ArrayList<AbstractRelic> specialList = new ArrayList<>();
    public static ArrayList<AbstractRelic> shopList = new ArrayList<>();

    private static void editImpl(javassist.expr.FieldAccess m) throws CannotCompileException {
        if (m.getClassName().equals(RelicLibrary.class.getName())) {
            m.replace("{ $_ = extendedui.patches.screens.RelicViewScreenPatches." + m.getFieldName() + "; }");
        }
    }

    public static ArrayList<AbstractRelic> getAllReics() {
        reset();
        return EUIUtils.flatten(starterList, commonList, uncommonList, rareList, bossList, specialList, shopList);
    }

    private static void reset() {
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

    private static void resetAllList() {
        allList = EUIUtils.mapAll(RelicInfo::new, starterList, commonList, uncommonList, rareList, bossList, specialList, shopList);
    }

    private static void updateForFilters() {

        if (EUI.relicFilters.areFiltersEmpty()) {
            reset();
        }
        else {
            starterList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.starterList);
            commonList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.commonList);
            uncommonList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.uncommonList);
            rareList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.rareList);
            bossList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.bossList);
            specialList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.specialList);
            shopList = EUI.relicFilters.applyFiltersToRelics(RelicLibrary.shopList);
            resetAllList();
        }
        EUI.relicFilters.refresh(allList);
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "open")
    public static class RelicViewScreen_Open {
        @SpirePostfixPatch
        public static void postfix(RelicViewScreen screen) {
            reset();

            EUI.relicFilters.initialize(__ -> updateForFilters()
                    , allList
                    , AbstractCard.CardColor.COLORLESS
                    , false);
            updateForFilters();
            EUI.openFiltersButton.setOnClick(() -> EUI.relicFilters.toggleFilters());
            EUIExporter.exportButton.setOnClick(() -> EUIExporter.relicExportable.openAndPosition(allList));
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "update")
    public static class RelicViewScreen_Update {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }

        @SpirePrefixPatch
        public static void prefix(RelicViewScreen __instance) {
            // Export relic button will only be null if open relic button is also null
            if (!EUI.relicFilters.isActive && EUI.openFiltersButton != null) {
                EUI.openFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
            }
            // Make sure both items update, but only one needs to be pass
            if (EUI.relicFilters.tryUpdate() | EUIExporter.exportDropdown.tryUpdate()) {
                EUIClassUtils.setField(__instance, "grabbedScreen", false);
            }
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateControllerInput")
    public static class RelicViewScreen_UpdateControllerInput {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "updateScrolling")
    public static class RelicViewScreen_UpdateScrolling {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(RelicViewScreen __instance) {
            if (EUI.relicFilters.isActive) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = RelicViewScreen.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RelicViewScreen_Render {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }

        @SpirePrefixPatch
        public static void postfix(RelicViewScreen __instance, SpriteBatch sb) {
            // Export relic button will only be null if open relic button is also null
            if (!EUI.relicFilters.isActive && EUI.openFiltersButton != null) {
                EUI.openFiltersButton.tryRender(sb);
                EUIExporter.exportButton.tryRender(sb);
            }
        }
    }

}
