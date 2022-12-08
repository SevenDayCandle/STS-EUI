package extendedui.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class PercentageRelativeHitbox extends RelativeHitbox
{
    public PercentageRelativeHitbox(Hitbox hb, float percentWidth, float percentHeight)
    {
        this(hb, percentWidth, percentHeight, 0, 0);
    }

    public PercentageRelativeHitbox(Hitbox hb, float percentWidth, float percentHeight, float percentOffsetX, float percentOffsetY)
    {
        super(hb, percentWidth * hb.width, percentHeight * hb.height, percentOffsetX, percentOffsetY);
    }

    public PercentageRelativeHitbox setOffset(float x, float y)
    {
        super.setOffset(x, y);

        return this;
    }

    public PercentageRelativeHitbox setOffsetX(float x)
    {
        super.setOffsetX(x);

        return this;
    }

    public PercentageRelativeHitbox setOffsetY(float y)
    {
        super.setOffsetY(y);

        return this;
    }

    public PercentageRelativeHitbox updateTargetPosition()
    {
        this.targetCx = parentHB.x + (offsetX * parentHB.width);
        this.targetCy = parentHB.y + (offsetY * parentHB.height);

        return this;
    }
}
