package extendedui.ui.panelitems;

import basemod.TopPanelItem;
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
        super.update();
        if (this.tooltip != null && getHitbox().hovered) {
            EUITooltip.QueueTooltip(tooltip);
        }
    }
}
