package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {

    List<? extends EUITooltip> getTips();

    default EUITooltip getTooltip() {
        List<? extends EUITooltip> tooltips = getTips();
        return tooltips != null && tooltips.size() > 0 ? getTips().get(0) : null;
    }

    default List<? extends EUITooltip> getTipsForRender() {
        return getTips();
    }

    default boolean isPopup() {
        return false;
    }

    default EUICardPreview getPreview() {
        return null;
    }
}
