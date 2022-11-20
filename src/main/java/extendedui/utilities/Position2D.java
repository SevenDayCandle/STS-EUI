package extendedui.utilities;

import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import org.lwjgl.util.vector.Vector4f;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class Position2D
{
    public float x;
    public float y;
    public float rotation;
    public float scale;

    public Position2D()
    {
        this(0, 0, 0, 0);
    }

    public Position2D(Vector4f vector)
    {
        Import(vector);
    }

    public Position2D(float x, float y, float rotation, float scale)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Position2D Copy()
    {
        return new Position2D(x, y, rotation, scale);
    }

    public void Import(Position2D other)
    {
        this.x = other.x;
        this.y = other.y;
        this.rotation = other.rotation;
        this.scale = other.scale;
    }

    public void Import(Vector4f vector)
    {
        this.x = vector.x;
        this.y = vector.y;
        this.rotation = vector.z;
        this.scale = vector.w;
    }

    public Vector4f Export()
    {
        return new Vector4f(x, y, rotation, scale);
    }

    public void Clear()
    {
        x = y = rotation = scale = 0;
    }

    public void ApplyMovement(Position2D target, Position2D speed, float progress)
    {
        x = Mathf.MoveTowards(x, target.x, speed.x * progress * Settings.scale);
        y = Mathf.MoveTowards(y, target.y, speed.y * progress * Settings.scale);
        rotation = Mathf.MoveTowards(rotation, target.rotation, speed.rotation * progress); // rotation
        scale = Mathf.MoveTowards(scale, target.scale, speed.scale * progress); // scale
    }

    public void ApplyAcceleration(Position2D acceleration, float duration, float delta, Interpolation interpolation)
    {
        if (duration == 0)
        {
            x = Math.max(0, x + (acceleration.x * delta));
            y = Math.max(0, y + (acceleration.y * delta));
            rotation = Math.max(0, rotation + (acceleration.rotation * delta));
            scale = Math.max(0, scale + (acceleration.scale * delta));
            return;
        }

        float t_x = acceleration.x;
        float t_y = acceleration.y;
        float t_s = acceleration.scale;
        float t_r = acceleration.rotation;
        if (duration > delta)
        {
            float m = Math.min(1, delta / duration);
            if (interpolation != null)
            {
                m = interpolation.apply(m);
            }

            t_x *= m;
            t_y *= m;
            t_s *= m;
            t_r *= m;
        }

        x += t_x;
        y += t_y;
        rotation += t_r;
        scale += t_s;

        if (scale < 0)
        {
            scale = 0;
        }

        acceleration.x -= t_x;
        acceleration.y -= t_y;
        acceleration.rotation -= t_r;
        acceleration.scale -= t_s;
    }

    @Override
    public String toString()
    {
        return EUIUtils.Format("[x:{0}, y:{1}, rotation:{2}, scale:{3}]", x, y, rotation, scale);
    }
}
