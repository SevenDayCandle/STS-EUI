package extendedui.interfaces.markers;

import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;

import java.util.List;

public interface KeywordProvider extends TooltipProvider {

    default List<EUIKeywordTooltip> getTipsForFilters() {
        return getTipsForRender();
    }

    // For use with cards that should act differently when rendered through EUICardPreview
    default void setIsPreview(boolean value) {
    }

    @Override
    List<EUIKeywordTooltip> getTips();

    @Override
    default List<EUIKeywordTooltip> getTipsForRender() {
        return getTips();
    }
}
