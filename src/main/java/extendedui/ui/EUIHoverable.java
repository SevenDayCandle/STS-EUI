package extendedui.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;

public abstract class EUIHoverable extends EUIBase
{
    public AdvancedHitbox hb;
    public EUITooltip tooltip;

    public EUIHoverable(AdvancedHitbox hb) {
        this.hb = hb;
    }

    public boolean tryRender(SpriteBatch sb)
    {
        if (isActive)
        {
            this.hb.render(sb);
            renderImpl(sb);
        }

        return isActive;
    }

    public void updateImpl()
    {
        this.hb.update();
        if (this.hb.hovered && tooltip != null && tooltip.canRender) {
            EUITooltip.queueTooltip(tooltip);
        }
    }

    public EUIHoverable setHitbox(AdvancedHitbox hb) {
        this.hb = hb;
        return this;
    }

    public EUIHoverable setDimensions(float width, float height)
    {
        this.hb.resize(width, height);

        return this;
    }

    // Center the hitbox on the specified coordinates
    public EUIHoverable setPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    // The hitbox's center will move towards the designated position
    public EUIHoverable setTargetPosition(float cX, float cY)
    {
        this.hb.setTargetPosition(cX, cY);

        return this;
    }

    // Move the hitbox's bottom-left corner to the specified coordinates
    public EUIHoverable translate(float x, float y)
    {
        this.hb.translate(x, y);

        return this;
    }

    public EUIHoverable setTooltip(String title, String description)
    {
        return setTooltip(new EUITooltip(title, description));
    }

    public EUIHoverable setTooltip(EUITooltip tooltip)
    {
        this.tooltip = tooltip;

        return this;
    }

    public void set(float xPos, float yPos) {
        translate(xPos, yPos);
    }

    public void setX(float xPos) {
        translate(xPos, hb.y);
    }

    public void setY(float yPos) {
        translate(hb.x, yPos);
    }

    public float getX() {
        return hb.x;
    }

    public float getY() {
        return hb.y;
    }
}
