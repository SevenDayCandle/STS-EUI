package extendedui.interfaces.markers;

import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    List<EUITooltip> GetTips();
    default EUITooltip GetTooltip() {
        List<EUITooltip> tooltips = GetTips();
        return tooltips != null && tooltips.size() > 0 ? GetTips().get(0) : null;
    }
    default EUICardPreview GetPreview() {return null;}
    default boolean IsPopup() {return false;}
    default void GenerateDynamicTooltips(ArrayList<EUITooltip> dynamicTooltips) {}
    // For use with cards that should act differently when rendered through EUICardPreview
    default void SetIsPreview(boolean value) {}
}
