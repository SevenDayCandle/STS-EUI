package extendedui.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class OriginRelativeHitbox extends RelativeHitbox {
    public OriginRelativeHitbox(Hitbox hb) {
        this(hb, hb.width, hb.height, 0, 0);
    }

    public OriginRelativeHitbox(Hitbox hb, float width, float height, float offsetX, float offsetY) {
        super(hb, width, height, offsetX, offsetY);
    }

    public OriginRelativeHitbox(Hitbox hb, float width, float height) {
        this(hb, width, height, 0, 0);
    }

    public static OriginRelativeHitbox fromPercentages(Hitbox hb, float percentWidth, float percentHeight) {
        return new OriginRelativeHitbox(hb, percentWidth * hb.width, percentHeight * hb.height);
    }

    public static OriginRelativeHitbox fromPercentages(Hitbox hb, float percentWidth, float percentHeight, float percentOffsetX, float percentOffsetY) {
        return new OriginRelativeHitbox(hb, percentWidth * hb.width, percentHeight * hb.height, percentOffsetX * hb.width, percentOffsetY * hb.height);
    }

    public OriginRelativeHitbox makeCopy() {
        OriginRelativeHitbox copy = new OriginRelativeHitbox(this.parentHB, width, height, offsetX, offsetY);
        copy.lerpSpeed = this.lerpSpeed;
        copy.parentElement = this.parentElement;
        copy.isPopupCompatible = this.isPopupCompatible;

        return copy;
    }

    public OriginRelativeHitbox setOffset(float x, float y) {
        super.setOffset(x, y);

        return this;
    }

    public OriginRelativeHitbox setOffsetX(float x) {
        super.setOffsetX(x);

        return this;
    }

    public OriginRelativeHitbox setOffsetY(float y) {
        super.setOffsetY(y);

        return this;
    }

    public OriginRelativeHitbox updateTargetPosition() {
        this.targetCx = parentHB.x + width / 2f + offsetX;
        this.targetCy = parentHB.y + height / 2f + offsetY;

        return this;
    }
}
