package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.ui.EUIBase;

import java.util.Collection;

public abstract class CustomCardFilterModule extends EUIBase
{
    public abstract boolean isCardValid(AbstractCard c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractCard> cards);
    public abstract void reset();
    public void processGroup(CardGroup group) {}
}
