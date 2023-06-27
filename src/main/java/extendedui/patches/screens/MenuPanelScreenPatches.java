package extendedui.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIMainMenuPanelButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen.COMPENDIUM;

public class MenuPanelScreenPatches
{
    private static final int MAX_SIZE = 3;
    private static final float START_X = Settings.WIDTH * 0.5f - 450f * Settings.scale;
    private static final ArrayList<EUIMainMenuPanelButton> AVAILABLE_COMPENDIUMS = new ArrayList<>();
    private static EUIButton nextButton;
    private static EUIButton prevButton;
    private static int leftMost;

    public static void initialize() {
        registerCompendiumPanel(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BEIGE, ImageMaster.P_INFO_CARD, MenuPanelScreen.TEXT[9], MenuPanelScreen.TEXT[11], () -> CardCrawlGame.mainMenuScreen.cardLibraryScreen.open()));
        registerCompendiumPanel(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BLUE, ImageMaster.P_INFO_RELIC, MenuPanelScreen.TEXT[12], MenuPanelScreen.TEXT[14], () -> CardCrawlGame.mainMenuScreen.relicScreen.open()));
        registerCompendiumPanel(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_RED, ImageMaster.P_INFO_POTION, MenuPanelScreen.TEXT[43], MenuPanelScreen.TEXT[44], () -> CardCrawlGame.mainMenuScreen.potionScreen.open()));
        registerCompendiumPanel(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_RED, ImageMaster.P_INFO_POTION, EUIRM.strings.uipool_blightPanel, EUIRM.strings.uipool_blightPanelDesc, () -> CardCrawlGame.mainMenuScreen.potionScreen.open()));
        nextButton = new EUIButton(ImageMaster.POPUP_ARROW, new EUIHitbox( Settings.WIDTH * 0.8f,  Settings.HEIGHT / 2f, 160f * Settings.scale, 160f * Settings.scale))
                .setOnClick(MenuPanelScreenPatches::addLeftMost);
        prevButton = new EUIButton(ImageMaster.POPUP_ARROW, new EUIHitbox(Settings.WIDTH * 0.2f, Settings.HEIGHT / 2f, 160f * Settings.scale, 160f * Settings.scale))
                .setButtonRotation(180)
                .setOnClick(MenuPanelScreenPatches::decreaseLeftMost);
    }

    public static void registerCompendiumPanel(EUIMainMenuPanelButton button) {
        AVAILABLE_COMPENDIUMS.add(button);
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
                for (int i = leftMost; i < Math.min(AVAILABLE_COMPENDIUMS.size(), leftMost + MAX_SIZE); i++) {
                    AVAILABLE_COMPENDIUMS.get(i).reposition(START_X + (i - leftMost) * 450F * Settings.scale).update();
                }
                nextButton.update();
                prevButton.update();
            }
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "render")
    public static class MenuPanelScreen_Render
    {
        @SpirePostfixPatch
        public static void postfix(MenuPanelScreen __instance, SpriteBatch sb)
        {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == COMPENDIUM) {
                for (int i = leftMost; i < Math.min(AVAILABLE_COMPENDIUMS.size(), leftMost + MAX_SIZE); i++) {
                    AVAILABLE_COMPENDIUMS.get(i).render(sb);
                }
                nextButton.renderCentered(sb);
                prevButton.renderCentered(sb);
            }
        }
    }

    public static void addLeftMost() {
        setLeftMost(leftMost + MAX_SIZE);
    }

    public static void decreaseLeftMost() {
        setLeftMost(leftMost - MAX_SIZE);
    }

    public static void setLeftMost(int val) {
        leftMost = val;
        nextButton.setInteractable(leftMost + MAX_SIZE < AVAILABLE_COMPENDIUMS.size());
        prevButton.setInteractable(leftMost - MAX_SIZE >= 0);
    }
}
