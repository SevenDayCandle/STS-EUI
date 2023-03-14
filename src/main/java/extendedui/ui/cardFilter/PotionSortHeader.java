package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIPotionGrid;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class PotionSortHeader extends EUIBase implements SortHeaderButtonListener
{
    public static PotionSortHeader instance;
    public static final float START_X = screenW(0.5f) - CardLibSortHeader.SPACE_X * 1.45f;

    private SortHeaderButton lastUsedButton;
    protected boolean isAscending;
    protected boolean snapToGroup;
    protected float baseY = Settings.HEIGHT * 0.85f;
    protected SortHeaderButton rarityButton;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton colorButton;
    protected SortHeaderButton amountButton;
    public SortHeaderButton[] buttons;
    public EUIPotionGrid grid;
    public ArrayList<EUIPotionGrid.PotionInfo> originalGroup;

    public PotionSortHeader(EUIPotionGrid grid)
    {
        this.grid = grid;
        instance = this;
        float xPosition = START_X;
        this.rarityButton = new SortHeaderButton(CardLibSortHeader.TEXT[0], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.colorButton = new SortHeaderButton(EUIRM.strings.uiColors, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.amountButton = new SortHeaderButton(EUIRM.strings.uiAmount, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.rarityButton, this.nameButton, this.colorButton, this.amountButton};
    }

    public PotionSortHeader setBaseY(float value)
    {
        this.baseY = value;
        return this;
    }

    public PotionSortHeader snapToGroup(boolean value)
    {
        this.snapToGroup = value;
        return this;
    }

    public PotionSortHeader setGrid(EUIPotionGrid grid) {
        EUI.potionFilters.clear(false, true);
        this.grid = grid;
        this.originalGroup = new ArrayList<>(grid.potionGroup);

        if (PotionKeywordFilters.customModule != null) {
            PotionKeywordFilters.customModule.processGroup(EUIUtils.map(grid.potionGroup, r -> r.potion));
        }
        for (SortHeaderButton button : buttons)
        {
            button.reset();
        }

        return this;
    }

    @Override
    public void updateImpl()
    {
        float scrolledY = snapToGroup && this.grid != null && this.grid.potionGroup.size() > 0 ? this.grid.potionGroup.get(0).potion.posY + 230.0F * Settings.yScale : baseY;
        for (SortHeaderButton button : buttons)
        {
            button.update();
            button.updateScrollPosition(scrolledY);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        for (SortHeaderButton button : buttons)
        {
            button.render(sb);
        }
    }

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending)
    {
        if (grid != null)
        {
            if (button == this.rarityButton)
            {
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.rarity.ordinal() - b.potion.rarity.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.nameButton)
            {
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.potion.name, b.potion.name)) * (isAscending ? 1 : -1));
            }
            else if (button == this.colorButton)
            {
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potionColor.ordinal() - b.potionColor.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.amountButton)
            {
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.getPotency() - b.potion.getPotency()) * (isAscending ? 1 : -1));
            }
            else
            {
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potionColor.ordinal() - b.potionColor.ordinal()) * (isAscending ? 1 : -1));
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.rarity.ordinal() - b.potion.rarity.ordinal()) * (isAscending ? 1 : -1));
                this.grid.potionGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.potion.getPotency() - b.potion.getPotency()) * (isAscending ? 1 : -1));
            }
        }
        for (SortHeaderButton eB : buttons)
        {
            eB.setActive(eB == button);
        }
    }

    public void updateForFilters() {
        if (this.grid != null) {
            if (EUI.potionFilters.areFiltersEmpty()) {
                this.grid.potionGroup = originalGroup;
            }
            else {
                this.grid.potionGroup = EUI.potionFilters.applyInfoFilters(originalGroup);
            }
            didChangeOrder(lastUsedButton, isAscending);
            EUI.potionFilters.refresh(EUIUtils.map(grid.potionGroup, group -> group.potion));
        }
    }

    public ArrayList<AbstractPotion> getPotions()
    {
        return EUIUtils.map(grid.potionGroup, r -> r.potion);
    }

    public ArrayList<AbstractPotion> getOriginalPotions()
    {
        return EUIUtils.map(originalGroup, r -> r.potion);
    }
}
