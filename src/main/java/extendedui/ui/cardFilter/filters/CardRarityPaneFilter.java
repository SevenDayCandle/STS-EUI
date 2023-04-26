package extendedui.ui.cardFilter.filters;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.markers.CountingPanelCardFilter;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;

import java.util.ArrayList;
import java.util.Collections;

public class CardRarityPaneFilter implements CountingPanelCardFilter {
    @Override
    public ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<AbstractCard> cards, Hitbox hb) {
        return CountingPanelStats.basic(c ->
                        Collections.singleton(CardRarityPanelFilterItem.get(c.rarity)),
                cards).generateCounters(hb, panel -> cards.sort(panel.type));
    }
}
