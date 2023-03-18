package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public interface CustomRelicPoolModule extends IUIElement
{
    public abstract void open(ArrayList<AbstractRelic> cards);
    public default void onClose() {}
}
