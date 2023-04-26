package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public interface CustomCardPoolModule extends IUIElement {
    default void onClose() {
    }

    void open(ArrayList<AbstractCard> cards);
}
