package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public interface CustomRelicFilterModule extends IUIElement
{
    public abstract boolean isRelicValid(AbstractRelic c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractRelic> cards);
    public abstract void reset();
    public default void processGroup(ArrayList<AbstractRelic> group) {}
    public default boolean isRelicValid(EUIRelicGrid.RelicInfo c)
    {
        return isRelicValid(c.relic);
    }
}
