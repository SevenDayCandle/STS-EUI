package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;

import java.util.ArrayList;

public interface CountingPanelCardFilter
{
    public abstract ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<AbstractCard> cards, Hitbox hb);
}
