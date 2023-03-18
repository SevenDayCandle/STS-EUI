package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.CardKeywordFilters;

import java.util.Collection;

public interface CustomCardFilterModule extends IUIElement
{
    public abstract boolean isCardValid(AbstractCard c);
    public abstract boolean isEmpty();
    public abstract boolean isHovered();
    public abstract void initializeSelection(Collection<AbstractCard> cards);
    public abstract void reset();
    public default void processGroup(CardGroup group) {}
}
