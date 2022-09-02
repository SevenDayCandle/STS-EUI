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
import extendedui.utilities.CardAmountComparator;
import extendedui.utilities.FakeLibraryCard;

import java.util.ArrayList;

public class CustomCardLibSortHeader extends CardLibSortHeader
{
    public static CustomCardLibSortHeader Instance;
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
    public static ArrayList<AbstractCard> GetFakeGroup() {
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

        Instance = this;
        if (fakeLibraryCard == null) {
            fakeLibraryCard = new FakeLibraryCard();
        }
    }

    public void SetupButtons()
    {
        if (override == null)
        {
            final float START_X = 430f * Settings.scale;
            final float SPACE_X = (226f * Settings.scale);
            final float xPosition = START_X + (SPACE_X * buttons.length);

            override = new SortHeaderButton[buttons.length + 1];

            rarityButton = buttons[0];
            typeButton = buttons[1];
            costButton = buttons[2];

            if (Settings.removeAtoZSort)
            {
                nameButton = null;
            }
            else
            {
                nameButton = buttons[3];
            }

            amountButton = new SortHeaderButton(EUIRM.Strings.UI_Amount, xPosition, 0f, this);

            float offsetX = -(Settings.scale * 30f);

            SetupButton(rarityButton, offsetX, 0);
            SetupButton(typeButton, offsetX, 1);
            SetupButton(costButton, offsetX, 2);

            if (nameButton != null)
            {
                SetupButton(nameButton, offsetX, 3);
                SetupButton(amountButton, offsetX, 4);
            }
            else
            {
                SetupButton(amountButton, offsetX, 3);
            }
        }

        this.buttons = override;
    }

    private void SetupButton(SortHeaderButton button, float offsetX, int index)
    {
        override[index] = button;

        Hitbox hitbox = button.hb;
        hitbox.resize(hitbox.width + offsetX, hitbox.height);
        hitbox.move(hitbox.cX + (offsetX * index), hitbox.cY);
    }

    @Override
    public void setGroup(CardGroup group)
    {
        EUI.CardFilters.Clear(false, true);
        if (this.group != null && this.originalGroup != null) {
            this.group.group = this.originalGroup;
        }
        this.originalGroup = new ArrayList<>(group.group);
        if (group.group.size() > 0) {
            fakeLibraryCard.current_x = group.group.get(0).current_x;
            fakeLibraryCard.current_y = group.group.get(0).current_y;
        }

        if (CardKeywordFilters.CustomModule != null) {
            CardKeywordFilters.CustomModule.ProcessGroup(group);
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

    public ArrayList<AbstractCard> GetVisibleCards() {
        return this.group != null ? this.group.group : new ArrayList<>();
    }

    public void UpdateForFilters() {
        if (this.group != null) {
            if (EUI.CardFilters.AreFiltersEmpty()) {
                this.group.group = originalGroup;
            }
            else {
                ArrayList<AbstractCard> tempGroup = EUI.CardFilters.ApplyFilters(originalGroup);
                if (tempGroup.size() > 0) {
                    this.group.group = tempGroup;
                }
                else {
                    this.group.group = GetFakeGroup();
                }
            }
            didChangeOrder(lastUsedButton, isAscending);
            EUI.CardFilters.Refresh(this.group.group);
        }
    }

    public float GetCenterY() {
        if (buttons.length > 0) {
            return buttons[0].hb.cY;
        }
        return Settings.HEIGHT * 0.75f;
    }
}
