package extendedui.ui.cardFilter.panels;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelCardFilter;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;

import java.util.ArrayList;
import java.util.Collections;

public class CardRarityPanelFilter implements CountingPanelCardFilter {
    @Override
    public ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<? extends AbstractCard> cards, Hitbox hb) {
        return CountingPanelStats.basic(c ->
                        Collections.singleton(CardRarityPanelFilterItem.get(c.rarity)),
                cards).generateCounters(hb, panel -> cards.sort(panel.type));
    }

    @Override
    public String getTitle() {
        return CardLibSortHeader.TEXT[0];
    }
}
