package extendedui.exporter;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.PotionInfo;

public class EUIExporterPotionRow extends EUIExporterRow {
    public String Rarity;
    public String Size;
    public String Potion_Color;
    public String Description;
    public int Potency;

    public EUIExporterPotionRow(PotionInfo potion) {
        super(potion.potion.ID, potion.potion, potion.potionColor, potion.potion.name);
        Rarity = EUIGameUtils.textForPotionRarity(potion.potion.rarity);
        Size = EUIGameUtils.textForPotionSize(potion.potion.size);
        Potion_Color = EUIUtils.capitalize(String.valueOf(potion.potion.color));
        Description = sanitizeDescription(potion.potion.description);
        Potency = potion.potion.getPotency();
    }
}
