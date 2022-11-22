package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    List<EUITooltip> getTips();
    default EUITooltip getTooltip() {
        List<EUITooltip> tooltips = getTips();
        return tooltips != null && tooltips.size() > 0 ? getTips().get(0) : null;
    }
    default List<EUITooltip> getTipsForFilters() {
        return getTips();
    }
    default EUICardPreview getPreview() {return null;}
    default boolean isPopup() {return false;}
    default void generateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {}
    // For use with cards that should act differently when rendered through EUICardPreview
    default void setIsPreview(boolean value) {}
}
