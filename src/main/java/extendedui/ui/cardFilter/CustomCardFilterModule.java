package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.ui.GUI_Base;

import java.util.Collection;

public abstract class CustomCardFilterModule extends GUI_Base {
    public abstract boolean IsCardValid(AbstractCard c);
    public abstract boolean IsEmpty();
    public abstract boolean IsHovered();
    public abstract void InitializeSelection(Collection<AbstractCard> cards);
    public abstract void Reset();
    public void ProcessGroup(CardGroup group) {}
}
