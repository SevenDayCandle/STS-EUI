package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.controls.EUIRelicGrid;

import java.util.ArrayList;
import java.util.Collection;

public interface CustomRelicFilterModule extends IUIElement {
    void initializeSelection(Collection<AbstractRelic> cards);

    boolean isEmpty();

    boolean isHovered();

    default boolean isRelicValid(EUIRelicGrid.RelicInfo c) {
        return isRelicValid(c.relic);
    }

    boolean isRelicValid(AbstractRelic c);

    default void processGroup(ArrayList<AbstractRelic> group) {
    }

    void reset();
}
