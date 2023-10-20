package extendedui.exporter;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.PotionInfo;

public class EUIExporterPotionRow extends EUIExporterRow {
    public String rarity;
    public String size;
    public String potionColor;
    public String description;
    public int potency;

    public EUIExporterPotionRow(PotionInfo potion) {
        super(potion.potion.ID, potion.potion, potion.potionColor, potion.potion.name);
        rarity = EUIGameUtils.textForPotionRarity(potion.potion.rarity);
        size = EUIGameUtils.textForPotionSize(potion.potion.size);
        potionColor = EUIUtils.capitalize(String.valueOf(potion.potion.color));
        description = sanitizeDescription(potion.potion.description);
        potency = potion.potion.getPotency();
    }
}
