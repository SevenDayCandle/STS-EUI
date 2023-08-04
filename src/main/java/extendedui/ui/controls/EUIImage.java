package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;

public class EUIImage extends EUIHoverable {
    public ColoredTexture background;
    public Texture texture;
    public EUIRenderHelpers.BlendingMode blendingMode = EUIRenderHelpers.BlendingMode.Normal;
    public EUIRenderHelpers.ShaderMode shaderMode = EUIRenderHelpers.ShaderMode.Normal;
    public Color color;
    public Color sourceColor;
    public Color targetColor;
    public float scaleX = 1;
    public float scaleY = 1;
    public float rotation;
    public float baseTransitionTime;
    public float transitionTime;
    public int srcWidth;
    public int srcHeight;
    public boolean flipX;
    public boolean flipY;

    public EUIImage(Texture texture) {
        this(texture, Color.WHITE);
    }

    public EUIImage(Texture texture, Color color) {
        this(texture, new EUIHitbox(texture.getWidth(), texture.getHeight()), color);
    }

    public EUIImage(Texture texture, EUIHitbox hb, Color color) {
        super(hb);
        this.texture = texture;
        this.color = color.cpy();
        this.sourceColor = this.color.cpy();
        this.srcWidth = texture.getWidth();
        this.srcHeight = texture.getHeight();
    }

    public EUIImage(Texture texture, EUIHitbox hb) {
        this(texture, hb, Color.WHITE);
    }

    public EUIImage(EUIImage other) {
        this(other.texture, other.hb.makeCopy());
    }

    public EUITourTooltip makeTour(boolean canDismiss) {
        if (tooltip != null) {
            EUITourTooltip tip = new EUITourTooltip(hb, tooltip.title, tooltip.description);
            tip.setFlash(this);
            tip.setCanDismiss(canDismiss);
            return tip;
        }
        return null;
    }

    public void render(SpriteBatch sb, Hitbox hb) {
        render(sb, hb, color);
    }

