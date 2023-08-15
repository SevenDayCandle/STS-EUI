package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import extendedui.ui.EUIBase;
import extendedui.utilities.ItemGroup;

import java.util.ArrayList;

public abstract class GenericSortHeader<T> extends EUIBase implements SortHeaderButtonListener {
    public static final float START_X = screenW(0.5f) - CardLibSortHeader.SPACE_X * 1.45f;
    private SortHeaderButton lastUsedButton;
    protected boolean isAscending;
    protected boolean snapToGroup;
    protected float baseY = Settings.HEIGHT * 0.85f;
    public SortHeaderButton[] buttons;
    public ItemGroup<T> group;
    public ArrayList<T> originalGroup;

    public GenericSortHeader(ItemGroup<T> group) {
        this.group = group;
    }

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending) {
        if (group != null) {
            sort(button, isAscending);
        }
        for (SortHeaderButton eB : buttons) {
            eB.setActive(eB == button);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (SortHeaderButton button : buttons) {
            button.render(sb);
        }
    }

    public GenericSortHeader<T> setBaseY(float value) {
        this.baseY = value;
        return this;
    }

    public GenericSortHeader<T> setGroup(ItemGroup<T> group) {
        getFilters().clear(false, true);
        this.group = group;
        this.originalGroup = new ArrayList<>(group.group);

        if (getFilters().customModule != null) {
            getFilters().customModule.processGroup(group);
        }
        for (SortHeaderButton button : buttons) {
            button.reset();
        }

        return this;
    }

    public GenericSortHeader<T> snapToGroup(boolean value) {
        this.snapToGroup = value;
        return this;
    }

    public void updateForFilters() {
        if (this.group != null) {
            if (getFilters().areFiltersEmpty()) {
                this.group.group = originalGroup;
            }
            else {
                this.group.group = getFilters().applyFilters(originalGroup);
            }
            didChangeOrder(lastUsedButton, isAscending);
            getFilters().refresh(group.group);
        }
    }

    @Override
    public void updateImpl() {
        float scrolledY = snapToGroup && this.group != null && this.group.size() > 0 ? getFirstY() + 230.0F * Settings.yScale : baseY;
        for (SortHeaderButton button : buttons) {
            button.update();
            button.updateScrollPosition(scrolledY);
        }
    }

    abstract protected float getFirstY();

    abstract protected void sort(SortHeaderButton button, boolean isAscending);

    abstract public GenericFilters<T, ?> getFilters();
}
