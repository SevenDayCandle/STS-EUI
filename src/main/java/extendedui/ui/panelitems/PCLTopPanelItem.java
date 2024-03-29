package extendedui.ui.panelitems;

import basemod.TopPanelItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUITooltip;

public abstract class PCLTopPanelItem extends TopPanelItem {
    private boolean rightClickable = true;
    protected ActionT1<PCLTopPanelItem> onLeftClick;
    protected ActionT1<PCLTopPanelItem> onRightClick;
    public EUITooltip tooltip;


    public PCLTopPanelItem(TextureCache tc, String id) {
        super(tc.texture(), id);
    }

    public static String createFullID(Class<? extends PCLTopPanelItem> type) {
        return EUIRM.getID(type.getSimpleName());
    }

    @Override
    protected void onClick() {
        if (this.onLeftClick != null) {
            this.onLeftClick.invoke(this);
        }
    }

    protected void onRightClick() {
        if (this.onRightClick != null) {
            this.onRightClick.invoke(this);
        }
    }

    public PCLTopPanelItem setOnClick(ActionT1<PCLTopPanelItem> onLeftClick) {
        this.onLeftClick = onLeftClick;
        setClickable(this.onLeftClick != null);
        return this;
    }

    public PCLTopPanelItem setOnRightClick(ActionT1<PCLTopPanelItem> onRightClick) {
        this.onRightClick = onRightClick;
        setRightClickable(this.onRightClick != null);
        return this;
    }

    public void setRightClickable(boolean rightClickable) {
        this.rightClickable = rightClickable;
    }

    public PCLTopPanelItem setTooltip(EUITooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void update() {
        // Do not allow top panel item to be clicked on while a FTUE is active, just like the other top panel items
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.FTUE) {
            super.update();
            if (this.tooltip != null && getHitbox().hovered) {
                EUITooltip.queueTooltip(tooltip);
            }
            if (this.hitbox.hovered && InputHelper.justClickedRight && this.rightClickable) {
                this.onRightClick();
            }
        }
    }
}
