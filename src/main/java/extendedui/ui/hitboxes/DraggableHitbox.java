package extendedui.ui.hitboxes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.JavaUtils;

public class DraggableHitbox extends AdvancedHitbox
{
    protected Vector2 dragStart = null;
    protected ActionT1<DraggableHitbox> onDragFinish;

    public boolean canDrag;
    public float min_y;
    public float max_y;
    public float min_x;
    public float max_x;

    public DraggableHitbox(Hitbox hb)
    {
        this(hb.x, hb.y, hb.width, hb.height, true);
    }

    public DraggableHitbox(float width, float height)
    {
        this(-9999, -9999, width, height, true);
    }

    public DraggableHitbox(Hitbox hb, float width, float height)
    {
        this(hb.x, hb.y, width, height, true);
    }

    public DraggableHitbox(float x, float y, float width, float height)
    {
        this(x, y, width, height, true);
    }

    public DraggableHitbox(float x, float y, float width, float height, boolean canDrag)
    {
        super(x, y, width, height);

        this.canDrag = canDrag;
        this.min_x = -width * 0.25f;
        this.max_x = Settings.WIDTH + (width * 0.25f);
        this.min_y = -height * 0.25f;
        this.max_y = Settings.HEIGHT + (height * 0.25f);
    }

    public DraggableHitbox SetPosition(float cX, float cY)
    {
        move(cX, cY);

        return this;
    }

    public DraggableHitbox SetBounds(float min_x, float max_x, float min_y, float max_y)
    {
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;

        return this;
    }

    public DraggableHitbox SetOnDragFinish(ActionT1<DraggableHitbox> onDragFinish) {
        this.onDragFinish = onDragFinish;
        return this;
    }

    @Override
    public void update()
    {
        super.update();

        if (canDrag)
        {
            float mX = Gdx.input.getX();
            float mY = Settings.HEIGHT - Gdx.input.getY();

            if (hovered || dragStart != null)
            {
                if (InputHelper.justClickedLeft)
                {
                    if (EUI.TryDragging())
                    {
                        dragStart = new Vector2(mX, mY);
                        return;
                    }
                }
                else if (!InputHelper.justReleasedClickLeft && dragStart != null)
                {
                    target_cX = Math.min(max_x, Math.max(min_x, target_cX + (mX - dragStart.x)));
                    target_cY = Math.min(max_y, Math.max(min_y, target_cY + (mY - dragStart.y)));

                    if (EUI.TryDragging())
                    {
                        dragStart.set(mX, mY);
                        return;
                    }
                }
            }
        }

        if (dragStart != null)
        {
            if (Settings.isDebug)
            {
                float xPercentage  = x  * 100f / Settings.WIDTH;
                float yPercentage  = y  * 100f / Settings.HEIGHT;
                float cxPercentage = cX * 100f / Settings.WIDTH;
                float cyPercentage = cY * 100f / Settings.HEIGHT;

                JavaUtils.LogInfo(this, "x  = {0}({1}%) , y  = {2}({3}%)", x, xPercentage, y, yPercentage);
                JavaUtils.LogInfo(this, "cX = {0}({1}%) , cY = {2}({3}%)", cX, cxPercentage, cY, cyPercentage);
            }

            if (onDragFinish != null) {
                onDragFinish.Invoke(this);
            }

            dragStart = null;
        }
    }

    public boolean IsDragging()
    {
        return dragStart != null;
    }
}
