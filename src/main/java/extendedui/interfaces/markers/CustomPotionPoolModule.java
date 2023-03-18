package extendedui.interfaces.markers;

import basemod.IUIElement;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public interface CustomPotionPoolModule extends IUIElement
{
    public abstract void open(ArrayList<AbstractPotion> cards);
    public default void onClose() {}
}
