package extendedui.ui.cardFilter;

import com.evacipated.cardcrawl.modthespire.ModInfo;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;

import java.util.HashSet;

public class GenericFiltersObject {
    public String currentDescription;
    public String currentName;
    public final HashSet<EUIKeywordTooltip> currentFilters = new HashSet<>();
    public final HashSet<EUIKeywordTooltip> currentNegateFilters = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();

    public void clear(boolean shouldClearColors) {
        currentName = null;
        currentDescription = null;
        currentFilters.clear();
        currentNegateFilters.clear();
        currentOrigins.clear();
    }

    public void cloneFrom(GenericFiltersObject other) {
        currentDescription = other.currentDescription;
        currentName = other.currentName;
        EUIUtils.replaceContents(currentFilters, other.currentFilters);
        EUIUtils.replaceContents(currentNegateFilters, other.currentNegateFilters);
        EUIUtils.replaceContents(currentOrigins, other.currentOrigins);
    }

    public boolean isEmpty() {
        return (currentName == null || currentName.isEmpty())
                && (currentDescription == null || currentDescription.isEmpty())
                && currentOrigins.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty();
    }
}
