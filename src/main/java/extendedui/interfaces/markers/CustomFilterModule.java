package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.Collection;

public interface CustomFilterModule<T> extends IUIElement {
    boolean isEmpty();
    boolean isHovered();
    boolean isItemValid(T c);
    void initializeSelection(Collection<? extends T> cards);
    void reset();
}
