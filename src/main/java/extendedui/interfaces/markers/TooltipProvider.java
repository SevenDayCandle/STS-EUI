package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.List;

public interface TooltipProvider {

    default EUICardPreview getPreview() {
        return null;
    }

    default List<? extends EUITooltip> getTipsForRender() {
        return getTips();
    }

    default EUITooltip getTooltip() {
        List<? extends EUITooltip> tooltips = getTips();
        return tooltips != null && tooltips.size() > 0 ? getTips().get(0) : null;
    }

    default boolean isPopup() {
        return false;
    }

    List<? extends EUITooltip> getTips();
}
