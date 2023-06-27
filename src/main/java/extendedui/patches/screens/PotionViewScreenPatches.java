package extendedui.patches.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.PotionViewScreen;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.PotionInfo;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

import java.util.ArrayList;

public class PotionViewScreenPatches {
    public static ArrayList<AbstractPotion> commonPotions = new ArrayList<>();
    public static ArrayList<AbstractPotion> uncommonPotions = new ArrayList<>();
    public static ArrayList<AbstractPotion> rarePotions = new ArrayList<>();
    // THESE NAMES MUST MATCH THE NAMES OF THE POTIONVIEWSCREEN LISTS OR THIS PATCH WILL GO BOOM
    private static ArrayList<PotionInfo> allList = new ArrayList<>();

    private static void editImpl(javassist.expr.FieldAccess m) throws CannotCompileException {
        switch (m.getFieldName()) {
            case "commonPotions":
            case "uncommonPotions":
            case "rarePotions":
                m.replace("{ $_ = extendedui.patches.screens.PotionViewScreenPatches." + m.getFieldName() + "; }");
                break;
        }
    }

    private static void updateForFilters(PotionViewScreen screen) {

        if (EUI.potionFilters.areFiltersEmpty()) {
            reset(screen);
        }
        else {
            commonPotions = EUI.potionFilters.applyFiltersToPotions(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "commonPotions"));
            uncommonPotions = EUI.potionFilters.applyFiltersToPotions(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "uncommonPotions"));
            rarePotions = EUI.potionFilters.applyFiltersToPotions(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "rarePotions"));
            resetAllList();
        }
        EUI.potionFilters.refresh(allList);
    }

    private static void reset(PotionViewScreen screen) {
        commonPotions.clear();
        uncommonPotions.clear();
        rarePotions.clear();

        commonPotions.addAll(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "commonPotions"));
        uncommonPotions.addAll(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "uncommonPotions"));
        rarePotions.addAll(ReflectionHacks.getPrivate(screen, PotionViewScreen.class, "rarePotions"));

        resetAllList();
    }

    private static void resetAllList() {
        allList = EUIUtils.mapAll(PotionInfo::new, commonPotions, uncommonPotions, rarePotions);
    }

    @SpirePatch(clz = PotionViewScreen.class, method = "open")
    public static class PotionViewScreen_Open {
        @SpirePostfixPatch
        public static SpireReturn<Void> postfix(PotionViewScreen screen) {
            reset(screen);
            EUI.potionFilters.initialize(__ -> updateForFilters(screen)
                    , allList
                    , AbstractCard.CardColor.COLORLESS
                    , false);
            updateForFilters(screen);
            EUIExporter.exportPotionButton.setOnClick(() -> EUIExporter.openForPotions(allList));

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionViewScreen.class, method = "update")
    public static class PotionViewScreen_Update {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }

        @SpirePrefixPatch
        public static void prefix(PotionViewScreen __instance) {
            if (!EUI.potionFilters.isActive && EUI.openPotionFiltersButton != null) {
                EUI.openPotionFiltersButton.tryUpdate();
                EUIExporter.exportPotionButton.tryUpdate();
            }
            // Make sure both items update, but only one needs to be pass
            if (EUI.potionFilters.tryUpdate() | EUIExporter.exportDropdown.tryUpdate()) {
                EUIClassUtils.setField(__instance, "grabbedScreen", false);
            }
        }
    }

    @SpirePatch(clz = PotionViewScreen.class, method = "updateControllerInput")
    public static class PotionViewScreen_UpdateControllerInput {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }
    }

    @SpirePatch(clz = PotionViewScreen.class, method = "updateScrolling")
    public static class PotionViewScreen_UpdateScrolling {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(PotionViewScreen __instance) {
            if (EUI.potionFilters.isActive) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionViewScreen.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class PotionViewScreen_Render {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    editImpl(m);
                }
            };
        }

        @SpirePrefixPatch
        public static void postfix(PotionViewScreen __instance, SpriteBatch sb) {
            if (!EUI.potionFilters.isActive && EUI.openPotionFiltersButton != null) {
                EUI.openPotionFiltersButton.tryRender(sb);
                EUIExporter.exportPotionButton.tryRender(sb);
            }
        }
    }

}
