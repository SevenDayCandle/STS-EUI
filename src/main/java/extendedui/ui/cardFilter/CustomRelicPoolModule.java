package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.GUI_Base;

import java.util.ArrayList;

public abstract class CustomRelicPoolModule extends GUI_Base
{
    public abstract void Open(ArrayList<AbstractRelic> cards);
}
