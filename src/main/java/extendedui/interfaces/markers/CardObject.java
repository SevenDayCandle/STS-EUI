package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;

// Marker used to denote objects that generate cards
public interface CardObject
{
    public abstract AbstractCard GetCard();
}
