package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUITooltip;

// Marker used to denote objects that have a PCLCardTooltip
public interface TooltipObject {
    public abstract EUITooltip GetTooltip();
}
