package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRenderHelpers;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class EUIColors {
    private static final Color BLACK = Color.BLACK.cpy();
    private static final Color WHITE = Color.WHITE.cpy();
    private static final Color CREAM = Settings.CREAM_COLOR.cpy();
    private static final Color PURPLE = Settings.PURPLE_COLOR.cpy();
    private static final Color GREEN = Settings.GREEN_TEXT_COLOR.cpy();
    private static final Color BLUE = Settings.BLUE_TEXT_COLOR.cpy();
    private static final Color GOLD = Settings.GOLD_COLOR.cpy();
    private static final Color RED = Settings.RED_TEXT_COLOR.cpy();

    public static Color black(float a) {
        BLACK.a = a;
        return BLACK;
    }

    public static Color blue(float a) {
        BLUE.a = a;
        return BLUE;
    }

    public static Color copy(Color color, float a) {
        return new Color(color.r, color.g, color.b, a);
    }

    public static void copyFrom(Color to, Color from) {
        to.r = from.r;
        to.g = from.g;
        to.b = from.b;
        to.a = from.a;
    }

    public static Color cream(float a) {
        CREAM.a = a;
        return CREAM;
    }

    public static Color gold(float a) {
        GOLD.a = a;
        return GOLD;
    }

    public static Color green(float a) {
        GREEN.a = a;
        return GREEN;
    }

    public static void lerp(Color current, Color target, float amount) {
        lerp(current, current, target, amount);
    }

    public static void lerp(Color toFill, Color current, Color target, float amount) {
        toFill.r = EUIRenderHelpers.lerp(current.r, target.r, amount);
        toFill.g = EUIRenderHelpers.lerp(current.g, target.g, amount);
        toFill.b = EUIRenderHelpers.lerp(current.b, target.b, amount);
        toFill.a = EUIRenderHelpers.lerp(current.a, target.a, amount);
    }

    public static Color lerpNew(Color current, Color target, float amount) {
        current = current.cpy();
        current.r = EUIRenderHelpers.lerp(current.r, target.r, amount);
        current.g = EUIRenderHelpers.lerp(current.g, target.g, amount);
        current.b = EUIRenderHelpers.lerp(current.b, target.b, amount);
        current.a = EUIRenderHelpers.lerp(current.a, target.a, amount);
        return current;
    }

    public static Color purple(float a) {
        PURPLE.a = a;
        return PURPLE;
    }

    public static Color random(float min, float max, boolean grayscale) {
        if (grayscale) {
            final float value = MathUtils.random(min, max);
            return new Color(value, value, value, 1);
        }

        return new Color(MathUtils.random(min, max), MathUtils.random(min, max), MathUtils.random(min, max), 1);
    }

    public static Color red(float a) {
        RED.a = a;
        return RED;
    }

    public static Color white(float a) {
        WHITE.a = a;
        return WHITE;
    }
}
