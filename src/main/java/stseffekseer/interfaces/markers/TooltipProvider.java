package stseffekseer.interfaces.markers;

import stseffekseer.ui.tooltips.EUICardPreview;
import stseffekseer.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    public List<EUITooltip> GetTips();
    public default EUICardPreview GetPreview() {return null;}
    public default void GenerateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {}
}
