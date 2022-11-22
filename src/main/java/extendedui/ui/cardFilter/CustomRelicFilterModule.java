package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CustomRelicFilterModule extends EUIBase
{
    public abstract boolean isRelicValid(AbstractRelic c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractRelic> cards);
    public abstract void reset();
    public void processGroup(ArrayList<AbstractRelic> group) {}

    public boolean isRelicValid(EUIRelicGrid.RelicInfo c)
    {
        return isRelicValid(c.relic);
    }
}
