package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CacheableCard;

public class TargetFilter {

    public static final TargetFilter None = new TargetFilter(EUIRM.strings.target_none);
    public static final TargetFilter All = new TargetFilter(EUIRM.strings.target_allCharacter);
    public static final TargetFilter AllEnemy = new TargetFilter(EUIRM.strings.target_allEnemy);
    public static final TargetFilter Any = new TargetFilter(EUIRM.strings.target_any);
    public static final TargetFilter Self = new TargetFilter(EUIRM.strings.target_self);
    public static final TargetFilter SingleEnemy = new TargetFilter(EUIRM.strings.target_singleEnemy);

    public final String name;

    public TargetFilter(String name) {
        this.name = name;
    }

    public static TargetFilter forCard(AbstractCard card) {
        return card instanceof CacheableCard ? ((CacheableCard) card).getTargetFilter() : forCardTarget(card.target);
    }

    public static TargetFilter forCardTarget(AbstractCard.CardTarget target) {
        switch (target) {
            case NONE:
                return None;
            case ALL:
                return All;
            case ALL_ENEMY:
                return AllEnemy;
            case SELF_AND_ENEMY:
                return Any;
            case SELF:
                return Self;
            case ENEMY:
                return SingleEnemy;
        }
        return None;
    }

    public static TargetFilter forPotion(AbstractPotion potion) {
        return potion.targetRequired ? SingleEnemy : potion.isThrown ? Any : Self;
    }
}
