package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.Collection;

public interface CustomCardFilterModule extends CustomFilterModule<AbstractCard> {
    default void processGroup(CardGroup group) {
    }

}
