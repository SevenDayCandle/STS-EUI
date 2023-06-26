package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIMainMenuPanelButton;
import extendedui.utilities.EUIClassUtils;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen.COMPENDIUM;

public class MenuPanelScreenPatches
{
    private static final int MAX_SIZE = 3;
    public static final ArrayList<EUIMainMenuPanelButton> AVAILABLE_COMPENDIUMS = new ArrayList<>();
    private static EUIButton backButton;
    private static EUIButton nextButton;
    private static int leftMost;

    public static void initialize() {
        AVAILABLE_COMPENDIUMS.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BEIGE, ImageMaster.P_INFO_CARD, MenuPanelScreen.TEXT[9], MenuPanelScreen.TEXT[11], () -> CardCrawlGame.mainMenuScreen.cardLibraryScreen.open()));
        AVAILABLE_COMPENDIUMS.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BLUE, ImageMaster.P_INFO_RELIC, MenuPanelScreen.TEXT[12], MenuPanelScreen.TEXT[14], () -> CardCrawlGame.mainMenuScreen.relicScreen.open()));
        AVAILABLE_COMPENDIUMS.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_RED, ImageMaster.P_INFO_POTION, MenuPanelScreen.TEXT[43], MenuPanelScreen.TEXT[44], () -> CardCrawlGame.mainMenuScreen.potionScreen.open()));
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class MenuPanelScreen_InitializePanels
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(MenuPanelScreen __instance)
        {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == COMPENDIUM) {
                __instance.panels.clear();
                setLeftMost(0);
                for (EUIMainMenuPanelButton button : AVAILABLE_COMPENDIUMS) {
                    button.reset();
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "update")
    public static class MenuPanelScreen_Update
    {
        @SpirePostfixPatch
        public static void postfix(MenuPanelScreen __instance)
        {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == COMPENDIUM) {
                for (int i = 0; i < MAX_SIZE; i++) {
                    int actual = i + leftMost % AVAILABLE_COMPENDIUMS.size();
                    AVAILABLE_COMPENDIUMS.get(actual).reposition(Settings.WIDTH / 2.0F + (i - 1) * 450F * Settings.scale).update();
                }
            }
        }
    }

    public static void setLeftMost(int val) {
        leftMost = val;
    }
}
