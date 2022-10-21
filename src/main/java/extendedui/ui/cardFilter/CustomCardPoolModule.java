package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.EUIBase;

import java.util.ArrayList;

public abstract class CustomCardPoolModule extends EUIBase
{
    public abstract void Open(ArrayList<AbstractCard> cards);
    public abstract void Update(boolean shouldDoStandardUpdate);

    public boolean TryUpdate()
    {
        return TryUpdate(true);
    }
    public boolean TryUpdate(boolean shouldDoStandardUpdate)
    {
        if (isActive)
        {
            Update(shouldDoStandardUpdate);
        }

        return isActive;
    }
    public void Update()
    {
        Update(true);
    }
}
