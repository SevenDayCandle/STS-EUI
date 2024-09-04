package extendedui.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;

public class MainMenuScreenPatches {

    @SpirePatch(clz = MainMenuScreen.class, method = "fadeOut")
    public static class MainMenuScreenPatches_SetMainMenuButtons {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getDeltaTime")) {
                        m.replace("{ $_ = extendedui.configuration.EUIConfiguration.canSkipFade() ? 100f : $proceed($$); }");
                    }
                }
            };
        }
    }

    // Fix rare transient crash that can occur if a controller is plugged in and custom menu screen is clicked
    @SpirePatch(clz = MainMenuScreen.class, method = "updateMenuPanelController")
    public static class MainMenuScreenPatches_UpdateMenuPanelController {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(MainMenuScreen __instance) {
            if (__instance.panelScreen.panels.isEmpty()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CInputHelper.class, "setCursor");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2]};
            }
        }
    }
}
