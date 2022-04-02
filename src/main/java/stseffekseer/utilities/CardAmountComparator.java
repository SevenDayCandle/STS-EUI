package stseffekseer.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import stseffekseer.JavaUtils;

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
        int a = CalculateRank(c1);
        int b = CalculateRank(c2);
        return ascending ? (a - b) : (b - a);
    }

    public static int CalculateRank(AbstractCard card)
    {
        return card.baseDamage > 0 ? card.baseDamage :
                Math.max(card.baseBlock, 0);
    }
}