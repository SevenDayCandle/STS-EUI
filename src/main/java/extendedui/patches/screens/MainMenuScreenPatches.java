package extendedui.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CannotCompileException;
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
}
