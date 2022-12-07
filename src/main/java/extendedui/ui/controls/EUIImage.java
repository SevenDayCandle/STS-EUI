package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;

public class EUIImage extends EUIHoverable
{
    public ColoredTexture background;
    public ColoredTexture foreground;
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

    public EUIImage(Texture texture)
    {
        this(texture, Color.WHITE);
    }

    public EUIImage(Texture texture, EUIHitbox hb)
    {
        this(texture, hb, Color.WHITE);
    }

    public EUIImage(Texture texture, Color color) {
        this(texture, new EUIHitbox(texture.getWidth(), texture.getHeight()), color);
    }

    public EUIImage(Texture texture, EUIHitbox hb, Color color)
    {
        super(hb);
        this.texture = texture;
        this.color = color.cpy();
        this.sourceColor = this.color.cpy();
        this.srcWidth = texture.getWidth();
        this.srcHeight = texture.getHeight();
    }

    public EUIImage translate(float x, float y)
    {
        this.hb.translate(x, y);

        return this;
    }

    public EUIImage resize(float width, float height, float scale)
    {
        hb.resize(width * scale, height * scale);

        return this;
    }

    public EUIImage setTexture(Texture texture, Color color)
    {
        this.texture = texture;
        if (color != null) {
            this.color = color.cpy();
            this.sourceColor = this.color.cpy();
        }
        this.srcWidth = texture.getWidth();
        this.srcHeight = texture.getHeight();

        return this;
    }

    public EUIImage setTexture(Texture texture) {
        setTexture(texture, null);
        return this;
    }

    public EUIImage setBackgroundTexture(Texture texture, Color color, float scale)
    {
        this.background = new ColoredTexture(texture);
        this.background.scale = scale;
        this.background.setColor(color);

        return this;
    }

    public EUIImage setBackgroundTexture(Texture texture)
    {
        setBackgroundTexture(texture, null, 1);

        return this;
    }

    public EUIImage setForegroundTexture(Texture texture, Color color, float scale)
    {
        this.foreground = new ColoredTexture(texture);
        this.foreground.scale = scale;
        this.foreground.setColor(color);

        return this;
    }

    public EUIImage setForegroundTexture(Texture texture)
    {
        setForegroundTexture(texture, null, 1);

        return this;
    }

    public EUIImage setHitbox(EUIHitbox hb)
    {
        this.hb = hb;

        return this;
    }

    public EUIImage setPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIImage setBlendingMode(EUIRenderHelpers.BlendingMode blendingMode) {
        this.blendingMode = blendingMode;
        return this;
    }

    public EUIImage setShaderMode(EUIRenderHelpers.ShaderMode shaderMode) {
        this.shaderMode = shaderMode;
        return this;
    }

    public EUIImage setColor(float r, float g, float b, float a)
    {
        this.color = new Color(r, g, b, a);
        this.sourceColor = this.color.cpy();

        return this;
    }

    public EUIImage setColor(Color color)
    {
        this.color = color.cpy();
        this.sourceColor = this.color.cpy();

        return this;
    }

    public EUIImage setTargetColor(Color color)
    {
        return setTargetColor(color, 0.3f);
    }

    public EUIImage setTargetColor(Color color, float transitionTime)
    {
        if (!color.equals(targetColor)) {
            this.targetColor = color.cpy();
            this.baseTransitionTime = this.transitionTime = Math.max(Gdx.graphics.getRawDeltaTime(), transitionTime);
        }

        return this;
    }

    public EUIImage setFlipping(boolean flipX, boolean flipY)
    {
        this.flipX = flipX;
        this.flipY = flipY;

        return this;
    }

    public EUIImage setOriginalDimensions(int srcWidth, int srcHeight)
    {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;

        return this;
    }

    public EUIImage setScale(float scaleX, float scaleY)
    {
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        return this;
    }

    public EUIImage setRotation(float rotation)
    {
        this.rotation = rotation;

        return this;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        updateColor();
    }

