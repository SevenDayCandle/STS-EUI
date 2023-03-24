package extendedui.ui.cardFilter.filters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;

import java.util.Collections;
import java.util.List;

public class CardRarityPanelFilterItem implements CountingPanelItem, TooltipProvider
{
    protected static final CardRarityPanelFilterItem COMMON = new CardRarityPanelFilterItem(AbstractCard.CardRarity.COMMON);
    protected static final CardRarityPanelFilterItem UNCOMMON = new CardRarityPanelFilterItem(AbstractCard.CardRarity.UNCOMMON);
    protected static final CardRarityPanelFilterItem RARE = new CardRarityPanelFilterItem(AbstractCard.CardRarity.RARE);
    protected static final CardRarityPanelFilterItem SPECIAL = new CardRarityPanelFilterItem(AbstractCard.CardRarity.SPECIAL);
    public final AbstractCard.CardRarity rarity;

    public static CardRarityPanelFilterItem get(AbstractCard.CardRarity rarity)
    {
        switch (rarity)
        {
            case BASIC:
            case COMMON:
                return COMMON;
            case RARE:
                return RARE;
            case UNCOMMON:
                return UNCOMMON;
            default:
                return SPECIAL;
        }
    }

    public CardRarityPanelFilterItem(AbstractCard.CardRarity rarity)
    {
        this.rarity = rarity;
    }

    @Override
    public Color getColor()
    {
        return EUIGameUtils.colorForRarity(rarity);
    }

    @Override
    public Texture getIcon()
    {
        return EUIRM.images.squaredButton2.texture();
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return EUIUtils.list(new EUITooltip(EUIGameUtils.textForRarity(rarity)));
    }
}