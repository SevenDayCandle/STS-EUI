package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    public List<EUITooltip> GetTips();
    public default EUITooltip GetTooltip() {
        List<EUITooltip> tooltips = GetTips();
        return tooltips != null && tooltips.size() > 0 ? GetTips().get(0) : null;
    };
    public default EUICardPreview GetPreview() {return null;}
    public default boolean IsPopup() {return false;}
    public default void GenerateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {}
    // For use with cards that should act differently when rendered through EUICardPreview
    public default void SetIsPreview(boolean value) {}
}
