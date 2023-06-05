package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public interface CustomPotionPoolModule extends IUIElement {
    default void onClose() {
    }

    void open(ArrayList<AbstractPotion> potions, AbstractCard.CardColor color, Object payload);
}
