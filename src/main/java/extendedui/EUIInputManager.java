package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;

public class EUIInputManager
{
    public enum KeyState
    {
        Pressed,
        JustPressed,
        JustReleased,
        Released;

        public boolean isPressed()
        {
            return this == Pressed;
        }

        public boolean isJustPressed()
        {
            return this == JustPressed;
        }

        public boolean isJustReleased()
        {
            return this == JustReleased;
        }

        public boolean isReleased()
        {
            return this == Released;
        }
    }

    public static KeyState RightClick = KeyState.Released;
    public static KeyState LeftClick = KeyState.Released;

    public static boolean didInputDown()
    {
        return CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed() || InputActionSet.down.isJustPressed();
    }

    public static boolean didInputLeft()
    {
        return CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed() || InputActionSet.left.isJustPressed();
    }

    public static boolean didInputRight()
    {
        return CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static boolean didInputUp()
    {
        return CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed() || InputActionSet.up.isJustPressed();
    }

    public static boolean isUsingNonMouseControl() {
        return Settings.isControllerMode || InputActionSet.up.isJustPressed() || InputActionSet.down.isJustPressed() || InputActionSet.left.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static void onControllerKeyPress(int keyCode)
    {
        if (keyCode == 9)
        {
            RightClick = KeyState.JustPressed;
        }
    }

    public static void onControllerKeyRelease(int keyCode)
    {
        if (keyCode == 9)
        {
            RightClick = KeyState.JustReleased;
        }
    }

    public static void postUpdate()
    {
        updateLeftClick();
        updateRightClick();
    }

    public static void setCursor(float x, float y)
    {
        setCursor((int) x, (int) y);
    }
    public static void setCursor(int x, int y)
    {
        Gdx.input.setCursorPosition(x, MathUtils.clamp(y, 0, Settings.HEIGHT));
    }

    private static void updateLeftClick()
    {
        if (Gdx.input.isButtonPressed(0))
        {
            if (LeftClick.isJustPressed())
            {
                LeftClick = KeyState.Pressed;
            }
            else if (LeftClick.isReleased() || LeftClick.isJustReleased())
            {
                LeftClick = KeyState.JustPressed;
            }
        }
        else
        {
            if (LeftClick.isJustReleased())
            {
                LeftClick = KeyState.Released;
            }
            else if (LeftClick.isPressed() || LeftClick.isJustPressed())
            {
                LeftClick = KeyState.JustReleased;
            }
        }
    }

    private static void updateRightClick()
    {
        if (Gdx.input.isButtonPressed(1))
        {
            if (RightClick.isJustPressed())
            {
                RightClick = KeyState.Pressed;
            }
            else if (RightClick.isReleased() || RightClick.isJustReleased())
            {
                RightClick = KeyState.JustPressed;
            }
        }
        else if (Settings.isControllerMode)
        {
            if (RightClick.isJustPressed())
            {
                RightClick = KeyState.Pressed;
            }
            else if (RightClick.isJustReleased())
            {
                RightClick = KeyState.Released;
            }
        }
        else
        {
            if (RightClick.isJustReleased())
            {
                RightClick = KeyState.Released;
            }
            else if (RightClick.isPressed() || RightClick.isJustPressed())
            {
                RightClick = KeyState.JustReleased;
            }
        }
    }

    public static void unpressLeft() {
        LeftClick = KeyState.Released;
    }

    public static void unpressRight() {
        RightClick = KeyState.Released;
    }
}
