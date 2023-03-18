package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public interface CustomCardPoolModule extends IUIElement
{
    public abstract void open(ArrayList<AbstractCard> cards);
    public default void onClose() {}
}
