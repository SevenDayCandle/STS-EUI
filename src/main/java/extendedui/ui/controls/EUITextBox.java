package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUITextBox extends EUIHoverable
{
    public final EUIImage image;
    public final EUILabel label;

    public EUITextBox(Texture backgroundTexture, EUIHitbox hb)
    {
        super(hb);
        this.label = new EUILabel(FontHelper.buttonLabelFont);
        this.image = new EUIImage(backgroundTexture);
    }

    public EUITextBox setLabel(Object value)
    {
        this.label.setLabel(String.valueOf(value));

        return this;
    }

    public EUITextBox setLabel(String text)
    {
        this.label.setLabel(text);

        return this;
    }

    public EUITextBox setLabel(String format, Object... args)
    {
        this.label.setLabel(format, args);

        return this;
    }

    public EUITextBox setFont(BitmapFont font, float fontScale)
    {
        this.label.setFont(font, fontScale);

        return this;
    }

    public EUITextBox setAlignment(float verticalRatio, float horizontalRatio)
    {
        return setAlignment(verticalRatio, horizontalRatio, false);
    }

    public EUITextBox setAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        this.label.setAlignment(verticalRatio, horizontalRatio, smartText);

        return this;
    }

    public EUITextBox setAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.label.setAlignment(verticalRatio, horizontalRatio, smartText, smartPadEnd);

        return this;
    }

    public EUITextBox setBackgroundTexture(Texture texture, Color color, float scale)
    {
        this.image.setBackgroundTexture(texture, color, scale);

        return this;
    }

    public EUITextBox setBackgroundTexture(Texture texture)
    {
        this.image.setBackgroundTexture(texture);

        return this;
    }

    public EUITextBox setColors(Color backgroundColor, Color textColor)
    {
        this.image.setColor(backgroundColor);
        this.label.setColor(textColor);

        return this;
    }

    public EUITextBox setPosition(float x, float y)
    {
        this.hb.move(x, y);

        return this;
    }

    public EUITextBox setFontColor(Color textColor)
    {
        this.label.setColor(textColor);

        return this;
    }

    public EUITextBox autosize() {
        return autosize(1f, 1f);
    }

    public EUITextBox autosize(Float resizeMultiplier, Float resizeHeight) {
        if (resizeMultiplier != null) {
            this.hb.width = label.getAutoWidth();
        }
        if (resizeHeight != null) {
            this.hb.height = label.getAutoHeight();
        }

        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        image.render(sb, hb);
        label.render(sb, hb);

        hb.render(sb);
    }
}
