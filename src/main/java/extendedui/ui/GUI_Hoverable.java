package extendedui.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;

public abstract class GUI_Hoverable extends GUI_Base
{
    public AdvancedHitbox hb;
    public EUITooltip tooltip;

    public GUI_Hoverable(AdvancedHitbox hb) {
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

    public GUI_Hoverable SetHitbox(AdvancedHitbox hb) {
        this.hb = hb;
        return this;
    }

    public GUI_Hoverable SetDimensions(float width, float height)
    {
        this.hb.resize(width, height);

        return this;
    }

    public GUI_Hoverable SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public GUI_Hoverable SetTooltip(String title, String description)
    {
        return SetTooltip(new EUITooltip(title, description));
    }

    public GUI_Hoverable SetTooltip(EUITooltip tooltip)
    {
        this.tooltip = tooltip;

        return this;
    }
}
