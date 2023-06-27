package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.ItemGroup;
import extendedui.utilities.RelicInfo;

public interface CustomRelicFilterModule extends CustomFilterModule<AbstractRelic> {
    default boolean isItemValid(RelicInfo c) {
        return isItemValid(c.relic);
    }
    default void processGroup(ItemGroup<RelicInfo> group) {
    }
}
