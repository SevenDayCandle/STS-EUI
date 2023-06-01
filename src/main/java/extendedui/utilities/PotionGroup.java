package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class PotionGroup implements Iterable<PotionGroup.PotionInfo> {
    public ArrayList<PotionInfo> group;

    public PotionGroup() {
        group = new ArrayList<>();
    }

    public PotionGroup(Collection<PotionInfo> infos) {
        group = new ArrayList<>(infos);
    }

    public void add(AbstractPotion relic) {
        group.add(new PotionInfo(relic));
    }

    public void add(PotionInfo relicInfo) {
        group.add(relicInfo);
    }

    public void addAll(Collection<PotionInfo> relicInfo) {
        group.addAll(relicInfo);
    }

    public void clear() {
        group.clear();
    }

    @Override
    public Iterator<PotionInfo> iterator() {
        return group.iterator();
    }

    public int size() {
        return group.size();
    }

    public void sort(Comparator<? super PotionInfo> comparator) {
        group.sort(comparator);
    }

    public static class PotionInfo {
        public final AbstractPotion potion;
        public final AbstractCard.CardColor potionColor;

        public PotionInfo(AbstractPotion potion) {
            this.potion = potion;
            this.potionColor = EUIGameUtils.getPotionColor(potion.ID);
        }
    }
}
