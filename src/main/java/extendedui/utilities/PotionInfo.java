package extendedui.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;

public class PotionInfo {
    public final AbstractPotion potion;
    public final AbstractCard.CardColor potionColor;

    public PotionInfo(AbstractPotion potion) {
        this.potion = potion;
        this.potionColor = EUIGameUtils.getPotionColor(potion.ID);
    }
}
