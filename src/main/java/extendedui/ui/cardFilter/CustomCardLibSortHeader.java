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
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.FakeLibraryCard;

import java.util.ArrayList;

public class CustomCardLibSortHeader extends CardLibSortHeader {
    public static final float SPACE_X = 166f * Settings.scale;
    public static final float WIDTH_DEC = 30 * Settings.scale;
    public static final float CENTER_Y = Settings.HEIGHT * 0.88f;
    private static CardGroup falseGroup;
    private static FakeLibraryCard fakeLibraryCard;
    public static CustomCardLibSortHeader instance;
    private SortHeaderButton[] override = null;
    private SortHeaderButton amountButton = null;
    private SortHeaderButton rarityButton;
    private SortHeaderButton typeButton;
    private SortHeaderButton nameButton;
    private SortHeaderButton costButton;
    private SortHeaderButton lastUsedButton;
    private boolean isAscending;
    private boolean isFixedPosition;
    public ArrayList<AbstractCard> originalGroup;

    public CustomCardLibSortHeader(CardGroup group) {
        super(group);

        instance = this;
        if (fakeLibraryCard == null) {
            fakeLibraryCard = new FakeLibraryCard();
        }
    }

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

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending) {
        this.lastUsedButton = button;
        this.isAscending = isAscending;
        if (!this.group.group.isEmpty()) {
            if (button == this.rarityButton) {
                this.group.sortByRarity(isAscending);
            }
            else if (button == this.typeButton) {
                this.group.sortByType(isAscending);
            }
            else if (button == this.costButton) {
                this.group.sortByCost(isAscending);
            }
            else if (button == this.nameButton) {
                this.group.sortAlphabetically(isAscending);
            }
            else if (button == this.amountButton) {
                this.group.group.sort(new CardAmountComparator(isAscending));
            }
            else {
                // Packmaster compatibility
                // TODO general series button
                SortHeaderButton packButton = getPackmasterButton();
                if (packButton != null) {
                    sortWithPackmaster(isAscending);
                    this.group.sortByStatus(false);
                }
                return;
            }

            this.group.sortByStatus(false);
        }

        this.justSorted = true;

        if (button != null) {
            button.setActive(true);
        }
    }

    protected SortHeaderButton getPackmasterButton() {
        try {
            return EUIClassUtils.getRFieldStatic("thePackmaster.patches.CompendiumPatches", "packButton");
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public ArrayList<AbstractCard> getVisibleCards() {
        return this.group != null ? this.group.group : new ArrayList<>();
    }

    public boolean isHovered() {
        return buttons != null && EUIUtils.any(buttons, button -> button.hb.hovered);
    }

    @Override
    public void render(SpriteBatch sb) {
        // The bar position should remain static when viewing the custom library screen or the card pool
        if (isFixedPosition) {
            this.renderButtons(sb);
            this.renderSelection(sb);
        }
        else {
            super.render(sb);
        }
    }

    public void resetSort() {
        this.justSorted = true;
        group.sortAlphabetically(true);
        group.sortByRarity(true);
        group.sortByStatus(true);

        for (SortHeaderButton button : buttons) {
            button.reset();
        }
    }

    @Override
    public void setGroup(CardGroup group) {
        EUI.cardFilters.clear(false, true);
        if (this.group != null && this.originalGroup != null) {
            this.group.group = this.originalGroup;
        }
        this.originalGroup = new ArrayList<>(group.group);
        if (group.group.size() > 0) {
            fakeLibraryCard.current_x = group.group.get(0).current_x;
            fakeLibraryCard.current_y = group.group.get(0).current_y;
        }

        if (EUI.cardFilters.customModule != null) {
            EUI.cardFilters.customModule.processGroup(group);
        }

        this.group = group;
        resetSort();
    }

    private void setupButton(SortHeaderButton button, float start, int index) {
        button.hb.resize(button.hb.width - WIDTH_DEC, button.hb.height);
        button.hb.move(start + (CustomCardLibSortHeader.SPACE_X * index), isFixedPosition ? CENTER_Y : button.hb.cY);
    }

    public void setupButtons(boolean isFixedPosition) {
        this.isFixedPosition = isFixedPosition;

        if (override == null) {
            rarityButton = buttons[0];
            typeButton = buttons[1];
            costButton = buttons[2];
            nameButton = Settings.removeAtoZSort ? null : buttons[3];
            amountButton = new SortHeaderButton(EUIRM.strings.ui_amount, 0f, 0f, this);

            override = EUIUtils.arrayAppend(buttons, amountButton);
        }

        final float start = buttons[0].hb.cX;
        for (int i = 0; i < override.length; i++) {
            setupButton(override[i], start, i);
        }

        this.buttons = override;
    }

    protected void sortWithPackmaster(boolean isAscending) {
        try {
            EUIClassUtils.invokeRStaticForTypes("thePackmaster.patches.CompendiumPatches", "packSort", EUIUtils.array(CardLibSortHeader.class, boolean.class), this, isAscending);
        }
        catch (Exception ignored) {
        }
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
}
