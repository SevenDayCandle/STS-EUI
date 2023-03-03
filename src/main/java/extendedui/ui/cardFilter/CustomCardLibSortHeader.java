package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.utilities.CardAmountComparator;
import extendedui.utilities.FakeLibraryCard;

import java.util.ArrayList;

public class CustomCardLibSortHeader extends CardLibSortHeader
{
    public static final float SPACE_X = 166f * Settings.scale;
    public static final float WIDTH_DEC = 30 * Settings.scale;
    public static CustomCardLibSortHeader instance;
    private static CardGroup falseGroup;
    private static FakeLibraryCard fakeLibraryCard;

    public ArrayList<AbstractCard> originalGroup;
    private SortHeaderButton[] override = null;
    private SortHeaderButton amountButton = null;
    private SortHeaderButton rarityButton;
    private SortHeaderButton typeButton;
    private SortHeaderButton nameButton;
    private SortHeaderButton costButton;
    private SortHeaderButton lastUsedButton;
    private boolean isAscending;

    // The fake group tells players that nothing can be found. It also prevents crashing from empty cardGroups without the need for patching
    public static ArrayList<AbstractCard> getFakeGroup() {
        if (fakeLibraryCard == null) {
            fakeLibraryCard = new FakeLibraryCard();
        }
        if (falseGroup == null) {
            falseGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            falseGroup.addToBottom(fakeLibraryCard);
        }
        return falseGroup.group;
    }

    public CustomCardLibSortHeader(CardGroup group)
    {
        super(group);

        instance = this;
        if (fakeLibraryCard == null) {
            fakeLibraryCard = new FakeLibraryCard();
        }
    }

    // Ignore custom sorting for now because the sort header can't recognize them without reflection
    public void setupButtons()
    {
        if (override == null)
        {
            final float START = buttons[0].hb.cX;
            rarityButton = buttons[0];
            typeButton = buttons[1];
            costButton = buttons[2];
            amountButton = new SortHeaderButton(EUIRM.strings.uiAmount, 0f, 0f, this);

            if (Settings.removeAtoZSort)
            {
                nameButton = null;
                override = EUIUtils.array(rarityButton, typeButton, costButton, amountButton);
            }
            else
            {
                nameButton = buttons[3];
                override = EUIUtils.array(rarityButton, typeButton, costButton, nameButton, amountButton);
            }


            for (int i = 0; i < override.length; i++)
            {
                setupButton(override[i], START, i);
            }
        }

        this.buttons = override;
    }

    private void setupButton(SortHeaderButton button, float start, int index)
    {
        override[index] = button;
        Hitbox hitbox = button.hb;
        hitbox.resize(hitbox.width - WIDTH_DEC, hitbox.height);
        hitbox.move(start + (CustomCardLibSortHeader.SPACE_X * index), hitbox.cY);
    }

    @Override
    public void setGroup(CardGroup group)
    {
        EUI.cardFilters.clear(false, true);
        if (this.group != null && this.originalGroup != null) {
            this.group.group = this.originalGroup;
        }
        this.originalGroup = new ArrayList<>(group.group);
        if (group.group.size() > 0) {
            fakeLibraryCard.current_x = group.group.get(0).current_x;
            fakeLibraryCard.current_y = group.group.get(0).current_y;
        }

        if (CardKeywordFilters.customModule != null) {
            CardKeywordFilters.customModule.processGroup(group);
        }

        super.setGroup(group);
    }

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending)
    {
        this.lastUsedButton = button;
        this.isAscending = isAscending;
        if (!this.group.group.isEmpty()) {
            if (button == this.rarityButton)
            {
                this.group.sortByRarity(isAscending);
            }
            else if (button == this.typeButton)
            {
                this.group.sortByType(isAscending);
            }
            else if (button == this.costButton)
            {
                this.group.sortByCost(isAscending);
            }
            else if (button == this.nameButton)
            {
                this.group.sortAlphabetically(isAscending);
            }
            else if (button == this.amountButton)
            {
                if (!isAscending)
                {
                    this.group.group.sort(new CardAmountComparator(false));
                }
                else
                {
                    this.group.group.sort(new CardAmountComparator(true));
                }
            }
            else
            {
                return;
            }

            this.group.sortByStatus(false);
        }

        this.justSorted = true;

        if (button != null) {
            button.setActive(true);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

    public ArrayList<AbstractCard> getVisibleCards() {
        return this.group != null ? this.group.group : new ArrayList<>();
    }

    public boolean isHovered()
    {
        return buttons != null && EUIUtils.any(buttons, button -> button.hb.hovered);
    }

    public void updateForFilters() {
        if (this.group != null) {
            if (EUI.cardFilters.areFiltersEmpty()) {
                this.group.group = originalGroup;
            }
            else {
                ArrayList<AbstractCard> tempGroup = EUI.cardFilters.applyFilters(originalGroup);
                if (tempGroup.size() > 0) {
                    this.group.group = tempGroup;
                }
                else {
                    this.group.group = getFakeGroup();
                }
            }
            didChangeOrder(lastUsedButton, isAscending);
            EUI.cardFilters.refresh(this.group.group);
        }
    }

    public float getCenterY() {
        if (buttons.length > 0) {
            return buttons[0].hb.cY;
        }
        return Settings.HEIGHT * 0.75f;
    }
}
