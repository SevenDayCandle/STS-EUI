package stseffekseer.patches.screens;

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
import stseffekseer.EUI;
import stseffekseer.EUIRM;
import stseffekseer.JavaUtils;
import stseffekseer.ui.controls.GUI_Button;
import stseffekseer.ui.hitboxes.DraggableHitbox;
import stseffekseer.utilities.FieldInfo;

public class CardLibraryScreenPatches
{
    private static final FieldInfo<AbstractCard> hoveredCards = JavaUtils.GetField("hoveredCard", CardLibraryScreen.class);
    private static final FieldInfo<ColorTabBar> _colorBar = JavaUtils.GetField("colorBar", CardLibraryScreen.class);
    private static GUI_Button openButton;
    @SpirePatch(clz = CardLibraryScreen.class, method = "open")
    public static class CardLibraryScreen_Open
    {

        @SpirePrefixPatch
        public static void Prefix(CardLibraryScreen screen)
        {
            ColorTabBar tabBar = _colorBar.Get(screen);
            if (tabBar.curTab != ColorTabBarFix.Enums.MOD)
            {
                screen.didChangeTab(tabBar, tabBar.curTab = ColorTabBarFix.Enums.MOD);
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
            EUI.CardFilters.Initialize(__ -> EUI.CustomHeader.UpdateForFilters(), EUI.CustomHeader.originalGroup);
            EUI.CustomHeader.UpdateForFilters();
            if (openButton == null) {
                openButton = new GUI_Button(EUIRM.Images.HexagonalButton.Texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).SetIsPopupCompatible(true))
                        .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.WHITE)
                        .SetPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.95f).SetText(EUIRM.Strings.UI_Filters)
                        .SetOnClick(() -> {
                            if (EUI.CardFilters.isActive) {
                                EUI.CardFilters.Close();
                            }
                            else {
                                EUI.CardFilters.Open();
                            }
                        })
                        .SetColor(Color.GRAY);
            }
        }
    }

    @SpirePatch(clz= CardLibraryScreen.class, method="update")
    public static class CardLibraryScreen_Update
    {
        private static FieldInfo<Boolean> _grabbedScreen = JavaUtils.GetField("grabbedScreen", CardLibraryScreen.class);

        @SpirePrefixPatch
        public static void Prefix(CardLibraryScreen __instance)
        {
            if (!EUI.CardFilters.isActive && openButton != null && !IsAnimator(__instance)) {
                openButton.TryUpdate();
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
            if (!EUI.CardFilters.isActive && openButton != null && !IsAnimator(__instance)) {
                openButton.TryRender(sb);
            }
        }
    }

    protected static boolean IsAnimator(CardLibraryScreen screen) {
        ColorTabBar tabBar = _colorBar.Get(screen);
        return tabBar != null && tabBar.curTab == ColorTabBarFix.Enums.MOD && "THE_ANIMATOR".equals(ColorTabBarFix.Fields.getModTab().color.name());
    }
}