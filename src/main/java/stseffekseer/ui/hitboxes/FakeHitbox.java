package stseffekseer.ui.hitboxes;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class FakeHitbox extends AdvancedHitbox
{
    public FakeHitbox(Hitbox hb) {
        super(hb);
    }

    public FakeHitbox(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void update()
    {
        this.update(this.x, this.y);
        if (cX != target_cX || cY != target_cY)
        {
            moveInternal(Lerp(cX, target_cX), Lerp(cY, target_cY));
        }
    }

    @Override
    public void update(float x, float y) {
        if (!AbstractDungeon.isFadingOut) {
            this.x = x;
            this.y = y;
        }
    }
}
