package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.ui.hitboxes.EUIHitbox;
import org.apache.commons.lang3.math.NumberUtils;

public class EUITextBoxNumericalInput extends EUITextBoxReceiver<Integer>
{
    protected int cachedValue;
    protected int min = Integer.MIN_VALUE;
    protected int max = Integer.MAX_VALUE;

    public EUITextBoxNumericalInput(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
    }

    public EUITextBoxNumericalInput setLimits(int min, int max)
    {
        this.min = min;
        this.max = Math.max(max, min);
        return this;
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c) && Character.isDigit(c);
    }

    @Override
    public void start() {
        super.start();
        if (!NumberUtils.isCreatable(label.text)) {
            label.text = "";
            cachedValue = 0;
        }
        else
        {
            cachedValue = getValue(label.text);
        }
    }

    @Override
    public void setText(String s) {
        super.setText(s);
        cachedValue = getValue(label.text);
    }

    protected void setText(int value)
    {
        setText(String.valueOf(value));
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        if (isEditing)
        {
            if (InputHelper.scrolledDown)
            {
                setText(cachedValue - 1);
            }
            else if (InputHelper.scrolledUp)
            {
                setText(cachedValue + 1);
            }
        }
    }

    @Override
    protected void renderUnderscore(SpriteBatch sb, float cur_x) {
        // Do not render
    }

    @Override
    public Integer getValue(String text)
    {
        try
        {
            return MathUtils.clamp(Integer.parseInt(label.text), min, max);
        }
        catch (Exception ignored)
        {
            return 0;
        }
    }
}
