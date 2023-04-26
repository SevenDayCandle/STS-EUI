package extendedui.ui.hitboxes;

import com.megacrit.cardcrawl.helpers.Hitbox;

public class RelativeHitbox extends EUIHitbox {
    public Hitbox parentHB;
    public float offsetX;
    public float offsetY;

    public RelativeHitbox(Hitbox hb) {
        this(hb, hb.width, hb.height, 0, 0);
    }

    public RelativeHitbox(Hitbox hb, float width, float height, float offsetX, float offsetY) {
        super(hb.x, hb.y, width, height);

        this.parentHB = hb;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.lerpSpeed = -1;

        updateTargetPosition();
        moveInternal(targetCx, targetCy);
    }

    public RelativeHitbox updateTargetPosition() {
        this.targetCx = parentHB.x + offsetX;
        this.targetCy = parentHB.y + offsetY;

        return this;
    }

    public RelativeHitbox(Hitbox hb, float width, float height) {
        this(hb, width, height, 0, 0);
    }

    public static RelativeHitbox fromPercentages(Hitbox hb, float percentWidth, float percentHeight) {
        return new RelativeHitbox(hb, percentWidth * hb.width, percentHeight * hb.height);
    }

    public static RelativeHitbox fromPercentages(Hitbox hb, float percentWidth, float percentHeight, float percentOffsetX, float percentOffsetY) {
        return new RelativeHitbox(hb, percentWidth * hb.width, percentHeight * hb.height, percentOffsetX * hb.width, percentOffsetY * hb.height);
    }

    public float getOffsetX() {
        return offsetX;
    }

    public RelativeHitbox setOffsetX(float x) {
        this.offsetX = x;
        updateTargetPosition();
        moveInternal(targetCx, targetCy);

        return this;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public RelativeHitbox setOffsetY(float y) {
        this.offsetY = y;
        updateTargetPosition();
        moveInternal(targetCx, targetCy);

        return this;
    }

    public RelativeHitbox makeCopy() {
        RelativeHitbox copy = new RelativeHitbox(this.parentHB, width, height, offsetX, offsetY);
        copy.lerpSpeed = this.lerpSpeed;
        copy.parentElement = this.parentElement;
        copy.isPopupCompatible = this.isPopupCompatible;

        return copy;
    }

    public RelativeHitbox setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
        updateTargetPosition();
        moveInternal(targetCx, targetCy);

        return this;
    }

    @Override
    public void update() {
        super.update();

        updateTargetPosition();
    }
}
