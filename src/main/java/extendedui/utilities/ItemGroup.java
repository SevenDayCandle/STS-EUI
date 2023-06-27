package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class ItemGroup<T> implements Iterable<T> {
    public ArrayList<T> group;

    public ItemGroup() {
        group = new ArrayList<>();
    }

    public ItemGroup(Collection<? extends T> infos) {
        group = new ArrayList<>(infos);
    }

    public void add(T relicInfo) {
        group.add(relicInfo);
    }

    public void addAll(Collection<? extends T> relicInfo) {
        group.addAll(relicInfo);
    }

    public void clear() {
        group.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return group.iterator();
    }

    public int size() {
        return group.size();
    }

    public void sort(Comparator<? super T> comparator) {
        group.sort(comparator);
    }
}
