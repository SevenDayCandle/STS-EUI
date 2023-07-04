package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public interface CustomPoolModule<T> extends IUIElement {
    default void onClose() {
    }

    void open(ArrayList<? extends T> cards, AbstractCard.CardColor color, Object payload);
}
