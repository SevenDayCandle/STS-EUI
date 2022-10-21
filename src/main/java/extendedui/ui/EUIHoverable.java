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

    public boolean TryRender(SpriteBatch sb)
    {
        if (isActive)
        {
            this.hb.render(sb);
            Render(sb);
        }

        return isActive;
    }

    public void Update()
    {
        this.hb.update();
        if (this.hb.hovered && tooltip != null && tooltip.canRender) {
            EUITooltip.QueueTooltip(tooltip);
        }
    }

    public EUIHoverable SetHitbox(AdvancedHitbox hb) {
        this.hb = hb;
        return this;
    }

    public EUIHoverable SetDimensions(float width, float height)
    {
        this.hb.resize(width, height);

        return this;
    }

    // Center the hitbox on the specified coordinates
    public EUIHoverable SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    // The hitbox's center will move towards the designated position
    public EUIHoverable SetTargetPosition(float cX, float cY)
    {
        this.hb.SetTargetPosition(cX, cY);

        return this;
    }

    // Move the hitbox's bottom-left corner to the specified coordinates
    public EUIHoverable Translate(float x, float y)
    {
        this.hb.translate(x, y);

        return this;
    }

    public EUIHoverable SetTooltip(String title, String description)
    {
        return SetTooltip(new EUITooltip(title, description));
    }

    public EUIHoverable SetTooltip(EUITooltip tooltip)
    {
        this.tooltip = tooltip;

        return this;
    }

    public void set(float xPos, float yPos) {
        Translate(xPos, yPos);
    }

    public void setX(float xPos) {
        Translate(xPos, hb.y);
    }

    public void setY(float yPos) {
        Translate(hb.x, yPos);
    }

    public float getX() {
        return hb.x;
    }

    public float getY() {
        return hb.y;
    }
}
