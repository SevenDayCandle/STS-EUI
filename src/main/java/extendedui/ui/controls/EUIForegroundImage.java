package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.ColoredTexture;

public class EUIForegroundImage extends EUIImage {
    public ColoredTexture foreground;

    public EUIForegroundImage(Texture texture) {
        super(texture);
    }

    public EUIForegroundImage(Texture texture, Color color) {
        super(texture, color);
    }

    public EUIForegroundImage(Texture texture, EUIHitbox hb, Color color) {
        super(texture, hb, color);
    }

    public EUIForegroundImage(Texture texture, EUIHitbox hb) {
        super(texture, hb);
    }

    public EUIForegroundImage(EUIImage other) {
        super(other);
    }

    protected void renderCenteredImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        super.renderCenteredImpl(sb, x, y, width, height, targetColor);
        if (foreground != null) {
            final float scale = foreground.scale * Settings.scale;
            final int s_w = foreground.texture.getWidth();
            final int s_h = foreground.texture.getHeight();
            sb.setColor(foreground.color != null ? foreground.color : targetColor);
            sb.draw(foreground.texture, x, y, width / 2f, height / 2f, width, height, scaleX * scale, scaleY * scale, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }
    }

    protected void renderImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        super.renderImpl(sb, x, y, width, height, targetColor);
        if (foreground != null) {
            final float w = width * foreground.scale;
            final float h = height * foreground.scale;
            final int s_w = foreground.texture.getWidth();
            final int s_h = foreground.texture.getHeight();
            sb.setColor(foreground.color != null ? foreground.color : targetColor);
            sb.draw(foreground.texture, x + ((width - w) * 0.5f), y + ((height - h) * 0.5f), 0, 0, w, h, scaleX, scaleY, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }
    }

    public EUIImage setForegroundTexture(Texture texture, Color color, float scale) {
        this.foreground = new ColoredTexture(texture);
        this.foreground.scale = scale;
        this.foreground.setColor(color);

        return this;
    }

    public EUIImage setForegroundTexture(Texture texture) {
        setForegroundTexture(texture, null, 1);

        return this;
    }
}
