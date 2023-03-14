package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CustomPotionFilterModule extends EUIBase
{
    public abstract boolean isPotionValid(AbstractPotion c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractPotion> cards);
    public abstract void reset();
    public void processGroup(ArrayList<AbstractPotion> group) {}

    public boolean isPotionValid(EUIPotionGrid.PotionInfo c)
    {
        return isPotionValid(c.potion);
    }
}
