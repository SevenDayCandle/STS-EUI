package extendedui.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.TargetFilter;

public interface CacheableCard extends CustomFilterable {
    AbstractCard getCachedUpgrade();

    TargetFilter getTargetFilter();
}
