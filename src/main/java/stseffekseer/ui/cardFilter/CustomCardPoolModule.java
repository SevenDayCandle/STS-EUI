package stseffekseer.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import stseffekseer.ui.GUI_Base;

import java.util.ArrayList;

public abstract class CustomCardPoolModule extends GUI_Base
{
    public abstract void Open(ArrayList<AbstractCard> cards);
}
