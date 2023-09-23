package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIRenderHelpers;

public class EUIBlightGrid extends EUIItemGrid<AbstractBlight> {

    public EUIBlightGrid() {
        this(0.5f, true);
    }

    public EUIBlightGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
    }

    public EUIBlightGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public EUIBlightGrid add(AbstractBlight blight) {
        super.add(blight);
        blight.scale = startingScale;

        return this;
    }

    @Override
    public void forceUpdateItemPosition(AbstractBlight blight, float x, float y) {
        blight.currentX = blight.targetX = x;
        blight.currentY = blight.targetY = y;
        blight.hb.update();
        blight.hb.move(blight.currentX, blight.currentY);
    }

    @Override
    public Hitbox getHitbox(AbstractBlight item) {
        return item.hb;
    }

    @Override
    protected float getScrollDistance(AbstractBlight blight, int index) {
        float scrollDistance = 1f / getRowCount();
        if (blight.targetY > drawTopY) {
            return -scrollDistance;
        }
        else if (blight.targetY < 0) {
            return scrollDistance;
        }
        return 0;
    }

    @Override
    protected void renderTip(SpriteBatch sb) {
        if (hovered != null) {
            hovered.renderTip(sb);
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, AbstractBlight blight) {
        blight.render(sb, false, Color.BLACK);
    }

    @Override
    protected void updateHoverLogic(AbstractBlight blight, int i) {
        blight.hb.update();
        blight.hb.move(blight.currentX, blight.currentY);

        if (blight.hb.hovered) {

            hovered = blight;
            hoveredIndex = i;
            if (shouldEnlargeHovered) {
                blight.scale = MathHelper.scaleLerpSnap(blight.scale, scale(hoveredScale));
            }
        }
        else {
            blight.scale = MathHelper.scaleLerpSnap(blight.scale, scale(targetScale));
        }
    }

    @Override
    public void updateItemPosition(AbstractBlight blight, float x, float y) {
        blight.targetX = x;
        blight.targetY = y;
        blight.currentX = EUIRenderHelpers.lerpSnap(blight.currentX, blight.targetX, LERP_SPEED);
        blight.currentY = EUIRenderHelpers.lerpSnap(blight.currentY, blight.targetY, LERP_SPEED);
    }
}
