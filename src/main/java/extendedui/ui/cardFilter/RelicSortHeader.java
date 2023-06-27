package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.utilities.ItemGroup;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class RelicSortHeader extends GenericSortHeader<RelicInfo> {
    public static RelicSortHeader instance;
    protected SortHeaderButton rarityButton;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton colorButton;
    protected SortHeaderButton seenButton;

    public RelicSortHeader(ItemGroup<RelicInfo> group) {
        super(group);
        instance = this;
        float xPosition = START_X;
        this.rarityButton = new SortHeaderButton(CardLibSortHeader.TEXT[0], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.colorButton = new SortHeaderButton(EUIRM.strings.ui_colors, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.seenButton = new SortHeaderButton(EUIRM.strings.ui_seen, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.rarityButton, this.nameButton, this.colorButton, this.seenButton};
    }

    @Override
    protected float getFirstY() {
        return group.group.get(0).relic.hb.y;
    }

    @Override
    protected void sort(SortHeaderButton button, boolean isAscending) {
        if (button == this.rarityButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
        }
        else if (button == this.nameButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.relic.name, b.relic.name)) * (isAscending ? 1 : -1));
        }
        else if (button == this.colorButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
        }
        else if (button == this.seenButton) {
            this.group.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
        }
        else {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
            this.group.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
        }
    }

    @Override
    public GenericFilters<RelicInfo, ?> getFilters() {
        return EUI.relicFilters;
    }

    protected int sortBySeen(RelicInfo a, RelicInfo b) {
        int aValue = a == null || a.locked ? 2 : a.relic.isSeen ? 1 : 0;
        int bValue = b == null || b.locked ? 2 : b.relic.isSeen ? 1 : 0;
        return aValue - bValue;
    }
}
