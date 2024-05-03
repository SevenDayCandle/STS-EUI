package extendedui.utilities.panels.relic;

import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CountingPanelFilter;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;
import extendedui.utilities.RelicInfo;

import java.util.ArrayList;
import java.util.Collections;

public class RelicRarityPanelFilter implements CountingPanelFilter<RelicInfo>  {
    @Override
    public ArrayList<? extends CountingPanelCounter<?, RelicInfo>> generateCounters(ArrayList<? extends RelicInfo> cards, Hitbox hb, ActionT1<CountingPanelCounter<? extends CountingPanelItem<RelicInfo>, RelicInfo>> onClick) {
        return CountingPanelStats.basic(c ->
                        Collections.singleton(RelicRarityPanelFilterItem.get(c.relic.tier)),
                cards).generateCounters(hb, onClick);
    }

    @Override
    public String getTitle() {
        return CardLibSortHeader.TEXT[0];
    }
}
