package extendedui.ui.cardFilter.filters;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.markers.CountingPanelCardFilter;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;

import java.util.ArrayList;
import java.util.Collections;

public class CardTypePaneFilter implements CountingPanelCardFilter {
    @Override
    public ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<? extends AbstractCard> cards, Hitbox hb) {
        return CountingPanelStats.basic(c ->
                        Collections.singleton(CardTypePanelFilterItem.get(c.type)),
                cards).generateCounters(hb, panel -> cards.sort(panel.type));
    }
}
