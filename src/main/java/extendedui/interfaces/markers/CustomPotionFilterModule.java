package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.utilities.ItemGroup;
import extendedui.utilities.PotionInfo;

public interface CustomPotionFilterModule extends CustomFilterModule<AbstractPotion> {
    default boolean isItemValid(PotionInfo c) {
        return isItemValid(c.potion);
    }
    default void processGroup(ItemGroup<PotionInfo> group) {
    }
}
