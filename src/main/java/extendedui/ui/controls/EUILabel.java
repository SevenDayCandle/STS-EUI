package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.JavaUtils;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.text.EUISmartText;

public class EUILabel extends EUIHoverable
{
    public String text;
    public boolean smartText;
    public boolean smartTextResize;
    public Color textColor;
    public float verticalRatio;
    public float horizontalRatio;
    public float fontScale;
    protected BitmapFont font;
    private boolean smartPadEnd;

    public EUILabel(BitmapFont font)
    {
        this(font, new AdvancedHitbox(0, 0));
    }

    public EUILabel(BitmapFont font, AdvancedHitbox hb)
    {
        super(hb);
        this.smartText = true;
        this.verticalRatio = 0.85f;
        this.horizontalRatio = 0.1f;
        this.textColor = Color.WHITE;
        this.fontScale = 1;
        this.font = font;
        this.text = "-";
    }

    public EUILabel MakeCopy()
    {
        return new EUILabel(font, new AdvancedHitbox(hb))
                .SetAlignment(verticalRatio, horizontalRatio, smartText)
                .SetColor(textColor)
                .SetFont(font, fontScale)
                .SetText(text);
    }

    public EUILabel SetText(Object content)
    {
        this.text = String.valueOf(content);

        return this;
    }

    public EUILabel SetText(String text)
    {
        this.text = text;

        return this;
    }

    public EUILabel SetText(String format, Object... args)
    {
        this.text = JavaUtils.Format(format, args);

        return this;
    }

    public EUILabel SetFont(BitmapFont font)
    {
        return SetFont(font, 1);
    }

    public EUILabel SetFont(BitmapFont font, float fontScale)
    {
        this.font = font;
        this.fontScale = fontScale;

        return this;
    }

    public EUILabel SetFontScale(float fontScale)
    {
        this.fontScale = fontScale;

        return this;
    }

    public EUILabel SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUILabel SetAlignment(float verticalRatio, float horizontalRatio)
    {
        return SetAlignment(verticalRatio, horizontalRatio, false);
    }

    public EUILabel SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        return SetAlignment(verticalRatio, horizontalRatio, smartText, true);
    }

    public EUILabel SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;

        return this;
    }

    public EUILabel SetSmartText(boolean smartText) {
        return SetSmartText(smartText, true);
    }

    public EUILabel SetSmartText(boolean smartText, boolean smartPadEnd) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        return this;
    }

    public EUILabel SetSmartText(boolean smartText, boolean smartPadEnd, boolean smartTextResize) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        this.smartTextResize = smartTextResize;
        return this;
    }

    public EUILabel SetColor(Color textColor)
    {
        this.textColor = textColor.cpy();

        return this;
    }

    public float GetAutoHeight() {
        return EUISmartText.GetSmartHeight(font, text, Settings.WIDTH);
    }

    public float GetAutoWidth() {
        return EUISmartText.GetSmartWidth(font, text, Settings.WIDTH, 0f);
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        Render(sb, hb);

        hb.render(sb);
    }

    public void Render(SpriteBatch sb, Hitbox hb)
    {
        font.getData().setScale(fontScale);

        if (smartText)
        {
            final float step = hb.width * horizontalRatio;
            EUISmartText.Write(sb, font, text, hb.x + step, hb.y + (hb.height * verticalRatio),
            smartPadEnd ? hb.width - (step * 2) : hb.width, font.getLineHeight(), textColor, smartTextResize);
        }
        else if (horizontalRatio < 0.5f)
        {
            final float step = hb.width * horizontalRatio;
            FontHelper.renderFontLeft(sb, font, text, hb.x + step, hb.y + hb.height * verticalRatio, textColor);
        }
        else if (horizontalRatio > 0.5f)
        {
            final float step = hb.width * (1-horizontalRatio) * 2;
            FontHelper.renderFontRightAligned(sb, font, text, hb.x + hb.width - step, hb.y + hb.height * verticalRatio, textColor);
        }
        else
        {
            FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.y + hb.height * verticalRatio, textColor);
        }

        EUIRenderHelpers.ResetFont(font);
    }
}
