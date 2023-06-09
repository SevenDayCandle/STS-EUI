package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.PotionGroup;
import extendedui.utilities.RelicGroup;

import java.util.Collection;

public interface CustomRelicFilterModule extends CustomFilterModule<AbstractRelic> {
    default boolean isItemValid(RelicGroup.RelicInfo c) {
        return isItemValid(c.relic);
    }
    default void processGroup(RelicGroup group) {
    }
}
