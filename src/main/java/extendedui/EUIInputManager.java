package extendedui;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.Settings;

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
