package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.Collection;

public interface CustomCardFilterModule extends IUIElement {
    void initializeSelection(Collection<AbstractCard> cards);

    boolean isCardValid(AbstractCard c);

    boolean isEmpty();

    boolean isHovered();

    default void processGroup(CardGroup group) {
    }

    void reset();
}
