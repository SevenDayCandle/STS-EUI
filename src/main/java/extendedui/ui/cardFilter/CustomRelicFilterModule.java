package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.GUI_Base;
import extendedui.ui.controls.GUI_RelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CustomRelicFilterModule extends GUI_Base {
    public abstract boolean IsRelicValid(AbstractRelic c);
    public abstract boolean IsEmpty();
    public abstract boolean IsHovered();
    public abstract void InitializeSelection(Collection<AbstractRelic> cards);
    public abstract void Reset();
    public void ProcessGroup(ArrayList<AbstractRelic> group) {}

    public boolean IsRelicValid(GUI_RelicGrid.RelicInfo c)
    {
        return IsRelicValid(c.relic);
    }
}
