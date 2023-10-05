package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.text.EUITextHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;

public class EUILabel extends EUIHoverable {
    private boolean smartPadEnd;
    protected float originalFontScale;
    protected BitmapFont font;
    public String text;
    public boolean smartText;
    public boolean smartTextResize;
    public Color textColor;
    public float verticalRatio;
    public float horizontalRatio;
    public float fontScale;

    public EUILabel(BitmapFont font) {
        this(font, new EUIHitbox(0, 0));
    }

    public EUILabel(BitmapFont font, EUIHitbox hb) {
        this(font, hb, 1);
    }

    public EUILabel(BitmapFont font, EUIHitbox hb, float scale) {
        this(font, hb, scale, 0.85f, 0.1f, false);
    }

    public EUILabel(BitmapFont font, EUIHitbox hb, float scale, float verticalRatio, float horizontalRatio, boolean smartText) {
        super(hb);
        this.smartText = smartText;
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        this.textColor = Color.WHITE;
        this.originalFontScale = this.fontScale = scale;
        this.font = font;
        this.text = "";
    }

    public EUILabel autosize() {
        return autosize(1f, 1f);
    }

    public EUILabel autosize(Float resizeMultiplier, Float resizeHeight) {
        if (resizeMultiplier != null) {
            this.hb.width = getAutoWidth();
        }
        if (resizeHeight != null) {
            this.hb.height = getAutoHeight();
        }

        return this;
    }

    public float getAutoHeight() {
        return EUITextHelper.getSmartHeight(font, text, Settings.WIDTH);
    }

    public float getAutoWidth() {
        return EUITextHelper.getSmartWidth(font, text, Settings.WIDTH, 0f);
    }

    public float getOriginalFontScale() {
        return originalFontScale;
    }

    public EUILabel makeCopy() {
        return new EUILabel(font, new EUIHitbox(hb))
                .setAlignment(verticalRatio, horizontalRatio, smartText)
                .setColor(textColor)
                .setFont(font, fontScale)
                .setLabel(text)
                .setTooltip(tooltip);
    }

    public EUITourTooltip makeTour(boolean canDismiss) {
        if (tooltip != null) {
            EUITourTooltip tip = new EUITourTooltip(hb, tooltip.title, tooltip.description);
            tip.setCanDismiss(canDismiss);
            return tip;
        }
        return null;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        renderImpl(sb, hb);
        hb.render(sb);
    }

    public void renderImpl(SpriteBatch sb, Hitbox hb) {
        renderImpl(sb, hb, textColor);
    }

    public void renderImpl(SpriteBatch sb, Hitbox hb, Color textColor) {
        renderImpl(sb, hb, textColor, text);
    }

    public void renderImpl(SpriteBatch sb, Hitbox hb, Color textColor, CharSequence text) {
        font.getData().setScale(fontScale);

        if (smartText) {
            final float step = hb.width * horizontalRatio;
            EUITextHelper.renderSmart(sb, font, text, hb.x + step, hb.y + (hb.height * verticalRatio),
                    smartPadEnd ? hb.width - (step * 2) : hb.width, font.getLineHeight(), textColor, smartTextResize);
        }
        else if (horizontalRatio < 0.5f) {
            final float step = hb.width * horizontalRatio;
            EUITextHelper.renderFontLeft(sb, font, text, hb.x + step, hb.y + hb.height * verticalRatio, textColor);
        }
        else if (horizontalRatio > 0.5f) {
            final float step = hb.width * (1 - horizontalRatio) * 2;
            EUITextHelper.renderFontRightAligned(sb, font, text, hb.x + hb.width - step, hb.y + hb.height * verticalRatio, textColor);
        }
        else {
            EUITextHelper.renderFontCentered(sb, font, text, hb.cX, hb.y + hb.height * verticalRatio, textColor);
        }

        EUIRenderHelpers.resetFont(font);
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio) {
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        return this;
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio, boolean smartText) {
        return setAlignment(verticalRatio, horizontalRatio, smartText, true);
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd) {
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;

        return this;
    }

    public EUILabel setColor(Color textColor) {
        this.textColor = textColor.cpy();

        return this;
    }

    public EUILabel setFont(BitmapFont font, float fontScale) {
        this.font = font;
        this.originalFontScale = this.fontScale = fontScale;

        return this;
    }

    public EUILabel setFont(BitmapFont font) {
        return setFont(font, 1);
    }

    public EUILabel setFontScale(float fontScale) {
        this.originalFontScale = this.fontScale = fontScale;

        return this;
    }

    public EUILabel setFontScaleRelative(float relativeSize) {
        this.fontScale = originalFontScale * relativeSize;

        return this;
    }

    public EUILabel setLabel(String text) {
        this.text = text;

        return this;
    }

    public EUILabel setLabel(Object content) {
        this.text = String.valueOf(content);

        return this;
    }

    public EUILabel setLabel(String format, Object... args) {
        this.text = EUIUtils.format(format, args);

        return this;
    }

    public EUILabel setPosition(float cX, float cY) {
        this.hb.move(cX, cY);

        return this;
    }

    public EUILabel setSmartText(boolean smartText) {
        return setSmartText(smartText, true);
    }

    public EUILabel setSmartText(boolean smartText, boolean smartPadEnd) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        return this;
    }

    public EUILabel setSmartText(boolean smartText, boolean smartPadEnd, boolean smartTextResize) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        this.smartTextResize = smartTextResize;
        return this;
    }

    public EUILabel setTooltip(String title, String description) {
        return setTooltip(new EUITooltip(title, description));
    }

    public EUILabel setTooltip(EUITooltip tooltip) {
        super.setTooltip(tooltip);

        return this;
    }
}
