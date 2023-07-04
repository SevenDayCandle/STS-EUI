package extendedui.exporter;

import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.utilities.PotionInfo;

public class EUIExporterPotionRow extends EUIExporterRow {
    public String rarity;
    public String size;
    public String potionColor;
    public String description;
    public int potency;

    public EUIExporterPotionRow(AbstractPotion potion) {
        super(potion.ID, String.valueOf(EUIGameUtils.getPotionColor(potion.ID)), potion.name);
        rarity = String.valueOf(potion.rarity);
        size = String.valueOf(potion.size);
        potionColor = String.valueOf(potion.color);
        description = sanitizeDescription(potion.description);
        potency = potion.getPotency();
    }

    public EUIExporterPotionRow(PotionInfo potion) {
        super(potion.potion.ID, String.valueOf(potion.potionColor), potion.potion.name);
        rarity = String.valueOf(potion.potion.rarity);
        size = String.valueOf(potion.potion.size);
        potionColor = String.valueOf(potion.potion.color);
        description = sanitizeDescription(potion.potion.description);
        potency = potion.potion.getPotency();
    }
}
