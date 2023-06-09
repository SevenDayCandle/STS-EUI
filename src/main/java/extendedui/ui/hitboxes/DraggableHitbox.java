package extendedui.ui.hitboxes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;

public class DraggableHitbox extends EUIHitbox {
    protected Vector2 dragStart = null;
    protected ActionT1<DraggableHitbox> onDragFinish;
    public boolean canDrag;
    public float minY;
    public float maxY;
    public float minX;
    public float maxX;

    public DraggableHitbox(Hitbox hb) {
        this(hb.x, hb.y, hb.width, hb.height, true);
    }

    public DraggableHitbox(float x, float y, float width, float height, boolean canDrag) {
        super(x, y, width, height);

        this.canDrag = canDrag;
        this.minX = -width * 0.25f;
        this.maxX = Settings.WIDTH + (width * 0.25f);
        this.minY = -height * 0.25f;
        this.maxY = Settings.HEIGHT + (height * 0.25f);
    }

    public DraggableHitbox(float width, float height) {
        this(-9999, -9999, width, height, true);
    }

    public DraggableHitbox(Hitbox hb, float width, float height) {
        this(hb.x, hb.y, width, height, true);
    }

    public DraggableHitbox(float x, float y, float width, float height) {
        this(x, y, width, height, true);
    }

    public void forceStartDrag() {
        if (EUI.tryDragging()) {
            dragStart = new Vector2(Gdx.input.getX(), Settings.HEIGHT - Gdx.input.getY());
        }
    }

    public boolean isDragging() {
        return dragStart != null;
    }

    public DraggableHitbox makeCopy() {
        DraggableHitbox copy = new DraggableHitbox(x, y, width, height, canDrag);
        copy.lerpSpeed = this.lerpSpeed;
        copy.parentElement = this.parentElement;
        copy.isPopupCompatible = this.isPopupCompatible;
        copy.onDragFinish = this.onDragFinish;

        return copy;
    }

    public DraggableHitbox setCenter(float cX, float cY) {
        move(cX, cY);

        return this;
    }

    @Override
    public void update() {
        super.update();

        if (canDrag) {
            float mX = Gdx.input.getX();
            float mY = Settings.HEIGHT - Gdx.input.getY();

            if (hovered || dragStart != null) {
                if (InputHelper.justClickedLeft) {
                    if (EUI.tryDragging()) {
                        dragStart = new Vector2(mX, mY);
                        return;
                    }
                }
                else if (!InputHelper.justReleasedClickLeft && dragStart != null) {
                    targetCx = Math.min(maxX, Math.max(minX, targetCx + (mX - dragStart.x)));
                    targetCy = Math.min(maxY, Math.max(minY, targetCy + (mY - dragStart.y)));

                    if (EUI.tryDragging()) {
                        dragStart.set(mX, mY);
                        return;
                    }
                }
            }
        }

        if (dragStart != null) {
            if (Settings.isDebug) {
                float xPercentage = x * 100f / Settings.WIDTH;
                float yPercentage = y * 100f / Settings.HEIGHT;
                float cxPercentage = cX * 100f / Settings.WIDTH;
                float cyPercentage = cY * 100f / Settings.HEIGHT;

                EUIUtils.logInfo(this, "x  = {0}({1}%) , y  = {2}({3}%)", x, xPercentage, y, yPercentage);
                EUIUtils.logInfo(this, "cX = {0}({1}%) , cY = {2}({3}%)", cX, cxPercentage, cY, cyPercentage);
            }

            if (onDragFinish != null) {
                onDragFinish.invoke(this);
            }

            dragStart = null;
        }
    }

    public DraggableHitbox setBounds(float min_x, float max_x, float min_y, float max_y) {
        this.minX = min_x;
        this.maxX = max_x;
        this.minY = min_y;
        this.maxY = max_y;

        return this;
    }

    public DraggableHitbox setOnDragFinish(ActionT1<DraggableHitbox> onDragFinish) {
        this.onDragFinish = onDragFinish;
        return this;
    }
}