    public void render(SpriteBatch sb, Hitbox hb, Color targetColor) {
        render(sb, shaderMode, hb, targetColor);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb, Color targetColor) {
        render(sb, mode, blendingMode, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb) {
        render(sb, mode, hb.x, hb.y, hb.width, hb.height);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, Hitbox hb) {
        render(sb, blend, hb.x, hb.y, hb.width, hb.height);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, float x, float y, float width, float height) {
        render(sb, shaderMode, blend, x, y, width, height, color);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, Hitbox hb, Color targetColor) {
        render(sb, shaderMode, blend, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, EUIRenderHelpers.BlendingMode blend, Hitbox hb, Color targetColor) {
        render(sb, mode, blend, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void render(SpriteBatch sb, float x, float y, float width, float height) {
        render(sb, shaderMode, x, y, width, height);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height) {
        render(sb, mode, blendingMode, x, y, width, height, color);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, EUIRenderHelpers.BlendingMode blend, float x, float y, float width, float height, Color targetColor) {
        mode.draw(sb, (s) -> blend.draw(s, s2 -> renderImpl(s, x, y, width, height, targetColor)));
    }

    public void renderBicubic(SpriteBatch sb, float x, float y, float width, float height) {

    }

    public void renderCentered(SpriteBatch sb) {
        renderCentered(sb, hb);

        hb.render(sb);
    }

    public void renderCentered(SpriteBatch sb, Hitbox hb) {
        renderCentered(sb, hb, color);
    }

    public void renderCentered(SpriteBatch sb, Hitbox hb, Color targetColor) {
        renderCentered(sb, shaderMode, hb, targetColor);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb, Color targetColor) {
        renderCentered(sb, mode, blendingMode, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, EUIRenderHelpers.BlendingMode blend, float x, float y, float width, float height, Color targetColor) {
        mode.draw(sb, (s) -> blend.draw(s, s2 -> renderCenteredImpl(s, x, y, width, height, targetColor)));
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb) {
        renderCentered(sb, mode, hb.x, hb.y, hb.width, hb.height);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height) {
        renderCentered(sb, mode, blendingMode, x, y, width, height, color);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, Hitbox hb) {
        renderCentered(sb, blend, hb.x, hb.y, hb.width, hb.height);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, float x, float y, float width, float height) {
        renderCentered(sb, shaderMode, blend, x, y, width, height, color);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.BlendingMode blend, Hitbox hb, Color targetColor) {
        renderCentered(sb, shaderMode, blend, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, EUIRenderHelpers.BlendingMode blend, Hitbox hb, Color targetColor) {
        renderCentered(sb, mode, blend, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void renderCentered(SpriteBatch sb, float x, float y, float width, float height) {
        renderCentered(sb, shaderMode, x, y, width, height);
    }

    protected void renderCenteredImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        if (background != null) {
            final float w = width * background.scale;
            final float h = height * background.scale;
            final int s_w = background.texture.getWidth();
            final int s_h = background.texture.getHeight();
            sb.setColor(background.color != null ? background.color : targetColor);
            sb.draw(background.texture, x, y, width / 2f, height / 2f, w, h, scaleX, scaleY, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }

        sb.setColor(targetColor);
        sb.draw(texture, x, y, width / 2f, height / 2f, width, height, scaleX, scaleY, rotation, 0, 0, srcWidth, srcHeight, flipX, flipY);
    }

    public void renderImpl(SpriteBatch sb) {
        render(sb, hb);

        hb.render(sb);
    }

    protected void renderImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        if (background != null) {
            final float w = width * background.scale;
            final float h = height * background.scale;
            final int s_w = background.texture.getWidth();
            final int s_h = background.texture.getHeight();
            sb.setColor(background.color != null ? background.color : targetColor);
            sb.draw(background.texture, x + ((width - w) * 0.5f), y + ((height - h) * 0.5f), 0, 0, w, h, scaleX, scaleY, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }

        sb.setColor(targetColor);
        sb.draw(texture, x, y, 0, 0, width, height, scaleX, scaleY, rotation, 0, 0, srcWidth, srcHeight, flipX, flipY);
    }

    public EUIImage resize(float width, float height, float scale) {
        hb.resize(width * scale, height * scale);

        return this;
    }

    public EUIImage setBackgroundTexture(Texture texture) {
        setBackgroundTexture(texture, null, 1);

        return this;
    }

    public EUIImage setBackgroundTexture(Texture texture, Color color, float scale) {
        this.background = new ColoredTexture(texture);
        this.background.scale = scale;
        this.background.setColor(color);

        return this;
    }

    public EUIImage setBlendingMode(EUIRenderHelpers.BlendingMode blendingMode) {
        this.blendingMode = blendingMode;
        return this;
    }

    public EUIImage setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        this.sourceColor.set(this.color);

        return this;
    }

    public EUIImage setColor(Color color) {
        this.color.set(color);
        this.sourceColor.set(this.color);

        return this;
    }

    public EUIImage setFlipping(boolean flipX, boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;

        return this;
    }

    public EUIImage setHitbox(EUIHitbox hb) {
        this.hb = hb;

        return this;
    }

    public EUIImage setPosition(float cX, float cY) {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIImage setTooltip(String title, String description) {
        return setTooltip(new EUITooltip(title, description));
    }

    public EUIImage setTooltip(EUITooltip tooltip) {
        super.setTooltip(tooltip);

        return this;
    }

    public EUIImage translate(float x, float y) {
        this.hb.translate(x, y);

        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        updateColor();
    }

    public EUIImage setOriginalDimensions(int srcWidth, int srcHeight) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;

        return this;
    }

    public EUIImage setRotation(float rotation) {
        this.rotation = rotation;

        return this;
    }

    public EUIImage setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        return this;
    }

    public EUIImage setShaderMode(EUIRenderHelpers.ShaderMode shaderMode) {
        this.shaderMode = shaderMode;
        return this;
    }

    public EUIImage setTargetColor(Color color) {
        return setTargetColor(color, 0.3f);
    }

    public EUIImage setTargetColor(Color color, float transitionTime) {
        if (!color.equals(targetColor)) {
            this.targetColor = color.cpy();
            this.baseTransitionTime = this.transitionTime = Math.max(Gdx.graphics.getRawDeltaTime(), transitionTime);
        }

        return this;
    }

    public EUIImage setTexture(Texture texture) {
        setTexture(texture, null);
        return this;
    }

    public EUIImage setTexture(Texture texture, Color color) {
        this.texture = texture;
        if (color != null) {
            this.color = color.cpy();
            this.sourceColor = this.color.cpy();
        }
        this.srcWidth = texture.getWidth();
        this.srcHeight = texture.getHeight();

        return this;
    }

    public boolean tryRenderCentered(SpriteBatch sb) {
        if (isActive) {
            this.hb.render(sb);
            renderCentered(sb);
        }

        return isActive;
    }

    public void updateColor() {
        if (targetColor != null && baseTransitionTime > 0) {
            transitionTime -= Gdx.graphics.getRawDeltaTime();
            EUIColors.lerp(this.color, targetColor, sourceColor, transitionTime / baseTransitionTime);
            if (transitionTime <= 0) {
                this.color = this.sourceColor = this.targetColor;
                this.targetColor = null;
            }
        }
    }
}