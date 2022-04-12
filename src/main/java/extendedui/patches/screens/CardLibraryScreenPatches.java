package extendedui.patches.screens;

import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.utilities.FieldInfo;

import java.util.ArrayList;

public class CardLibraryScreenPatches
{
    private static final FieldInfo<AbstractCard> hoveredCards = JavaUtils.GetField("hoveredCard", CardLibraryScreen.class);
    private static final FieldInfo<ColorTabBar> _colorBar = JavaUtils.GetField("colorBar", CardLibraryScreen.class);
    private static final FieldInfo<ArrayList<ColorTabBarFix.ModColorTab>> _tabs = JavaUtils.GetField("modTabs", ColorTabBarFix.Fields.class);
    @SpirePatch(clz = CardLibraryScreen.class, method = "open")
    public static class CardLibraryScreen_Open
    {

        @SpirePrefixPatch
        public static void Prefix(CardLibraryScreen screen)
        {
            ColorTabBar tabBar = _colorBar.Get(screen);
            int size = _tabs.Get(null).size();
            if (tabBar.curTab != ColorTabBarFix.Enums.MOD)
            {
                screen.didChangeTab(tabBar, tabBar.curTab = (size > 0 ? ColorTabBarFix.Enums.MOD : ColorTabBar.CurrentTab.COLORLESS));
            }
        }
    }

    @SpirePatch(clz = CardLibraryScreen.class, method = "didChangeTab", paramtypez = {ColorTabBar.class, ColorTabBar.CurrentTab.class})
    public static class CardLibraryScreen_DidChangeTab
    {
        private static final FieldInfo<CardLibSortHeader> _sortHeader = JavaUtils.GetField("sortHeader", CardLibraryScreen.class);
        private static CardLibSortHeader defaultHeader;

        @SpireInsertPatch(rloc = 0)
        public static void Insert(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection)
        {
            if (!IsAnimator(screen)) {
                Hitbox upgradeHitbox = tabBar.viewUpgradeHb;
                upgradeHitbox.width = 260 * Settings.scale;
                if (_sortHeader.Get(screen) != EUI.CustomHeader)
                {
                    _sortHeader.Set(screen, EUI.CustomHeader);
                }

                EUI.CustomHeader.SetupButtons();
            }
        }

        @SpirePostfixPatch
        public static void Postfix(CardLibraryScreen screen, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {
            EUI.CardFilters.Initialize(__ -> EUI.CustomHeader.UpdateForFilters(), EUI.CustomHeader.originalGroup, newSelection == ColorTabBarFix.Enums.MOD ? ColorTabBarFix.Fields.getModTab().color : AbstractCard.CardColor.COLORLESS);
            EUI.CustomHeader.UpdateForFilters();
        }
    }

    @SpirePatch(clz= CardLibraryScreen.class, method="update")
    public static class CardLibraryScreen_Update
    {
        private static FieldInfo<Boolean> _grabbedScreen = JavaUtils.GetField("grabbedScreen", CardLibraryScreen.class);

        @SpirePrefixPatch
        public static void Prefix(CardLibraryScreen __instance)
        {
            if (!EUI.CardFilters.isActive && EUI.OpenCardFiltersButton != null && !IsAnimator(__instance)) {
                EUI.OpenCardFiltersButton.TryUpdate();
            }
            if (EUI.CardFilters.TryUpdate())
            {
                _grabbedScreen.Set(__instance, false);
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
            // The Animator has its own card filters so it is given priority to prevent overlap
            if (!EUI.CardFilters.isActive && EUI.OpenCardFiltersButton != null && !IsAnimator(__instance)) {
                EUI.OpenCardFiltersButton.TryRender(sb);
            }
        }
    }

    protected static boolean IsAnimator(CardLibraryScreen screen) {
        ColorTabBar tabBar = _colorBar.Get(screen);
        int size = _tabs.Get(null).size();
        return tabBar != null && size > 0
                && tabBar.curTab == ColorTabBarFix.Enums.MOD
                && "THE_ANIMATOR".equals(ColorTabBarFix.Fields.getModTab().color.name());
    }
}