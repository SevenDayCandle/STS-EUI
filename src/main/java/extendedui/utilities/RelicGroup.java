package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;

import java.util.*;

public class RelicGroup implements Iterable<RelicGroup.RelicInfo> {
    public ArrayList<RelicInfo> group;

    public RelicGroup() {
        group = new ArrayList<>();
    }

    public RelicGroup(Collection<RelicInfo> infos) {
        group = new ArrayList<>(infos);
    }

    public void add(AbstractRelic relic) {
        group.add(new RelicInfo(relic));
    }

    public void add(RelicInfo relicInfo) {
        group.add(relicInfo);
    }

    public void addAll(Collection<RelicInfo> relicInfo) {
        group.addAll(relicInfo);
    }

    public void clear() {
        group.clear();
    }

    @Override
    public Iterator<RelicInfo> iterator() {
        return group.iterator();
    }

    public int size() {
        return group.size();
    }

    public void sort(Comparator<? super RelicInfo> comparator) {
        group.sort(comparator);
    }

    public static class RelicInfo {
        public final AbstractRelic relic;
        public final AbstractCard.CardColor relicColor;
        public final boolean locked;

        public RelicInfo(AbstractRelic relic) {
            this.relic = relic;
            this.relicColor = EUIGameUtils.getRelicColor(relic.relicId);
            this.locked = UnlockTracker.isRelicLocked(relic.relicId);
        }
    }
}
