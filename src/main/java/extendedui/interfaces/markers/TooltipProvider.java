package extendedui.interfaces.markers;

import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.RotatingList;

import java.util.List;

public interface TooltipProvider {

    default void fillPreviews(RotatingList<EUIPreview> list) {
    }

    default EUIPreview getPreview() {
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
