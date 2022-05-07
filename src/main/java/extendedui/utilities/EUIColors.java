package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class EUIColors
{
    private static final Color BLACK = Color.BLACK.cpy();
    private static final Color WHITE = Color.WHITE.cpy();
    private static final Color CREAM = Settings.CREAM_COLOR.cpy();
    private static final Color PURPLE = Settings.PURPLE_COLOR.cpy();
    private static final Color GREEN = Settings.GREEN_TEXT_COLOR.cpy();
    private static final Color BLUE = Settings.BLUE_TEXT_COLOR.cpy();
    private static final Color GOLD = Settings.GOLD_COLOR.cpy();
    private static final Color RED = Settings.RED_TEXT_COLOR.cpy();

    public static Color Copy(Color color, float a)
    {
        return new Color(color.r, color.g, color.b, a);
    }

    public static Color Lerp(Color current, Color target, float amount)
    {
        current = current.cpy();
        current.r = Mathf.Lerp(current.r, target.r, amount);
        current.g = Mathf.Lerp(current.g, target.g, amount);
        current.b = Mathf.Lerp(current.b, target.b, amount);
        current.a = Mathf.Lerp(current.a, target.a, amount);
        return current;
    }

    public static Color Random(float min, float max, boolean grayscale)
    {
        if (grayscale)
        {
            final float value = MathUtils.random(min, max);
            return new Color(value, value, value, 1);
        }

        return new Color(MathUtils.random(min, max), MathUtils.random(min, max), MathUtils.random(min, max), 1);
    }

    public static Color Black(float a)
    {
        BLACK.a = a;
        return BLACK;
    }

    public static Color White(float a)
    {
        WHITE.a = a;
        return WHITE;
    }

    public static Color Cream(float a)
    {
        CREAM.a = a;
        return CREAM;
    }

    public static Color Purple(float a)
    {
        PURPLE.a = a;
        return PURPLE;
    }

    public static Color Green(float a)
    {
        GREEN.a = a;
        return GREEN;
    }

    public static Color Blue(float a)
    {
        BLUE.a = a;
        return BLUE;
    }

    public static Color Gold(float a)
    {
        GOLD.a = a;
        return GOLD;
    }

    public static Color Red(float a)
    {
        RED.a = a;
        return RED;
    }
}
