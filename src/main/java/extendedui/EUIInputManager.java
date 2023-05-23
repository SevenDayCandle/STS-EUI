package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class EUIInputManager {
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

    public static boolean tryEscape() {
        if (InputHelper.pressedEscape && !CardCrawlGame.isPopupOpen && !EUI.cardFilters.isActive && !EUI.relicFilters.isActive && !EUI.potionFilters.isActive) {
            InputHelper.pressedEscape = false;
            return true;
        }
        return false;
    }

    // TODO utility mapping for mapping InputActionSet to CInputActionSet
    public static boolean isHoldingPeek() {
        return CInputActionSet.peek.isPressed() || InputActionSet.peek.isPressed();
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

    public static void postUpdate() {
        updateLeftClick();
        updateRightClick();
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

    public static void setCursor(float x, float y) {
        setCursor((int) x, (int) y);
    }

    public static void setCursor(int x, int y) {
        Gdx.input.setCursorPosition(x, MathUtils.clamp(y, 0, Settings.HEIGHT));
    }

    public static void unpressLeft() {
        leftClick = KeyState.Released;
    }

    public static void unpressRight() {
        rightClick = KeyState.Released;
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
