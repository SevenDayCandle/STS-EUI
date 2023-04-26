package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    default void generateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {
    }

    default EUITooltip getIntentTip() {
        return null;
    }

    default EUICardPreview getPreview() {
        return null;
    }

    default List<EUITooltip> getTipsForFilters() {
        return getTips();
    }

    default EUITooltip getTooltip() {
        List<EUITooltip> tooltips = getTips();
        return tooltips != null && tooltips.size() > 0 ? getTips().get(0) : null;
    }

    List<EUITooltip> getTips();

    default boolean isPopup() {
        return false;
    }

    // For use with cards that should act differently when rendered through EUICardPreview
    default void setIsPreview(boolean value) {
    }
}
