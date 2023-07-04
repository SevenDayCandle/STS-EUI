package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

public interface CustomCardFilterModule extends CustomFilterModule<AbstractCard> {
    default void processGroup(CardGroup group) {
    }

}
