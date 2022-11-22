package extendedui.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class RelativeHitbox extends AdvancedHitbox
{
    public Hitbox parentHB;
    public float offsetCx;
    public float offsetCy;
    public boolean percentageOffset;

    public RelativeHitbox(Hitbox hb, float width, float height)
    {
        this(hb, width, height, 0, 0, false);
    }

    public RelativeHitbox(Hitbox hb, float percentWidth, float percentHeight, float percent_cX, float percent_cY)
    {
        this(hb, percentWidth * hb.width, percentHeight * hb.height, percent_cX, percent_cY, true);
    }

    public RelativeHitbox(Hitbox hb, float width, float height, float offsetCx, float offsetCy, boolean percentageOffset)
    {
        super(hb.x, hb.y, width, height);

        this.parentHB = hb;
        this.percentageOffset = percentageOffset;
        this.offsetCx = offsetCx;
        this.offsetCy = offsetCy;
        this.lerpSpeed = -1;

        updateTargetPosition();
        moveInternal(targetCx, targetCy);
    }

    public RelativeHitbox setOffset(float x, float y)
    {
        this.percentageOffset = false;
        this.offsetCx = x;
        this.offsetCy = y;

        return this;
    }

    public RelativeHitbox setPercentageOffset(float x, float y)
    {
        this.percentageOffset = true;
        this.offsetCx = x;
        this.offsetCy = y;

        return this;
    }

    public RelativeHitbox updateTargetPosition()
    {
        if (percentageOffset)
        {
            this.targetCx = parentHB.x + (offsetCx * parentHB.width);
            this.targetCy = parentHB.y + (offsetCy * parentHB.height);
        }
        else
        {
            this.targetCx = parentHB.x + offsetCx;
            this.targetCy = parentHB.y + offsetCy;
        }

        return this;
    }

    @Override
    public void update()
    {
        super.update();

        updateTargetPosition();
    }

    public static RelativeHitbox setPercentageOffset(Hitbox hb, Float x, Float y)
    {
        RelativeHitbox hitbox = (RelativeHitbox)hb;
        hitbox.setPercentageOffset(x == null ? hitbox.offsetCx : x, y == null ? hitbox.offsetCy : y);
        return hitbox;
    }
}
