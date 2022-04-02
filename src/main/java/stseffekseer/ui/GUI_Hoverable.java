package stseffekseer.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import stseffekseer.ui.hitboxes.AdvancedHitbox;
import stseffekseer.ui.tooltips.EUITooltip;

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

    public boolean TryUpdate()
    {
        if (isActive)
        {
            this.hb.update();
            Update();
            if (this.hb.hovered && tooltip != null && tooltip.canRender) {
                EUITooltip.QueueTooltip(tooltip);
            }
        }

        return isActive;
    }

    public GUI_Hoverable SetHitbox(AdvancedHitbox hb) {
        this.hb = hb;
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
