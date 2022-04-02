package stseffekseer.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import stseffekseer.ui.GUI_Hoverable;
import stseffekseer.ui.hitboxes.AdvancedHitbox;

public class GUI_TextBox extends GUI_Hoverable
{
    public final GUI_Image image;
    public final GUI_Label label;

    public GUI_TextBox(Texture backgroundTexture, AdvancedHitbox hb)
    {
        super(hb);
        this.label = new GUI_Label(FontHelper.buttonLabelFont);
        this.image = new GUI_Image(backgroundTexture);
    }

    public GUI_TextBox SetText(Object value)
    {
        this.label.SetText(String.valueOf(value));

        return this;
    }

    public GUI_TextBox SetText(String text)
    {
        this.label.SetText(text);

        return this;
    }

    public GUI_TextBox SetText(String format, Object... args)
    {
        this.label.SetText(format, args);

        return this;
    }

    public GUI_TextBox SetFont(BitmapFont font, float fontScale)
    {
        this.label.SetFont(font, fontScale);

        return this;
    }

    public GUI_TextBox SetAlignment(float verticalRatio, float horizontalRatio)
    {
        return SetAlignment(verticalRatio, horizontalRatio, false);
    }

    public GUI_TextBox SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        this.label.SetAlignment(verticalRatio, horizontalRatio, smartText);

        return this;
    }

    public GUI_TextBox SetAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.label.SetAlignment(verticalRatio, horizontalRatio, smartText, smartPadEnd);

        return this;
    }

    public GUI_TextBox SetBackgroundTexture(Texture texture, Color color, float scale)
    {
        this.image.SetBackgroundTexture(texture, color, scale);

        return this;
    }

    public GUI_TextBox SetBackgroundTexture(Texture texture)
    {
        this.image.SetBackgroundTexture(texture);

        return this;
    }

    public GUI_TextBox SetColors(Color backgroundColor, Color textColor)
    {
        this.image.SetColor(backgroundColor);
        this.label.SetColor(textColor);

        return this;
    }

    public GUI_TextBox SetPosition(float x, float y)
    {
        this.hb.move(x, y);

        return this;
    }

    public GUI_TextBox SetFontColor(Color textColor)
    {
        this.label.SetColor(textColor);

        return this;
    }

    public GUI_TextBox Autosize() {
        return Autosize(1f, 1f);
    }

    public GUI_TextBox Autosize(Float resizeMultiplier, Float resizeHeight) {
        if (resizeMultiplier != null) {
            this.hb.width = label.GetAutoWidth();
        }
        if (resizeHeight != null) {
            this.hb.height = label.GetAutoHeight();
        }

        return this;
    }

    @Override
    public void Update()
    {
        hb.update();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        image.Render(sb, hb);
        label.Render(sb, hb);

        hb.render(sb);
    }
}
