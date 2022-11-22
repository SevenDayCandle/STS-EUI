package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Comparator;

public class CardAmountComparator implements Comparator<AbstractCard>
{
    private final boolean ascending;

    public CardAmountComparator()
    {
        this(false);
    }

    public CardAmountComparator(boolean ascending)
    {
        this.ascending = ascending;
    }

    public int compare(AbstractCard c1, AbstractCard c2)
    {
        int a = calculateRank(c1);
        int b = calculateRank(c2);
        return ascending ? (a - b) : (b - a);
    }

    public static int calculateRank(AbstractCard card)
    {
        return card.baseDamage > 0 ? card.baseDamage :
                Math.max(card.baseBlock, 0);
    }
}