package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class Mathf
{
    public static final float PI = (float)Math.PI;
    public static final float PositiveInfinity = Float.POSITIVE_INFINITY;
    public static final float NegativeInfinity = Float.NEGATIVE_INFINITY;
    public static final float Deg2Rad = PI * 2F / 360F;
    public static final float Rad2Deg = 1F / Deg2Rad;
    public static final float Epsilon = 0.00001f;

    //@Formatter: Off
    public static int Abs(int value) { return Math.abs(value); }
    public static int CeilToInt(float f) { return (int)Math.ceil(f); }
    public static int FloorToInt(float f) { return (int)Math.floor(f); }
    public static int RoundToInt(float f) { return Math.round(f); }
    public static float Abs(float f) { return Math.abs(f); }
    public static float Ceil(float f) { return (float)Math.ceil(f); }
    public static float Exp(float power) { return (float)Math.exp(power); }
    public static float Floor(float f) { return (float)Math.floor(f); }
    public static float Log(float f) { return (float)Math.log(f); }
    public static float Log10(float f) { return (float)Math.log10(f); }
    public static float Pow(float f, float p) { return (float)Math.pow(f, p); }
    public static float Round(float f) { return (float)Math.round(f); }
    public static float Sqrt(float f) { return (float)Math.sqrt(f); }
    public static float Sign(float f) { return f >= 0f ? 1f : -1f; }
    public static float Acos(float f) { return (float)Math.acos(f); }
    public static float Asin(float f) { return (float)Math.asin(f); }
    public static float Atan(float f) { return (float)Math.atan(f); }
    public static float Atan2(float y, float x) { return (float)Math.atan2(y, x); }
    public static float Cos(float degrees) { return (float)Math.cos(degrees * Deg2Rad); }
    public static float Sin(float degrees) { return (float)Math.sin(degrees * Deg2Rad); }
    public static float Tan(float degrees) { return (float)Math.tan(degrees * Deg2Rad); }
    //@Formatter: On

    public static int Max(int a, int b)
    {
        return a > b ? a : b;
    }

    public static float Max(float a, float b)
    {
        return a > b ? a : b;
    }

    public static int Min(int a, int b)
    {
        return a < b ? a : b;
    }

    public static float Min(float a, float b)
    {
        return a < b ? a : b;
    }

    public static float Clamp(float value, float min, float max)
    {
        return (value < min) ? min : (value > max) ? max : value;
    }

    public static int Clamp(int value, int min, int max)
    {
        return (value < min) ? min : (value > max) ? max : value;
    }

    public static float Clamp01(float value)
    {
        return (value < 0) ? 0 : (value > 1) ? 1 : value;
    }

    public static float Lerp(float current, float target, float progress)
    {
        return current + (target - current) * Clamp01(progress);
    }

    public static float LerpUnclamped(float current, float target, float progress)
    {
        return current + (target - current) * progress;
    }

    public static float LerpAngle(float current, float target, float progress)
    {
        float delta = PositiveModulus((target - current), 360);
        if (delta > 180)
        {
            delta -= 360;
        }

        return current + delta * Clamp01(progress);
    }

    static public float MoveTowards(float current, float target, float progress)
    {
        return Abs(target - current) <= progress ? target : current + Sign(target - current) * progress;
    }

    static public float MoveTowardsAngle(float current, float target, float progress)
    {
        float deltaAngle = DeltaAngle(current, target);
        if (-progress < deltaAngle && deltaAngle < progress)
        {
            return target;
        }

        return MoveTowards(current, current + deltaAngle, progress);
    }

    public static float SmoothMovement(float current, float target, float progress)
    {
        progress = Clamp01(progress);
        progress = -(2.0F * progress * progress * progress) + (3.0F * progress * progress);

        return (target * progress) + (current * (1F - progress));
    }

    public static boolean AlmostEqual(float a, float b)
    {
        return Abs(b - a) < Epsilon;
    }

    public static float PositiveModulus(float t, float length)
    {
        return Clamp(t - Floor(t / length) * length, 0.0f, length);
    }

    public static float InverseLerp(float a, float b, float value)
    {
        return a != b ? Clamp01((value - a) / (b - a)) : 0.0f;
    }

    public static float DeltaAngle(float current, float target)
    {
        float delta = PositiveModulus((target - current), 360.0F);
        if (delta > 180.0F)
        {
            delta -= 360.0F;
        }

        return delta;
    }

    public static void Lerp(Color current, Color target, float amount)
    {
        current.r = Lerp(current.r, target.r, amount);
        current.g = Lerp(current.g, target.g, amount);
        current.b = Lerp(current.b, target.b, amount);
        current.a = Lerp(current.a, target.a, amount);
    }

    public static void MoveTowards(Vector3f current, Vector3f target, Vector3f speed, float progress)
    {
        current.x = MoveTowards(current.x, target.x, speed.x * progress);
        current.y = MoveTowards(current.y, target.y, speed.y * progress);
        current.z = MoveTowards(current.z, target.z, speed.z * progress);
    }

    public static void Lerp(Vector3f current, Vector3f target, Vector3f speed, float progress)
    {
        current.x = Lerp(current.x, target.x, speed.x * progress);
        current.y = Lerp(current.y, target.y, speed.y * progress);
        current.z = Lerp(current.z, target.z, speed.z * progress);
    }

    public static void Subtract(Color a, Color b, boolean includeAlpha)
    {
        if (includeAlpha)
        {
            a.a -= b.a;
        }
        a.r -= b.r;
        a.g -= b.g;
        a.b -= b.b;
        a.clamp();
    }

    public static void Add(Vector3f a, Vector3f b, float delta)
    {
        a.x = Max(0, a.x + (b.x * delta));
        a.y = Max(0, a.y + (b.y * delta));
        a.z = Max(0, a.z + (b.z * delta));
    }

    public static void ApplyMovement(Vector3f current, Vector3f target, Vector3f speed, float progress)
    {
        current.x = MoveTowards(current.x, target.x, speed.x * progress * Settings.scale);
        current.y = MoveTowards(current.y, target.y, speed.y * progress * Settings.scale);
        current.z = MoveTowards(current.z, target.z, speed.z * progress); // rotation
    }

    public static float GetAngle(float aX, float aY, float bX, float bY)
    {
        return Rad2Deg * Atan2(bY - aY, bX - aX);
    }

    public static Integer[] Range(int lowest, int highest)
    {
        return Range(lowest, highest, 1);
    }

    public static Integer[] Range(int lowest, int highest, int step)
    {
        if (highest < lowest) {
            return new Integer[]{};
        }
        Integer[] values = new Integer[(highest - lowest) / step + 1];
        Arrays.setAll(values, i -> i * step + lowest);
        return values;
    }
}
