package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface KeywordProvider extends TooltipProvider {
    @Override
    List<EUIKeywordTooltip> getTips();

    default List<EUIKeywordTooltip> getTipsForFilters() {
        return getTipsForRender();
    }

    @Override
    default List<EUIKeywordTooltip> getTipsForRender() {
        return getTips();
    }

    // For use with cards that should act differently when rendered through EUICardPreview
    default void setIsPreview(boolean value) {
    }

    default boolean isPopup() {
        return false;
    }

    default EUICardPreview getPreview() {
        return null;
    }
}
