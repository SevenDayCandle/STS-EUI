package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.interfaces.markers.CountingPanelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// K is a game object to be counted (e.g. AbstractCard)
// J is an intermediate group that holds things of type K (e.g. CardGroup)
public class CountingPanelStats<T extends CountingPanelItem<K>, J, K> implements Iterable<Map.Entry<T, Integer>> {
    private final HashMap<T, Integer> groups = new HashMap<>();
    private final FuncT1<Iterable<? extends J>, K> vendorFunc;
    private final FuncT1<T, J> keyFunc;
    private final FuncT1<Integer, J> countingFunc;
    private final FuncT2<Integer, Iterable<? extends J>, K> amountFunc;
    private int totalK;

    public CountingPanelStats(FuncT1<Iterable<? extends J>, K> vendorFunc, FuncT1<T, J> keyFunc, FuncT1<Integer, J> countingFunc, FuncT2<Integer, Iterable<? extends J>, K> amountFunc, Iterable<? extends K> items) {
        this(vendorFunc, keyFunc, countingFunc, amountFunc);
        addItems(items);
    }

    public CountingPanelStats(FuncT1<Iterable<? extends J>, K> vendorFunc, FuncT1<T, J> keyFunc, FuncT1<Integer, J> countingFunc, FuncT2<Integer, Iterable<? extends J>, K> amountFunc) {
        this.vendorFunc = vendorFunc;
        this.keyFunc = keyFunc;
        this.countingFunc = countingFunc;
        this.amountFunc = amountFunc;
    }

    public static <T extends CountingPanelItem<K>, K> CountingPanelStats<T, T, K> basic(FuncT1<Iterable<? extends T>, K> vendorFunc) {
        return new CountingPanelStats<>(vendorFunc,
                (a) -> {
                    return a;
                },
                (a) -> {
                    return 1;
                },
                (res, i) -> {
                    return 1;
                }
        );
    }

    public static <T extends CountingPanelItem<K>, K> CountingPanelStats<T, T, K> basic(FuncT1<Iterable<? extends T>, K> vendorFunc, Iterable<? extends K> items) {
        return new CountingPanelStats<>(vendorFunc,
                (a) -> {
                    return a;
                },
                (a) -> {
                    return 1;
                },
                (res, b) -> {
                    return 1;
                },
                items
        );
    }

    public void addItem(K item) {
        Iterable<? extends J> results = vendorFunc.invoke(item);
        totalK += amountFunc.invoke(results, item);
        for (J res : results) {
            groups.merge(keyFunc.invoke(res), countingFunc.invoke(res), Integer::sum);
        }
    }

    public void addItems(Iterable<? extends K> items) {
        for (K item : items) {
            addItem(item);
        }
    }

    public ArrayList<CountingPanelCounter<T, K>> generateCounters(Hitbox baseHb, ActionT1<CountingPanelCounter<? extends CountingPanelItem<K>, K>> onClick) {
        ArrayList<CountingPanelCounter<T, K>> base = sortedStream().map(entry -> new CountingPanelCounter<T, K>(this, baseHb, entry.getKey(), onClick)).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < base.size(); i++) {
            base.get(i).setIndex(i);
        }
        return base;
    }

    public int getAmount(T key) {
        return groups.getOrDefault(key, 0);
    }

    public float getPercentage(T key) {
        return totalK <= 0 ? 0 : getAmount(key) / (float) totalK;
    }

    public String getPercentageString(T key) {
        return Math.round(getPercentage(key) * 100) + "%";
    }

    public ArrayList<Map.Entry<T, Integer>> getSortedItems() {
        return sortedStream().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Iterator<Map.Entry<T, Integer>> iterator() {
        return sortedStream().iterator();
    }

    public void reset() {
        totalK = 0;
        groups.clear();
    }

    public int size() {
        return totalK;
    }

    protected Stream<Map.Entry<T, Integer>> sortedStream() {
        // Descending value
        return groups.entrySet().stream().sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()));
    }
}