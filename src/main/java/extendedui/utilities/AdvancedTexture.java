package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class AdvancedTexture
{
    public Color color;
    public Texture texture;
    public Position2D pos;

    public AdvancedTexture(Texture texture, Color color, float alpha)
    {
        this.pos = new Position2D(0, 0, 0, 1);
        this.texture = texture;

        if (color != null)
        {
            this.color = color.cpy();
            this.color.a = alpha;
        }
    }

    public AdvancedTexture(Texture texture, Color color)
    {
        this(texture, color, 1);
    }

    public AdvancedTexture(Texture texture)
    {
        this(texture, Color.WHITE);
    }

    public float GetX()
    {
        return pos.x;
    }

    public float GetY()
    {
        return pos.y;
    }

    public float GetRotation()
    {
        return pos.rotation;
    }

    public float GetScale()
    {
        return pos.scale;
    }

    public int GetWidth()
    {
        return texture.getWidth();
    }

    public int GetHeight()
    {
        return texture.getHeight();
    }

    public AdvancedTexture SetColor(Color color)
    {
        if (color != null)
        {
            this.color.r = color.r;
            this.color.g = color.g;
            this.color.b = color.b;
            this.color.a = color.a;
        }
        else
        {
            this.color = null;
        }

        return this;
    }

    public AdvancedTexture SetColor(Float r, Float g, Float b, Float a)
    {
        if (r != null)
        {
            this.color.r = r;
        }
        if (g != null)
        {
            this.color.g = g;
        }
        if (b != null)
        {
            this.color.b = b;
        }
        if (a != null)
        {
            this.color.a = a;
        }

        return this;
    }
}
