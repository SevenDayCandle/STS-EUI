package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public interface CustomPotionFilterModule extends IUIElement
{
    public abstract boolean isPotionValid(AbstractPotion c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractPotion> cards);
    public abstract void reset();
    public default void processGroup(ArrayList<AbstractPotion> group) {}
    public default boolean isPotionValid(EUIPotionGrid.PotionInfo c)
    {
        return isPotionValid(c.potion);
    }
}
