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
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIMainMenuPanelButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen.COMPENDIUM;

public class MenuPanelScreenPatches {
    private static final int MAX_SIZE = 3;
    private static final float START_X = Settings.WIDTH * 0.5f - 450f * Settings.scale;
    private static EUIButton nextButton;
    private static EUIButton prevButton;
    private static int leftMost;
    public static ArrayList<EUIMainMenuPanelButton> currentButtons = new ArrayList<>();

    public static void addLeftMost() {
        setLeftMost(leftMost + MAX_SIZE);
    }

    public static void decreaseLeftMost() {
        setLeftMost(leftMost - MAX_SIZE);
    }

    public static ArrayList<EUIMainMenuPanelButton> getCompendiums() {
        ArrayList<EUIMainMenuPanelButton> available = new ArrayList<>();
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BEIGE, ImageMaster.P_INFO_CARD, MenuPanelScreen.TEXT[9], MenuPanelScreen.TEXT[11], () -> CardCrawlGame.mainMenuScreen.cardLibraryScreen.open()));
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BLUE, ImageMaster.P_INFO_RELIC, MenuPanelScreen.TEXT[12], MenuPanelScreen.TEXT[14], () -> CardCrawlGame.mainMenuScreen.relicScreen.open()));
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_RED, ImageMaster.P_INFO_POTION, MenuPanelScreen.TEXT[43], MenuPanelScreen.TEXT[44], () -> CardCrawlGame.mainMenuScreen.potionScreen.open()));
        available.add(new EUIMainMenuPanelButton(new Color(0.6f, 0.7f, 0.5f, 1f), ImageMaster.MENU_PANEL_BG_BEIGE, EUIRM.images.menuBlight.texture(), EUIRM.strings.uipool_blightPanel, EUIRM.strings.uipool_blightPanelDesc, () -> EUI.blightLibraryScreen.open()));
        return available;
    }

    public static void initialize() {
        nextButton = new EUIButton(ImageMaster.POPUP_ARROW, new EUIHitbox(Settings.WIDTH * 0.95f - 160f * Settings.scale, Settings.HEIGHT / 2f - 80f * Settings.scale, 160f * Settings.scale, 160f * Settings.scale))
                .setButtonFlip(true, false)
                .setOnClick(MenuPanelScreenPatches::addLeftMost);
        prevButton = new EUIButton(ImageMaster.POPUP_ARROW, new EUIHitbox(Settings.WIDTH * 0.05f, Settings.HEIGHT / 2f - 80f * Settings.scale, 160f * Settings.scale, 160f * Settings.scale))
                .setOnClick(MenuPanelScreenPatches::decreaseLeftMost);
    }

    public static void setLeftMost(int val) {
        leftMost = val;
        nextButton.setInteractable(leftMost + MAX_SIZE < currentButtons.size());
        prevButton.setInteractable(leftMost - MAX_SIZE >= 0);
        for (int i = leftMost; i < Math.min(currentButtons.size(), leftMost + MAX_SIZE); i++) {
            currentButtons.get(i).reposition(START_X + (i - leftMost) * 450F * Settings.scale);
        }
    }

    public static void setupPanels(ArrayList<EUIMainMenuPanelButton> buttons) {
        currentButtons = buttons;
        setLeftMost(0);
        for (EUIMainMenuPanelButton button : currentButtons) {
            button.reset();
        }
        nextButton.setActive(currentButtons.size() > 3);
        prevButton.setActive(nextButton.isActive);
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "open")
    public static class MenuPanelScreen_Open {
        @SpirePrefixPatch
        public static void prefix(MenuPanelScreen __instance) {
            currentButtons.clear();
            nextButton.setActive(false);
            prevButton.setActive(false);
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class MenuPanelScreen_InitializePanels {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(MenuPanelScreen __instance) {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == COMPENDIUM) {
                __instance.panels.clear();
                setupPanels(getCompendiums());
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "update")
    public static class MenuPanelScreen_Update {
        @SpirePostfixPatch
        public static void postfix(MenuPanelScreen __instance) {
            for (int i = leftMost; i < Math.min(currentButtons.size(), leftMost + MAX_SIZE); i++) {
                currentButtons.get(i).update();
            }
            nextButton.update();
            prevButton.update();
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "render")
    public static class MenuPanelScreen_Render {
        @SpirePostfixPatch
        public static void postfix(MenuPanelScreen __instance, SpriteBatch sb) {
            for (int i = leftMost; i < Math.min(currentButtons.size(), leftMost + MAX_SIZE); i++) {
                currentButtons.get(i).render(sb);
            }
            nextButton.render(sb);
            prevButton.render(sb);
        }
    }
}
