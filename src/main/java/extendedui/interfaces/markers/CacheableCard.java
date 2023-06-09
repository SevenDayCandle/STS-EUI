package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface CacheableCard extends CustomFilterable {
    AbstractCard getCachedUpgrade();
}
