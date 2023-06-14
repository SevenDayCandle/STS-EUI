package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUITourTooltip;

public interface TourProvider {
    default boolean isComplete() {
        return true;
    }
    default void onComplete() {

    }
}
