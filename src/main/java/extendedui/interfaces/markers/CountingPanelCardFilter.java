package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.ui.cardFilter.CountingPanelCounter;

import java.util.ArrayList;

public interface CountingPanelCardFilter {
    ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<? extends AbstractCard> cards, Hitbox hb);
    String getTitle();
}
