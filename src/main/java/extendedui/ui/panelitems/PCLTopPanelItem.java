package extendedui.ui.panelitems;

import basemod.TopPanelItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.GenericCallback;

public abstract class PCLTopPanelItem extends TopPanelItem
{
    public GenericCallback<PCLTopPanelItem> onLeftClick;
    public EUITooltip tooltip;


    public static String CreateFullID(Class<? extends PCLTopPanelItem> type)
    {
        return EUIRM.GetID(type.getSimpleName());
    }

    public PCLTopPanelItem(TextureCache tc, String id) {
        super(tc.Texture(), id);
    }

    public PCLTopPanelItem SetOnClick(GenericCallback<PCLTopPanelItem> onLeftClick) {
        this.onLeftClick = onLeftClick;
        setClickable(this.onLeftClick != null);
        return this;
    }

    public PCLTopPanelItem SetTooltip(EUITooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    protected void onClick() {
        if (this.onLeftClick != null) {
            this.onLeftClick.Complete(this);
        }
    }

    @Override
    public void update() {
        // Do not allow top panel item to be clicked on while a FTUE is active, just like the other top panel items
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.FTUE) {
            super.update();
            if (this.tooltip != null && getHitbox().hovered) {
                EUITooltip.QueueTooltip(tooltip);
            }
        }
    }
}
