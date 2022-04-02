package extendedui.patches.cInputHelper;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import extendedui.EUIInputManager;

@SpirePatch(clz= CInputHelper.class, method="listenerPress")
public class CInputHelper_ListenerPress
{
    @SpirePrefixPatch
    public static void Method(int keycode)
    {
        EUIInputManager.OnControllerKeyPress(keycode);
    }
}