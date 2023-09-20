package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.utilities.ItemGroup;
import org.apache.commons.lang3.StringUtils;

public class BlightSortHeader extends GenericSortHeader<AbstractBlight> {
    public static BlightSortHeader instance;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton uniqueButton;

    public BlightSortHeader(ItemGroup<AbstractBlight> group) {
        super(group);
        instance = this;
        float xPosition = START_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.uniqueButton = new SortHeaderButton(EUIRM.strings.ui_unique, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.nameButton, this.uniqueButton};
    }

    @Override
    public GenericFilters<AbstractBlight, ?, ?> getFilters() {
        return EUI.blightFilters;
    }

    @Override
    protected float getFirstY() {
        return group.group.get(0).hb.y;
    }

    @Override
    protected void sort(SortHeaderButton button, boolean isAscending) {
        if (button == this.nameButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.name, b.name)) * (isAscending ? 1 : -1));
        }
        else if (button == this.uniqueButton) {
            this.group.sort((a, b) -> sortByUnique(a, b) * (isAscending ? 1 : -1));
        }
        else {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.blightID, b.blightID)) * (isAscending ? 1 : -1));
            this.group.sort((a, b) -> sortByUnique(a, b) * (isAscending ? 1 : -1));
        }
    }

    protected int sortByUnique(AbstractBlight a, AbstractBlight b) {
        int aValue = a == null || a.unique ? 1 : 0;
        int bValue = b == null || b.unique ? 1 : 0;
        return aValue - bValue;
    }
}
