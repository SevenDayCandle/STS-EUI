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

        public boolean IsPressed()
        {
            return this == Pressed;
        }

        public boolean IsJustPressed()
        {
            return this == JustPressed;
        }

        public boolean IsJustReleased()
        {
            return this == JustReleased;
        }

        public boolean IsReleased()
        {
            return this == Released;
        }
    }

    public static KeyState RightClick = KeyState.Released;
    public static KeyState LeftClick = KeyState.Released;

    public static boolean DidInputDown()
    {
        return CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed() || InputActionSet.down.isJustPressed();
    }

    public static boolean DidInputLeft()
    {
        return CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed() || InputActionSet.left.isJustPressed();
    }

    public static boolean DidInputRight()
    {
        return CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static boolean DidInputUp()
    {
        return CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed() || InputActionSet.up.isJustPressed();
    }

    public static boolean IsUsingNonMouseControl() {
        return Settings.isControllerMode || InputActionSet.up.isJustPressed() || InputActionSet.down.isJustPressed() || InputActionSet.left.isJustPressed() || InputActionSet.right.isJustPressed();
    }

    public static void OnControllerKeyPress(int keyCode)
    {
        if (keyCode == 9)
        {
            RightClick = KeyState.JustPressed;
        }
    }

    public static void OnControllerKeyRelease(int keyCode)
    {
        if (keyCode == 9)
        {
            RightClick = KeyState.JustReleased;
        }
    }

    public static void PostUpdate()
    {
        UpdateLeftClick();
        UpdateRightClick();
    }

    public static void SetCursor(float x, float y)
    {
        SetCursor((int) x, (int) y);
    }
    public static void SetCursor(int x, int y)
    {
        Gdx.input.setCursorPosition(x, MathUtils.clamp(y, 0, Settings.HEIGHT));
    }

    private static void UpdateLeftClick()
    {
        if (Gdx.input.isButtonPressed(0))
        {
            if (LeftClick.IsJustPressed())
            {
                LeftClick = KeyState.Pressed;
            }
            else if (LeftClick.IsReleased() || LeftClick.IsJustReleased())
            {
                LeftClick = KeyState.JustPressed;
            }
        }
        else
        {
            if (LeftClick.IsJustReleased())
            {
                LeftClick = KeyState.Released;
            }
            else if (LeftClick.IsPressed() || LeftClick.IsJustPressed())
            {
                LeftClick = KeyState.JustReleased;
            }
        }
    }

    private static void UpdateRightClick()
    {
        if (Gdx.input.isButtonPressed(1))
        {
            if (RightClick.IsJustPressed())
            {
                RightClick = KeyState.Pressed;
            }
            else if (RightClick.IsReleased() || RightClick.IsJustReleased())
            {
                RightClick = KeyState.JustPressed;
            }
        }
        else if (Settings.isControllerMode)
        {
            if (RightClick.IsJustPressed())
            {
                RightClick = KeyState.Pressed;
            }
            else if (RightClick.IsJustReleased())
            {
                RightClick = KeyState.Released;
            }
        }
        else
        {
            if (RightClick.IsJustReleased())
            {
                RightClick = KeyState.Released;
            }
            else if (RightClick.IsPressed() || RightClick.IsJustPressed())
            {
                RightClick = KeyState.JustReleased;
            }
        }
    }

    public static void UnpressLeft() {
        LeftClick = KeyState.Released;
    }

    public static void UnpressRight() {
        RightClick = KeyState.Released;
    }
}
