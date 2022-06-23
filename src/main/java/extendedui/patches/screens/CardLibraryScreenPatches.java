package extendedui.patches.screens;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import extendedui.EUI;
import extendedui.JavaUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.utilities.ClassUtils;

import java.util.ArrayList;

public class CardLibraryScreenPatches
{
    @SpirePatch(
            clz = CardLibraryScreen.class,
            method = "initialize"
    )
    public static class CardLibraryScreen_Initialize {
        @SpirePostfixPatch
        public static void Postfix(CardLibraryScreen screen)
        {
            // Must perform initialization right after card library groups are first initialized
            EUI.CustomLibraryScreen.Initialize(screen);
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "open")
    public static class CardLibraryScreen_Open
    {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(CardLibraryScreen screen)
        {
            // Redirect to the custom library screen if enabled
            if (!EUIConfiguration.UseVanillaCompendium.Get()) {
                EUI.CustomLibraryScreen.Open();
                return SpireReturn.Return();
            }

            ColorTabBar tabBar = ClassUtils.GetField(screen, "colorBar");
            ArrayList<ColorTabBarFix.ModColorTab> tabs = ReflectionHacks.getPrivateStatic(ColorTabBarFix.Fields.class, "modTabs");
            if (tabBar.curTab != ColorTabBarFix.Enums.MOD)
            {
                screen.didChangeTab(tabBar, tabBar.curTab = (tabs.size() > 0 ? ColorTabBarFix.Enums.MOD : ColorTabBar.CurrentTab.COLORLESS));
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "didChangeTab", paramtypez = {ColorTabBar.class, ColorTabBar.CurrentTab.class})
    public static class CardLibraryScreen_DidChangeTab
    {
        private static CardLibSortHeader defaultHeader;

        @SpireInsertPatch(rloc = 0)
        public static void Insert(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection)
        {
            Hitbox upgradeHitbox = tabBar.viewUpgradeHb;
            upgradeHitbox.width = 260 * Settings.scale;

            if (ClassUtils.GetField(screen, "sortHeader") != EUI.CustomHeader)
            {
                ClassUtils.SetField(screen, "sortHeader", EUI.CustomHeader);
            }

            EUI.CustomHeader.SetupButtons();
        }

        @SpirePostfixPatch
        public static void Postfix(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {
            EUI.CardFilters.Initialize(__ -> EUI.CustomHeader.UpdateForFilters(), EUI.CustomHeader.originalGroup, newSelection == ColorTabBarFix.Enums.MOD ? ColorTabBarFix.Fields.getModTab().color : AbstractCard.CardColor.COLORLESS, false);
            EUI.CustomHeader.UpdateForFilters();
        }
    }

    @SpirePatch(clz= CardLibraryScreen.class, method="update")
    public static class CardLibraryScreen_Update
    {

        @SpirePrefixPatch
        public static void Prefix(CardLibraryScreen __instance)
        {
            if (!EUI.CardFilters.isActive && EUI.OpenCardFiltersButton != null) {
                EUI.OpenCardFiltersButton.TryUpdate();
            }
            if (EUI.CardFilters.TryUpdate())
            {
                ClassUtils.SetField(__instance, "grabbedScreen", false);
            }
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "updateScrolling")
    public static class CardLibraryScreen_UpdateScrolling
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> Prefix(CardLibraryScreen __instance)
        {
            if (EUI.CardFilters.TryUpdate()) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz= CardLibraryScreen.class, method="render", paramtypez = {SpriteBatch.class})
    public static class CardLibraryScreen_Render
    {
        @SpirePrefixPatch
        public static void Postfix(CardLibraryScreen __instance, SpriteBatch sb)
        {
            if (!EUI.CardFilters.isActive && EUI.OpenCardFiltersButton != null) {
                EUI.OpenCardFiltersButton.TryRender(sb);
            }
        }
    }
}