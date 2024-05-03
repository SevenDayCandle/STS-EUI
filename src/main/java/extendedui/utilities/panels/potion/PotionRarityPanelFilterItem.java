package extendedui.utilities.panels.potion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.PotionInfo;

public class PotionRarityPanelFilterItem implements CountingPanelItem<PotionInfo> {
    protected static final PotionRarityPanelFilterItem COMMON = new PotionRarityPanelFilterItem(AbstractPotion.PotionRarity.COMMON);
    protected static final PotionRarityPanelFilterItem UNCOMMON = new PotionRarityPanelFilterItem(AbstractPotion.PotionRarity.UNCOMMON);
    protected static final PotionRarityPanelFilterItem RARE = new PotionRarityPanelFilterItem(AbstractPotion.PotionRarity.RARE);
    protected static final PotionRarityPanelFilterItem SPECIAL = new PotionRarityPanelFilterItem(AbstractPotion.PotionRarity.PLACEHOLDER);
    public final AbstractPotion.PotionRarity rarity;

    public PotionRarityPanelFilterItem(AbstractPotion.PotionRarity rarity) {
        this.rarity = rarity;
    }

    public static PotionRarityPanelFilterItem get(AbstractPotion.PotionRarity rarity) {
        switch (rarity) {
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

    @Override
    public Color getColor() {
        return EUIGameUtils.colorForPotionRarity(rarity);
    }

    @Override
    public Texture getIcon() {
        return EUIRM.images.squaredButton2.texture();
    }

    @Override
    public EUITooltip getTipForButton() {
        return new EUITooltip(EUIGameUtils.textForPotionRarity(rarity), EUIRM.strings.misc_countPanelItem);
    }

    @Override
    public int getRank(PotionInfo c) {
        int ordinal = c.potion.rarity.ordinal();
        return c.potion.rarity == rarity ? ordinal + 1000 : ordinal;
    }
}
