package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public class FilterSortHeader extends EUIBase {
    protected final ArrayList<FilterSortToggle> buttons;
    private boolean snapToGroup;
    protected GenericFilters<?, ?, ?> filters;
    protected float baseY = Settings.HEIGHT * 0.86f;

    public FilterSortHeader() {
        buttons = new ArrayList<>();
    }

    public void didChangeOrder(FilterSortToggle button) {
        for (FilterSortToggle eB : buttons) {
            if (eB == button) {
                eB.select(filters.isReverseOrder);
            }
            else {
                eB.select(null);
            }
        }
    }

    public float getLastX() {
        if (buttons.size() > 0) {
            FilterSortToggle last = buttons.get(buttons.size() - 1);
            return last.hb.x + last.hb.width;
        }
        return 0;
    }

    public boolean isHovered() {
        return EUIUtils.any(buttons, b -> b.hb.hovered);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (FilterSortToggle button : buttons) {
            button.render(sb);
        }
    }

    public FilterSortHeader setBaseY(float value) {
        this.baseY = value;
        return this;
    }

    public FilterSortHeader setFilters(GenericFilters<?, ?, ?> filters, float startX) {
        this.filters = filters;
        this.buttons.clear();
        filters.setupSortHeader(this, startX);
        return this;
    }

    public FilterSortHeader snapToGroup(boolean value) {
        this.snapToGroup = value;
        return this;
    }

    @Override
    public void updateImpl() {
        if (snapToGroup) {
            float scrolledY = filters.group != null && filters.group.size() > 0 ? filters.getFirstY() + 230.0F * Settings.yScale : baseY;
            for (FilterSortToggle button : buttons) {
                button.updateImpl();
                button.hb.moveY(scrolledY);
            }
        }
        else {
            for (FilterSortToggle button : buttons) {
                button.updateImpl();
            }
        }
    }
}
