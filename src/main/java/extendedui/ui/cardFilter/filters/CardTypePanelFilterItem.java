package extendedui.ui.cardFilter.filters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;

import java.util.List;

public class CardTypePanelFilterItem implements CountingPanelItem, TooltipProvider
{
    protected static final CardTypePanelFilterItem ATTACK = new CardTypePanelFilterItem(AbstractCard.CardType.ATTACK);
    protected static final CardTypePanelFilterItem CURSE = new CardTypePanelFilterItem(AbstractCard.CardType.CURSE);
    protected static final CardTypePanelFilterItem POWER = new CardTypePanelFilterItem(AbstractCard.CardType.POWER);
    protected static final CardTypePanelFilterItem SKILL = new CardTypePanelFilterItem(AbstractCard.CardType.SKILL);
    protected static final CardTypePanelFilterItem STATUS = new CardTypePanelFilterItem(AbstractCard.CardType.STATUS);

    public final AbstractCard.CardType type;

    public static CardTypePanelFilterItem get(AbstractCard.CardType type)
    {
        switch (type)
        {
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

    public CardTypePanelFilterItem(AbstractCard.CardType type)
    {
        this.type = type;
    }

    @Override
    public Texture getIcon()
    {
        return EUIGameUtils.iconForType(type).texture();
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return EUIUtils.list(new EUITooltip(EUIGameUtils.textForType(type)));
    }
}
