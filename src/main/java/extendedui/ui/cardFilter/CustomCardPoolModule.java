package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public abstract class CustomCardPoolModule extends EUIBase
{
    public abstract void open(ArrayList<AbstractCard> cards);
    public abstract void update(boolean shouldDoStandardUpdate);

    public boolean tryUpdate()
    {
        return tryUpdate(true);
    }
    public boolean tryUpdate(boolean shouldDoStandardUpdate)
    {
        if (isActive)
        {
            update(shouldDoStandardUpdate);
        }

        return isActive;
    }
    public void updateImpl()
    {
        update(true);
    }
}
