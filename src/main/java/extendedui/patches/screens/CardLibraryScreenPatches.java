package extendedui.patches.screens;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.FakeLibraryCard;

import java.util.ArrayList;

public class CardLibraryScreenPatches {
    private static CardLibSortHeader header;
    private static FakeLibraryCard fakeLibraryCard;
    private static ArrayList<AbstractCard> fakeGroup;

    // The fake group tells players that nothing can be found. It also prevents crashing from empty cardGroups without the need for patching
    public static ArrayList<AbstractCard> getFakeGroup() {
        if (fakeLibraryCard == null) {
            fakeLibraryCard = new FakeLibraryCard();
        }
        if (fakeGroup == null) {
            fakeGroup = new ArrayList<>();
            fakeGroup.add(fakeLibraryCard);
        }
        return fakeGroup;
    }

    private static void updateForFilters() {
        if (header != null && header.group != null) {
            if (EUI.cardFilters.areFiltersEmpty()) {
                header.group.group = EUI.cardFilters.getOriginalGroup();
            }
            else {
                ArrayList<AbstractCard> tempGroup = EUI.cardFilters.applyFilters(EUI.cardFilters.getOriginalGroup());
                if (!tempGroup.isEmpty()) {
                    header.group.group = tempGroup;
                }
                else {
                    header.group.group = getFakeGroup();
                }
            }
            SortHeaderButton button = EUIUtils.find(header.buttons, b -> EUIClassUtils.getField(b, "isActive"));
            if (button != null) {
                boolean ascending = EUIClassUtils.getField(button, "isAscending");
                header.didChangeOrder(button, ascending);
            }
            EUI.cardFilters.manualInvalidate(header.group.group);
        }
    }

    @SpirePatch(
            clz = CardLibraryScreen.class,
            method = "initialize"
    )
    public static class CardLibraryScreen_Initialize {
        @SpirePostfixPatch
        public static void postfix(CardLibraryScreen screen) {
            // Must perform initialization right after card library groups are first initialized
            EUI.customLibraryScreen.initialize(screen);
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "open")
    public static class CardLibraryScreen_Open {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CardLibraryScreen screen) {
            // Redirect to the custom library screen if enabled
            if (!EUIConfiguration.useVanillaCompendium.get()) {
                EUI.customLibraryScreen.openImpl();
                CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.CARD_LIBRARY;
                return SpireReturn.Return();
            }

            ColorTabBar tabBar = EUIClassUtils.getField(screen, "colorBar");
            ArrayList<ColorTabBarFix.ModColorTab> tabs = ReflectionHacks.getPrivateStatic(ColorTabBarFix.Fields.class, "modTabs");
            if (tabBar.curTab != ColorTabBarFix.Enums.MOD) {
                screen.didChangeTab(tabBar, tabBar.curTab = (!tabs.isEmpty() ? ColorTabBarFix.Enums.MOD : ColorTabBar.CurrentTab.COLORLESS));
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "didChangeTab", paramtypez = {ColorTabBar.class, ColorTabBar.CurrentTab.class})
    public static class CardLibraryScreen_DidChangeTab {
        private static CardLibSortHeader defaultHeader;

        // TODO re-add custom buttons here
/*        @SpirePrefixPatch
        public static void insert(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {

        }*/

        @SpirePostfixPatch
        public static void postfix(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {
            header = EUIClassUtils.getField(screen, "sortHeader");
            EUI.cardFilters.initialize(__ -> updateForFilters(), header.group.group, newSelection == ColorTabBarFix.Enums.MOD ? ColorTabBarFix.Fields.getModTab().color : AbstractCard.CardColor.COLORLESS, false);
            EUI.openFiltersButton.setOnClick(() -> EUI.cardFilters.toggleFilters());
            updateForFilters();
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "update")
    public static class CardLibraryScreen_Update {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CardLibraryScreen __instance) {
            // Override vanilla compendium if enabled
            if (!EUIConfiguration.useVanillaCompendium.get()) {
                EUI.customLibraryScreen.updateImpl();
                return SpireReturn.Return();
            }

            if (!EUI.cardFilters.isActive && EUI.openFiltersButton != null) {
                EUI.openFiltersButton.tryUpdate();
                EUIExporter.exportButton.tryUpdate();
            }
            // Ensure that both update, but only one needs to have updated for this to pass
            if (EUI.cardFilters.tryUpdate() | EUIExporter.exportDropdown.tryUpdate()) {
                EUIClassUtils.setField(__instance, "grabbedScreen", false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "updateScrolling")
    public static class CardLibraryScreen_UpdateScrolling {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardLibraryScreen __instance) {
            if (EUI.cardFilters.isActive) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class CardLibraryScreen_Render {
        @SpirePrefixPatch
        public static void postfix(CardLibraryScreen __instance, SpriteBatch sb) {
            if (!EUI.cardFilters.isActive && EUI.openFiltersButton != null) {
                EUI.openFiltersButton.tryRender(sb);
                EUIExporter.exportButton.tryRender(sb);
            }
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CardLibraryScreen __instance, SpriteBatch sb) {
            // Override vanilla compendium if enabled
            if (!EUIConfiguration.useVanillaCompendium.get()) {
                EUI.customLibraryScreen.renderImpl(sb);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}