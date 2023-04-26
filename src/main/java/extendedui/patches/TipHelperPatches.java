package extendedui.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.TipHelper;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

import java.util.ArrayList;

public class TipHelperPatches {
    public static ArrayList<String> getFilteredKeywords(ArrayList<String> original) {
        return EUIUtils.filter(original, o -> !EUIConfiguration.getIsTipDescriptionHiddenByName(o));
    }

    // Make a proxy arraylist that only renders tooltips that can be seen
    @SpirePatch(clz = TipHelper.class, method = "render")
    public static class TipHelperPatches_Render {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getFieldName().equals("KEYWORDS")) {
                        m.replace("{ $_ = extendedui.patches.TipHelperPatches.getFilteredKeywords(KEYWORDS); }");
                    }
                }
            };
        }
    }
}
