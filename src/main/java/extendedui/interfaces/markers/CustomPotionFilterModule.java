package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.ui.controls.EUIPotionGrid;

import java.util.ArrayList;
import java.util.Collection;

public interface CustomPotionFilterModule extends IUIElement {
    void initializeSelection(Collection<AbstractPotion> cards);

    boolean isEmpty();

    boolean isHovered();

    default boolean isPotionValid(EUIPotionGrid.PotionInfo c) {
        return isPotionValid(c.potion);
    }

    boolean isPotionValid(AbstractPotion c);

    default void processGroup(ArrayList<AbstractPotion> group) {
    }

    void reset();
}
