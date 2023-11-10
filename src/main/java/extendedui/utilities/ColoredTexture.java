package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod
public class ColoredTexture {
    public Color color;
    public Texture texture;
    public float scale;

    public ColoredTexture(Texture texture) {
        this(texture, Color.WHITE);
    }

    public ColoredTexture(Texture texture, Color color) {
        this(texture, color, 1);
    }

    public ColoredTexture(Texture texture, Color color, float alpha) {
        this(texture, color, alpha, 1);
    }

    public ColoredTexture(Texture texture, Color color, float alpha, float scale) {
        this.scale = scale;
        this.texture = texture;

        if (color != null) {
            this.color = color.cpy();
            this.color.a = alpha;
        }
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public int getWidth() {
        return texture.getWidth();
    }

    public ColoredTexture setColor(Color color) {
        if (color != null) {
            EUIColors.copyFrom(this.color, color);
        }
        else {
            this.color = null;
        }

        return this;
    }

    public ColoredTexture setColor(Float r, Float g, Float b, Float a) {
        if (r != null) {
            this.color.r = r;
        }
        if (g != null) {
            this.color.g = g;
        }
        if (b != null) {
            this.color.b = b;
        }
        if (a != null) {
            this.color.a = a;
        }

        return this;
    }
}
