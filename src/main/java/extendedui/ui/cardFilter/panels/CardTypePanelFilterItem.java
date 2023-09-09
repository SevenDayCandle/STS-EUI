package extendedui.ui.cardFilter.panels;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.Collections;
import java.util.List;

public class CardTypePanelFilterItem implements CountingPanelItem, TooltipProvider {
    private static final CardTypePanelFilterItem ATTACK = new CardTypePanelFilterItem(AbstractCard.CardType.ATTACK);
    private static final CardTypePanelFilterItem CURSE = new CardTypePanelFilterItem(AbstractCard.CardType.CURSE);
    private static final CardTypePanelFilterItem POWER = new CardTypePanelFilterItem(AbstractCard.CardType.POWER);
    private static final CardTypePanelFilterItem SKILL = new CardTypePanelFilterItem(AbstractCard.CardType.SKILL);
    private static final CardTypePanelFilterItem STATUS = new CardTypePanelFilterItem(AbstractCard.CardType.STATUS);

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
    public Texture getIcon() {
        return EUIGameUtils.iconForType(type).texture();
    }

    @Override
    public int getRank(AbstractCard c) {
        int ordinal = c.type.ordinal();
        return c.type == type ? ordinal + 1000 : ordinal;
    }

    @Override
    public List<EUITooltip> getTips() {
        EUITooltip tip = EUIKeywordTooltip.findByID(EUIUtils.capitalize(type.name()));
        return Collections.singletonList(tip != null ? tip : new EUITooltip(EUIGameUtils.textForType(type)));
    }
}