    public void updateColor()
    {
        if (targetColor != null && baseTransitionTime > 0) {
            transitionTime -= Gdx.graphics.getRawDeltaTime();
            this.color = EUIColors.lerp(targetColor, sourceColor, transitionTime / baseTransitionTime);
            if (transitionTime <= 0) {
                this.color = this.sourceColor = this.targetColor;
                this.targetColor = null;
            }
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        render(sb, hb);

        hb.render(sb);
    }

    public void render(SpriteBatch sb, Hitbox hb)
    {
        render(sb, hb.x, hb.y, hb.width, hb.height);
    }

    public void render(SpriteBatch sb, Hitbox hb, Color targetColor)
    {
        render(sb, shaderMode, hb, targetColor);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb)
    {
        render(sb, mode, hb.x, hb.y, hb.width, hb.height);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb, Color targetColor)
    {
        render(sb, mode, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void render(SpriteBatch sb, float x, float y, float width, float height) {
        render(sb, shaderMode, x, y, width, height);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height) {
        render(sb, shaderMode, x, y, width, height, color);
    }

    public void render(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height, Color targetColor) {
        mode.draw(sb, (s) -> renderImpl(s, x, y, width, height, targetColor));
    }

    protected void renderImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        sb.setBlendFunction(blendingMode.srcFunc, blendingMode.dstFunc);
        if (background != null)
        {
            final float w = width * background.scale;
            final float h = height * background.scale;
            final int s_w = background.texture.getWidth();
            final int s_h = background.texture.getHeight();
            sb.setColor(background.color != null ? background.color : targetColor);
            sb.draw(background.texture, x + ((width-w)*0.5f), y + ((height-h)*0.5f), 0, 0, w, h, scaleX, scaleY, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }

        sb.setColor(targetColor);
        sb.draw(texture, x, y, 0, 0, width, height, scaleX, scaleY, rotation, 0, 0, srcWidth, srcHeight, flipX, flipY);

        if (foreground != null)
        {
            final float w = width * foreground.scale;
            final float h = height * foreground.scale;
            final int s_w = foreground.texture.getWidth();
            final int s_h = foreground.texture.getHeight();
            sb.setColor(foreground.color != null ? foreground.color : targetColor);
            sb.draw(foreground.texture, x + ((width-w)*0.5f), y + ((height-h)*0.5f), 0, 0, w, h, scaleX, scaleY, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }
        sb.setBlendFunction(770, 771);
    }

    public void renderCentered(SpriteBatch sb)
    {
        renderCentered(sb, hb);

        hb.render(sb);
    }

    public void renderCentered(SpriteBatch sb, Hitbox hb)
    {
        renderCentered(sb, hb.x, hb.y, hb.width, hb.height);
    }

    public void renderCentered(SpriteBatch sb, Hitbox hb, Color targetColor)
    {
        renderCentered(sb, shaderMode, hb, targetColor);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb)
    {
        renderCentered(sb, mode, hb.x, hb.y, hb.width, hb.height);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, Hitbox hb, Color targetColor)
    {
        renderCentered(sb, mode, hb.x, hb.y, hb.width, hb.height, targetColor);
    }

    public void renderCentered(SpriteBatch sb, float x, float y, float width, float height) {
        renderCentered(sb, shaderMode, x, y, width, height);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height) {
        renderCentered(sb, shaderMode, x, y, width, height, color);
    }

    public void renderCentered(SpriteBatch sb, EUIRenderHelpers.ShaderMode mode, float x, float y, float width, float height, Color targetColor) {
        mode.draw(sb, (s) -> renderCenteredImpl(s, x, y, width, height, targetColor));
    }

    protected void renderCenteredImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor)
    {
        sb.setBlendFunction(blendingMode.srcFunc, blendingMode.dstFunc);
        if (background != null)
        {
            final float scale = background.scale * Settings.scale;
            final int s_w = background.texture.getWidth();
            final int s_h = background.texture.getHeight();
            sb.setColor(background.color != null ? background.color : targetColor);
            sb.draw(background.texture, x, y, width/2f, height/2f, width, height, scaleX * scale, scaleY * scale, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }

        sb.setColor(targetColor);
        sb.draw(texture, x, y, width/2f, height/2f, width, height, scaleX * Settings.scale, scaleY * Settings.scale, rotation, 0, 0, srcWidth, srcHeight, flipX, flipY);

        if (foreground != null)
        {
            final float scale = foreground.scale * Settings.scale;
            final int s_w = foreground.texture.getWidth();
            final int s_h = foreground.texture.getHeight();
            sb.setColor(foreground.color != null ? foreground.color : targetColor);
            sb.draw(foreground.texture, x, y, width/2f, height/2f, width, height, scaleX * scale, scaleY * scale, rotation, 0, 0, s_w, s_h, flipX, flipY);
        }
        sb.setBlendFunction(770, 771);
    }

    public void renderBicubic(SpriteBatch sb, float x, float y, float width, float height) {

    }

    public boolean tryRenderCentered(SpriteBatch sb)
    {
        if (isActive)
        {
            this.hb.render(sb);
            renderCentered(sb);
        }

        return isActive;
    }
}