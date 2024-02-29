package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.cardFilter.CountingPanelCounter;

import java.util.ArrayList;

// T is a game object to be counted (e.g. AbstractCard)
public interface CountingPanelFilter<T> {
    ArrayList<? extends CountingPanelCounter<?, T>> generateCounters(ArrayList<? extends T> cards, Hitbox hb, ActionT1<CountingPanelCounter<? extends CountingPanelItem<T>, T>> onClick);

    String getTitle();
}
