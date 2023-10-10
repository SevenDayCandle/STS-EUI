package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;

import java.util.ArrayList;
import java.util.List;

public enum CostFilter {
    CostX("X", -1, -1),
    Cost0("0", 0, 0),
    Cost1("1", 1, 1),
    Cost2("2", 2, 2),
    Cost3("3", 3, 3),
    Cost4("4", 4, 4),
    Cost5("5+", 5, Integer.MAX_VALUE),
    Unplayable(EUIRM.strings.ui_na, Integer.MIN_VALUE, -2);

    public final int lowerBound;
    public final int upperBound;
    public final String name;

    CostFilter(String name, int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.name = name;
    }

    public static ArrayList<String> getCostRangeStrings(List<CostFilter> costs) {
        ArrayList<String> ranges = new ArrayList<>();
        if (costs == null) {
            return ranges;
        }
        boolean[] range = new boolean[7];
        for (CostFilter cost : costs) {
            if (cost != null) {
                if (cost.upperBound < 0) {
                    ranges.add(cost.name);
                }
                else {
                    range[cost.lowerBound] = true;
                }
            }
        }
        Integer lo = null;
        Integer hi = null;
        for (int i = 0; i < range.length; i++) {
            if (range[i]) {
                if (lo == null) {
                    lo = i;
                }
                hi = i;
            }
            else if (hi != null) {
                ranges.add(i == range.length - 1 ? lo + "+" :
                        !lo.equals(hi) ? lo + "-" + hi : String.valueOf(lo));
                lo = hi = null;
            }
        }
        return ranges;
    }

    public boolean check(AbstractCard c) {
        return (c.cost >= lowerBound && c.cost <= upperBound);
    }
}
