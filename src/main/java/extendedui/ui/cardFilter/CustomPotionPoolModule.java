package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public abstract class CustomPotionPoolModule extends EUIBase
{
    public abstract void open(ArrayList<AbstractPotion> cards);
}
