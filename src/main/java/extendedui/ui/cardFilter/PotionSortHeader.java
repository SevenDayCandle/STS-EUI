package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.utilities.ItemGroup;
import extendedui.utilities.PotionInfo;
import org.apache.commons.lang3.StringUtils;

public class PotionSortHeader extends GenericSortHeader<PotionInfo> {
    public static PotionSortHeader instance;
    protected SortHeaderButton rarityButton;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton colorButton;
    protected SortHeaderButton amountButton;

    public PotionSortHeader(ItemGroup<PotionInfo> group) {
        super(group);
        instance = this;
        float xPosition = START_X;
        this.rarityButton = new SortHeaderButton(CardLibSortHeader.TEXT[0], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.colorButton = new SortHeaderButton(EUIRM.strings.ui_colors, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.amountButton = new SortHeaderButton(EUIRM.strings.ui_amount, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.rarityButton, this.nameButton, this.colorButton, this.amountButton};
    }

    @Override
    public GenericFilters<PotionInfo, ?, ?> getFilters() {
        return EUI.potionFilters;
    }

    @Override
    protected float getFirstY() {
        return this.group.group.get(0).potion.posY;
    }

    @Override
    protected void sort(SortHeaderButton button, boolean isAscending) {
        if (button == this.rarityButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.rarity.ordinal() - b.potion.rarity.ordinal()) * (isAscending ? 1 : -1));
        }
        else if (button == this.nameButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.potion.name, b.potion.name)) * (isAscending ? 1 : -1));
        }
        else if (button == this.colorButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potionColor.ordinal() - b.potionColor.ordinal()) * (isAscending ? 1 : -1));
        }
        else if (button == this.amountButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.getPotency() - b.potion.getPotency()) * (isAscending ? 1 : -1));
        }
        else {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potionColor.ordinal() - b.potionColor.ordinal()) * (isAscending ? 1 : -1));
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.rarity.ordinal() - b.potion.rarity.ordinal()) * (isAscending ? 1 : -1));
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.getPotency() - b.potion.getPotency()) * (isAscending ? 1 : -1));
        }
    }
}
