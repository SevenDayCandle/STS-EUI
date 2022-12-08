package extendedui.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class RelativeHitbox extends EUIHitbox
{
    public Hitbox parentHB;
    public float offsetX;
    public float offsetY;

    public RelativeHitbox(Hitbox hb)
    {
        this(hb, hb.width, hb.height, 0, 0);
    }

    public RelativeHitbox(Hitbox hb, float width, float height)
    {
        this(hb, width, height, 0, 0);
    }

    public RelativeHitbox(Hitbox hb, float width, float height, float offsetX, float offsetY)
    {
        super(hb.x, hb.y, width, height);

        this.parentHB = hb;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.lerpSpeed = -1;

        updateTargetPosition();
        moveInternal(targetCx, targetCy);
    }

    public RelativeHitbox setOffset(float x, float y)
    {
        this.offsetX = x;
        this.offsetY = y;

        return this;
    }

    public RelativeHitbox setOffsetX(float x)
    {
        this.offsetX = x;

        return this;
    }

    public RelativeHitbox setOffsetY(float y)
    {
        this.offsetY = y;

        return this;
    }

    public RelativeHitbox updateTargetPosition()
    {
        this.targetCx = parentHB.x + offsetX;
        this.targetCy = parentHB.y + offsetY;

        return this;
    }

    @Override
    public void update()
    {
        super.update();

        updateTargetPosition();
    }
}
