package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public interface CustomRelicPoolModule extends IUIElement {
    default void onClose() {
    }

    void open(ArrayList<AbstractRelic> relics, AbstractCard.CardColor color, Object payload);
}
