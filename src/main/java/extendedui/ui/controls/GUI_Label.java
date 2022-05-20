package extendedui.ui.controls;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.JavaUtils;
import extendedui.ui.GUI_Hoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class GUI_Label extends GUI_Hoverable
{
    public String text;
    public boolean smartText;
    public Color textColor;
    public float verticalRatio;
    public float horizontalRatio;
    public float fontScale;
    protected BitmapFont font;
    private boolean smartPadEnd;

    public GUI_Label(BitmapFont font)
    {
        this(font, new AdvancedHitbox(0, 0));
    }

    public GUI_Label(BitmapFont font, AdvancedHitbox hb)
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

    public GUI_Label MakeCopy()
    {
        return new GUI_Label(font, new AdvancedHitbox(hb))
                .SetAlignment(verticalRatio, horizontalRatio, smartText)
                .SetColor(textColor)
                .SetFont(font, fontScale)
                .SetText(text);
    }

    public GUI_Label SetText(Object content)
    {
        this.text = String.valueOf(content);

        return this;
    }

    public GUI_Label SetText(String text)
    {
        this.text = text;

        return this;
    }

    public GUI_Label SetText(String format, Object... args)
    {
        this.text = JavaUtils.Format(format, args);

        return this;
    }

    public GUI_Label SetFont(BitmapFont font)
    {
        return SetFont(font, 1);
    }

    public GUI_Label SetFont(BitmapFont font, float fontScale)
    {
        this.font = font;
        this.fontScale = fontScale;

        return this;
    }

    public GUI_Label SetFontScale(float fontScale)
    {
        this.fontScale = fontScale;

        return this;
    }

    public GUI_Label SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public GUI_Label SetAlignment(float verticalRatio, float horizontalRatio)
    {
        return SetAlignment(verticalRatio, horizontalRatio, false);
    }

    public GUI_Label SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        return SetAlignment(verticalRatio, horizontalRatio, smartText, true);
    }

    public GUI_Label SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;

        return this;
    }

    public GUI_Label SetSmartText(boolean smartText) {
        return SetSmartText(smartText, true);
    }

    public GUI_Label SetSmartText(boolean smartText, boolean smartPadEnd) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        return this;
    }

    public GUI_Label SetColor(Color textColor)
    {
        this.textColor = textColor.cpy();

        return this;
    }

    public float GetAutoHeight() {
        return EUIRenderHelpers.GetSmartHeight(font, text, Settings.WIDTH);
    }

    public float GetAutoWidth() {
        return EUIRenderHelpers.GetSmartWidth(font, text, Settings.WIDTH, 0f);
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
            EUIRenderHelpers.WriteSmartText(sb, font, text, hb.x + step, hb.y + (hb.height * verticalRatio),
            smartPadEnd ? hb.width - (step * 2) : hb.width, font.getLineHeight(), textColor);
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
