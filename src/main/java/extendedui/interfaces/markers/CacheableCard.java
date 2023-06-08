package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface CacheableCard {
    AbstractCard getCachedUpgrade();
    String getDescriptionForSort();
    String getNameForSort();
}
