package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.utilities.PotionGroup;

import java.util.Collection;

public interface CustomPotionFilterModule extends CustomFilterModule<AbstractPotion> {
    default boolean isItemValid(PotionGroup.PotionInfo c) {
        return isItemValid(c.potion);
    }
    default void processGroup(PotionGroup group) {
    }
}
