package extendedui.ui.hitboxes;

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

        updateTargetPosition();
        moveInternal(targetCx, targetCy);
    }

    public RelativeHitbox setOffset(float x, float y)
    {
        this.percentageOffset = false;
        this.offset_cX = x;
        this.offset_cY = y;

        return this;
    }

    public RelativeHitbox setPercentageOffset(float x, float y)
    {
        this.percentageOffset = true;
        this.offset_cX = x;
        this.offset_cY = y;

        return this;
    }

    public RelativeHitbox updateTargetPosition()
    {
        if (percentageOffset)
        {
            this.targetCx = parentHB.x + (offset_cX * parentHB.width);
            this.targetCy = parentHB.y + (offset_cY * parentHB.height);
        }
        else
        {
            this.targetCx = parentHB.x + offset_cX;
            this.targetCy = parentHB.y + offset_cY;
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
        hitbox.setPercentageOffset(x == null ? hitbox.offset_cX : x, y == null ? hitbox.offset_cY : y);
        return hitbox;
    }
}
