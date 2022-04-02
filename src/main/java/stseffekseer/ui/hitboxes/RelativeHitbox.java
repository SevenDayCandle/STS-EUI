package stseffekseer.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class RelativeHitbox extends AdvancedHitbox
{
    public Hitbox parentHB;
    public float offset_cX;
    public float offset_cY;
    public boolean percentageOffset;

    public RelativeHitbox(Hitbox hb, float width, float height)
    {
        this(hb, width, height, 0, 0, false);
    }

    public RelativeHitbox(Hitbox hb, float percentWidth, float percentHeight, float percent_cX, float percent_cY)
    {
        this(hb, percentWidth * hb.width, percentHeight * hb.height, percent_cX, percent_cY, true);
    }

    public RelativeHitbox(Hitbox hb, float width, float height, float offset_cX, float offset_cY, boolean percentageOffset)
    {
        super(hb.x, hb.y, width, height);

        this.parentHB = hb;
        this.percentageOffset = percentageOffset;
        this.offset_cX = offset_cX;
        this.offset_cY = offset_cY;
        this.lerpSpeed = -1;

        UpdateTargetPosition();
        moveInternal(target_cX, target_cY);
    }

    public RelativeHitbox SetOffset(float x, float y)
    {
        this.percentageOffset = false;
        this.offset_cX = x;
        this.offset_cY = y;

        return this;
    }

    public RelativeHitbox SetPercentageOffset(float x, float y)
    {
        this.percentageOffset = true;
        this.offset_cX = x;
        this.offset_cY = y;

        return this;
    }

    public RelativeHitbox UpdateTargetPosition()
    {
        if (percentageOffset)
        {
            this.target_cX = parentHB.x + (offset_cX * parentHB.width);
            this.target_cY = parentHB.y + (offset_cY * parentHB.height);
        }
        else
        {
            this.target_cX = parentHB.x + offset_cX;
            this.target_cY = parentHB.y + offset_cY;
        }

        return this;
    }

    @Override
    public void update()
    {
        super.update();

        UpdateTargetPosition();
    }

    public static RelativeHitbox SetPercentageOffset(Hitbox hb, Float x, Float y)
    {
        RelativeHitbox hitbox = (RelativeHitbox)hb;
        hitbox.SetPercentageOffset(x == null ? hitbox.offset_cX : x, y == null ? hitbox.offset_cY : y);
        return hitbox;
    }
}
