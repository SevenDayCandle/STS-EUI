package extendedui.patches;

import basemod.interfaces.TextReceiver;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;
import extendedui.EUIInputManager;

public class ScrollInputProcessorPatches {
    @SpirePatch(
            clz = ScrollInputProcessor.class,
            method = "keyTyped"
    )
    public static class ScrollInputProcessorPatches_KeyTyped
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> readKeyboardInput(ScrollInputProcessor __instance, char character)
        {
            if (!EUIInputManager.tryType(character)) {
                return SpireReturn.Continue();
            }
            return SpireReturn.Return(true);
        }
    }

    @SpirePatch(
            clz = ScrollInputProcessor.class,
            method = "keyDown"
    )
    public static class ScrollInputProcessorPatches_KeyDown
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> onKeyDown(ScrollInputProcessor __instance, int keycode)
        {
            if (!EUIInputManager.isInputTyping()) {
                return SpireReturn.Continue();
            }

            if (EUIInputManager.tryUseControlAction(keycode)) {
                return SpireReturn.Return(true);
            }

            return SpireReturn.Return(EUIInputManager.onKeyboardDown(keycode));
        }
    }

    @SpirePatch(
            clz = ScrollInputProcessor.class,
            method = "keyUp"
    )
    public static class ScrollInputProcessorPatches_KeyUp
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> onKeyUp(ScrollInputProcessor __instance, int keycode)
        {
            if (!EUIInputManager.isInputTyping()) {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return(EUIInputManager.onKeyboardUp(keycode));
        }
    }
}
