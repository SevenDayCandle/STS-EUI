package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class EUITextBox extends EUIHoverable
{
    public final EUIImage image;
    public final EUILabel label;

    public EUITextBox(Texture backgroundTexture, AdvancedHitbox hb)
    {
        super(hb);
        this.label = new EUILabel(FontHelper.buttonLabelFont);
        this.image = new EUIImage(backgroundTexture);
    }

    public EUITextBox SetText(Object value)
    {
        this.label.SetText(String.valueOf(value));

        return this;
    }

    public EUITextBox SetText(String text)
    {
        this.label.SetText(text);

        return this;
    }

    public EUITextBox SetText(String format, Object... args)
    {
        this.label.SetText(format, args);

        return this;
    }

    public EUITextBox SetFont(BitmapFont font, float fontScale)
    {
        this.label.SetFont(font, fontScale);

        return this;
    }

    public EUITextBox SetAlignment(float verticalRatio, float horizontalRatio)
    {
        return SetAlignment(verticalRatio, horizontalRatio, false);
    }

    public EUITextBox SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        this.label.SetAlignment(verticalRatio, horizontalRatio, smartText);

        return this;
    }

    public EUITextBox SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.label.SetAlignment(verticalRatio, horizontalRatio, smartText, smartPadEnd);

        return this;
    }

    public EUITextBox SetBackgroundTexture(Texture texture, Color color, float scale)
    {
        this.image.SetBackgroundTexture(texture, color, scale);

        return this;
    }

    public EUITextBox SetBackgroundTexture(Texture texture)
    {
        this.image.SetBackgroundTexture(texture);

        return this;
    }

    public EUITextBox SetColors(Color backgroundColor, Color textColor)
    {
        this.image.SetColor(backgroundColor);
        this.label.SetColor(textColor);

        return this;
    }

    public EUITextBox SetPosition(float x, float y)
    {
        this.hb.move(x, y);

        return this;
    }

    public EUITextBox SetFontColor(Color textColor)
    {
        this.label.SetColor(textColor);

        return this;
    }

    public EUITextBox Autosize() {
        return Autosize(1f, 1f);
    }

    public EUITextBox Autosize(Float resizeMultiplier, Float resizeHeight) {
        if (resizeMultiplier != null) {
            this.hb.width = label.GetAutoWidth();
        }
        if (resizeHeight != null) {
            this.hb.height = label.GetAutoHeight();
        }

        return this;
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        image.Render(sb, hb);
        label.Render(sb, hb);

        hb.render(sb);
    }
}
