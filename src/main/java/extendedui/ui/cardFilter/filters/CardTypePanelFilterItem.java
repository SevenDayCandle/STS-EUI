package extendedui.ui.cardFilter.filters;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;

import java.util.Collections;
import java.util.List;

public class CardTypePanelFilterItem implements CountingPanelItem, TooltipProvider {
    protected static final CardTypePanelFilterItem ATTACK = new CardTypePanelFilterItem(AbstractCard.CardType.ATTACK);
    protected static final CardTypePanelFilterItem CURSE = new CardTypePanelFilterItem(AbstractCard.CardType.CURSE);
    protected static final CardTypePanelFilterItem POWER = new CardTypePanelFilterItem(AbstractCard.CardType.POWER);
    protected static final CardTypePanelFilterItem SKILL = new CardTypePanelFilterItem(AbstractCard.CardType.SKILL);
    protected static final CardTypePanelFilterItem STATUS = new CardTypePanelFilterItem(AbstractCard.CardType.STATUS);

    public final AbstractCard.CardType type;

    public CardTypePanelFilterItem(AbstractCard.CardType type) {
        this.type = type;
    }

    public static CardTypePanelFilterItem get(AbstractCard.CardType type) {
        switch (type) {
            case ATTACK:
                return ATTACK;
            case CURSE:
                return CURSE;
            case POWER:
                return POWER;
            case SKILL:
                return SKILL;
            default:
                return STATUS;
        }
    }

    @Override
    public int getRank(AbstractCard c) {
        int ordinal = c.type.ordinal();
        return c.type == type ? ordinal + 1000 : ordinal;
    }

    @Override
    public Texture getIcon() {
        return EUIGameUtils.iconForType(type).texture();
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(new EUITooltip(EUIGameUtils.textForType(type)));
    }
}
