package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.interfaces.markers.TextInputProvider;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/* This class allows one to check if the user clicked in the mouse in render calls, which is not possible with InputManager because clicks are cleared after the update phase */
public class EUIInputManager {
    public static final char BACKSPACE = 8;
    public static final char DEL = 127;
    public static final char DUMMY_CUT = 6; // Using control characters to stand in for cut/paste since we can't enter them anyhow
    public static final char DUMMY_PASTE = 7;
    public static final char ENTER_DESKTOP = '\r';
    public static final char ENTER_ANDROID = '\n';
    public static final char TAB = '\t';
    private static TextInputProvider textProvider;
    private static int curLimit;
    private static int pos;
    public static KeyState rightClick = KeyState.Released;
    public static KeyState leftClick = KeyState.Released;

    public static boolean didInputDown() {
        return CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed() || InputActionSet.down.isJustPressed();
    }

    public static boolean didInputLeft() {
        return CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed() || InputActionSet.left.isJustPressed();
    }

    public static boolean didInputRight() {
        return CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static boolean didInputUp() {
        return CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed() || InputActionSet.up.isJustPressed();
    }

    public static int getPos() {
        return pos;
    }

    public static boolean isHoldingPeek() {
        return CInputActionSet.peek.isPressed() || InputActionSet.peek.isPressed();
    }

    public static boolean isInputTyping() {
        return textProvider != null;
    }

    public static boolean isInputTyping(TextInputProvider provider) {
        return textProvider == provider;
    }

    public static boolean isUsingNonMouseControl() {
        return Settings.isControllerMode || InputActionSet.up.isJustPressed() || InputActionSet.down.isJustPressed() || InputActionSet.left.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static void onControllerKeyPress(int keyCode) {
        if (keyCode == 9) {
            rightClick = KeyState.JustPressed;
        }
    }

    public static void onControllerKeyRelease(int keyCode) {
        if (keyCode == 9) {
            rightClick = KeyState.JustReleased;
        }
    }

    // Bounded by isInputTyping
    public static boolean onKeyboardDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.DOWN:
                pos = MathUtils.clamp(textProvider.onPushArrowDown(pos), 0, textProvider.getBuffer().length());
                break;
            case Input.Keys.LEFT:
                pos = MathUtils.clamp(textProvider.onPushArrowLeft(pos), 0, textProvider.getBuffer().length());
                break;
            case Input.Keys.RIGHT:
                pos = MathUtils.clamp(textProvider.onPushArrowRight(pos), 0, textProvider.getBuffer().length());
                break;
            case Input.Keys.UP:
                pos = MathUtils.clamp(textProvider.onPushArrowUp(pos), 0, textProvider.getBuffer().length());
                break;
        }
        return textProvider.onKeyDown(keyCode);
    }

    // Bounded by isInputTyping
    public static boolean onKeyboardUp(int keyCode) {
        return textProvider.onKeyUp(keyCode);
    }

    public static void postUpdate() {
        updateLeftClick();
        updateRightClick();
    }

    public static boolean releaseType(TextInputProvider provider) {
        if (textProvider == provider) {
            textProvider = null;
            return true;
        }
        return false;
    }

    public static void resetPos() {
        pos = textProvider.getBuffer().length();
    }

    public static void setCursor(float x, float y) {
        setCursor((int) x, (int) y);
    }

    public static void setCursor(int x, int y) {
        Gdx.input.setCursorPosition(x, MathUtils.clamp(y, 0, Settings.HEIGHT));
    }

    public static boolean tryEscape() {
        if (InputHelper.pressedEscape && !CardCrawlGame.isPopupOpen && !EUI.cardFilters.isActive && !EUI.relicFilters.isActive && !EUI.potionFilters.isActive) {
            InputHelper.pressedEscape = false;
            return true;
        }
        return false;
    }

    public static boolean tryStartType(TextInputProvider provider) {
        if (textProvider != null) {
            return textProvider == provider;
        }
        textProvider = provider;
        curLimit = textProvider.getMaxPosition();
        return true;
    }

