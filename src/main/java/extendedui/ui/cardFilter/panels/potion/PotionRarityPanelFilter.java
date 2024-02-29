package extendedui.ui.cardFilter.panels.potion;

import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CountingPanelFilter;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.cardFilter.CountingPanelCounter;
import extendedui.ui.cardFilter.CountingPanelStats;
import extendedui.utilities.PotionInfo;

import java.util.ArrayList;
import java.util.Collections;

public class PotionRarityPanelFilter implements CountingPanelFilter<PotionInfo>  {
    @Override
    public ArrayList<? extends CountingPanelCounter<?, PotionInfo>> generateCounters(ArrayList<? extends PotionInfo> cards, Hitbox hb, ActionT1<CountingPanelCounter<? extends CountingPanelItem<PotionInfo>, PotionInfo>> onClick) {
        return CountingPanelStats.basic(c ->
                        Collections.singleton(PotionRarityPanelFilterItem.get(c.potion.rarity)),
                cards).generateCounters(hb, onClick);
    }

    @Override
    public String getTitle() {
        return CardLibSortHeader.TEXT[0];
    }
}
