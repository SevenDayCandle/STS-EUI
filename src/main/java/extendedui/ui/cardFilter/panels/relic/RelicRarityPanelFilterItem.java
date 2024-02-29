package extendedui.ui.cardFilter.panels.relic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.RelicInfo;

import java.util.Collections;
import java.util.List;

public class RelicRarityPanelFilterItem implements CountingPanelItem<RelicInfo> {
    protected static final RelicRarityPanelFilterItem STARTER = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.STARTER);
    protected static final RelicRarityPanelFilterItem COMMON = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.COMMON);
    protected static final RelicRarityPanelFilterItem UNCOMMON = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.UNCOMMON);
    protected static final RelicRarityPanelFilterItem RARE = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.RARE);
    protected static final RelicRarityPanelFilterItem SHOP = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.SHOP);
    protected static final RelicRarityPanelFilterItem BOSS = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.BOSS);
    protected static final RelicRarityPanelFilterItem SPECIAL = new RelicRarityPanelFilterItem(AbstractRelic.RelicTier.SPECIAL);
    public final AbstractRelic.RelicTier rarity;

    public RelicRarityPanelFilterItem(AbstractRelic.RelicTier rarity) {
        this.rarity = rarity;
    }

    public static RelicRarityPanelFilterItem get(AbstractRelic.RelicTier rarity) {
        switch (rarity) {
            case STARTER:
                return STARTER;
            case COMMON:
                return COMMON;
            case RARE:
                return RARE;
            case UNCOMMON:
                return UNCOMMON;
            case BOSS:
                return BOSS;
            case SHOP:
                return SHOP;
            default:
                return SPECIAL;
        }
    }

    @Override
    public Color getColor() {
        return EUIGameUtils.colorForRelicTier(rarity);
    }

    @Override
    public Texture getIcon() {
        return EUIRM.images.squaredButton2.texture();
    }

    @Override
    public EUITooltip getTipForButton() {
        return new EUITooltip(EUIGameUtils.textForRelicTier(rarity), EUIRM.strings.misc_countPanelItem);
    }

    @Override
    public int getRank(RelicInfo c) {
        int ordinal = c.relic.tier.ordinal();
        return c.relic.tier == rarity ? ordinal + 1000 : ordinal;
    }
}