    public static boolean tryType(char character) {
        if (!isInputTyping()) {
            return false;
        }

        StringBuilder sb = textProvider.getBuffer();
        if (pos > sb.length()) {
            pos = sb.length();
        }

        // Copying from TextReceiver in case LibGDX's input doesn't block off sym
        if (UIUtils.isMac && Gdx.input.isKeyPressed(Input.Keys.SYM)) {
            return false;
        }

        switch (character) {
            case ENTER_ANDROID:
            case ENTER_DESKTOP:
                if (textProvider.onPushEnter())
                    return true;
                break;
            case BACKSPACE:
                if (textProvider.onPushBackspace()) {
                    return true;
                }
                if (pos > 0) {
                    sb.deleteCharAt(pos - 1);
                    pos -= 1;
                    textProvider.onUpdate(pos, character);
                }
                return true;
            case DEL:
                if (textProvider.onPushDelete()) {
                    return true;
                }
                if (pos >= 0 && pos < sb.length()) {
                    sb.deleteCharAt(pos);
                    textProvider.onUpdate(pos, character);
                }
                return true;
            case TAB:
                if (textProvider.onPushTab()) {
                    return true;
                }
            // Ignore other control characters
            default:
                if (character < 32) return false;
        }

        boolean add = textProvider.acceptCharacter(character);
        if (add && curLimit < 0 || sb.length() < curLimit) {
            sb.insert(pos, character);
            pos += 1;
            textProvider.onUpdate(pos, character);
            return true;
        }

        return false;
    }

    public static boolean tryUseControlAction(int keycode) {
        // Escape
        if (keycode == Input.Keys.ESCAPE && textProvider.onPushEscape()) {
            return true;
        }
        // Control operations
        if (InputHelper.isShortcutModifierKeyPressed()) {
            StringBuilder sb = textProvider.getBuffer();
            switch (keycode) {
                case Input.Keys.X:
                    if (textProvider.allowCopy()) {
                        StringSelection selection = new StringSelection(sb.toString());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        sb.setLength(0);
                        pos = 0;
                        textProvider.onUpdate(pos, DUMMY_CUT);
                    }
                    return true;
                case Input.Keys.C:
                    if (textProvider.allowCopy()) {
                        StringSelection selection = new StringSelection(sb.toString());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                    }
                    return true;
                case Input.Keys.V:
                    if (textProvider.allowPaste()) {
                        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            try {
                                String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                                sb.insert(pos, text);
                                pos += text.length();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        textProvider.onUpdate(pos, DUMMY_PASTE);
                    }
                    return true;
            }
        }
        return false;
    }

    public static void unpressLeft() {
        leftClick = KeyState.Released;
    }

    public static void unpressRight() {
        rightClick = KeyState.Released;
    }

    private static void updateLeftClick() {
        if (Gdx.input.isButtonPressed(0)) {
            if (leftClick.isJustPressed()) {
                leftClick = KeyState.Pressed;
            }
            else if (leftClick.isReleased() || leftClick.isJustReleased()) {
                leftClick = KeyState.JustPressed;
            }
        }
        else {
            if (leftClick.isJustReleased()) {
                leftClick = KeyState.Released;
            }
            else if (leftClick.isPressed() || leftClick.isJustPressed()) {
                leftClick = KeyState.JustReleased;
            }
        }
    }

    private static void updateRightClick() {
        if (Gdx.input.isButtonPressed(1)) {
            if (rightClick.isJustPressed()) {
                rightClick = KeyState.Pressed;
            }
            else if (rightClick.isReleased() || rightClick.isJustReleased()) {
                rightClick = KeyState.JustPressed;
            }
        }
        else if (Settings.isControllerMode) {
            if (rightClick.isJustPressed()) {
                rightClick = KeyState.Pressed;
            }
            else if (rightClick.isJustReleased()) {
                rightClick = KeyState.Released;
            }
        }
        else {
            if (rightClick.isJustReleased()) {
                rightClick = KeyState.Released;
            }
            else if (rightClick.isPressed() || rightClick.isJustPressed()) {
                rightClick = KeyState.JustReleased;
            }
        }
    }

    public enum KeyState {
        Pressed,
        JustPressed,
        JustReleased,
        Released;

        public boolean isJustPressed() {
            return this == JustPressed;
        }

        public boolean isJustReleased() {
            return this == JustReleased;
        }

        public boolean isPressed() {
            return this == Pressed;
        }

        public boolean isReleased() {
            return this == Released;
        }
    }
}
