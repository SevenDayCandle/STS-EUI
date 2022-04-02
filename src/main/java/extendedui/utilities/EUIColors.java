package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class EUIColors
{
    public static final Color COLOR_AFTERLIFE = new Color(0.7f,0.9f,1.0f,1);
    public static final Color COLOR_AUTOPLAY = new Color(0.33f,0.33f,0.45f,1);
    public static final Color COLOR_DELAYED = new Color(0.26f,0.26f,0.26f,1);
    public static final Color COLOR_FRAGILE = new Color(0.80f,0.46f,0.7f,1);
    public static final Color COLOR_GRAVE = new Color(0.5f,0.5f,0.5f,1);
    public static final Color COLOR_HARMONIC = new Color(1.0f,0.55f,0.8f,1);
    public static final Color COLOR_HASTE = new Color(0.35f,0.5f,0.79f,1);
    public static final Color COLOR_LOYAL = new Color(0.81f,0.51f,0.3f,1);
    public static final Color COLOR_PURGE = new Color(0.71f,0.3f,0.55f,1);
    public static final Color COLOR_ETHEREAL = new Color(0.51f,0.69f,0.6f,1);
    public static final Color COLOR_EXHAUST = new Color(0.81f,0.35f,0.35f,1);
    public static final Color COLOR_INNATE = new Color(0.8f,0.8f,0.35f,1);
    public static final Color COLOR_RETAIN = new Color(0.49f,0.78f,0.35f,1);
    public static final Color COLOR_UNPLAYABLE = new Color(0.3f,0.20f,0.20f,1);

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
