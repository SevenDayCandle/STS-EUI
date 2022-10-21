package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CustomRelicFilterModule extends EUIBase
{
    public abstract boolean IsRelicValid(AbstractRelic c);
    public abstract boolean IsEmpty();
    public abstract boolean IsHovered();
    public abstract void InitializeSelection(Collection<AbstractRelic> cards);
    public abstract void Reset();
    public void ProcessGroup(ArrayList<AbstractRelic> group) {}

    public boolean IsRelicValid(EUIRelicGrid.RelicInfo c)
    {
        return IsRelicValid(c.relic);
    }
}
