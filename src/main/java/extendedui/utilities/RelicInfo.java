package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;

public class RelicInfo {
    public final AbstractRelic relic;
    public final AbstractCard.CardColor relicColor;
    public boolean locked;
    public boolean faded;

    public RelicInfo(AbstractRelic relic) {
        this.relic = relic;
        this.relicColor = EUIGameUtils.getRelicColor(relic.relicId);
        this.locked = UnlockTracker.isRelicLocked(relic.relicId);
    }

    public RelicInfo(AbstractRelic relic, boolean locked) {
        this.relic = relic;
        this.relicColor = EUIGameUtils.getRelicColor(relic.relicId);
        this.locked = locked;
    }
}
