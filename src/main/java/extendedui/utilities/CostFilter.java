package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;

public enum CostFilter
{
    CostX("X", -1, -1),
    Cost0("0", 0, 0),
    Cost1("1", 1, 1),
    Cost2("2", 2, 2),
    Cost3("3", 3, 3),
    Cost4Plus("4+", 4, 9999),
    Unplayable(EUIRM.strings.na, -9999, -2);

    public final int lowerBound;
    public final int upperBound;
    public final String name;

    CostFilter(String name, int lowerBound, int upperBound)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.name = name;
    }

    public boolean check(AbstractCard c)
    {
        return (c.cost >= lowerBound && c.cost <= upperBound);
    }
}
