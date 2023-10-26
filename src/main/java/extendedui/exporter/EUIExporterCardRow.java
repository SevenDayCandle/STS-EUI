package extendedui.exporter;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.TargetFilter;

public class EUIExporterCardRow extends EUIExporterRow {
    public String Asset_URL;
    public String Type;
    public String Rarity;
    public String Card_Target;
    public int Cost;
    public int Cost_Upgrade;
    public int Damage;
    public int Damage_Upgrade;
    public int Block;
    public int Block_Upgrade;
    public int Magic_Number;
    public int Magic_Number_Upgrade;
    public int Heal;
    public int Heal_Upgrade;
    public Object Card_Tags;
    public String Effects;

    public EUIExporterCardRow(AbstractCard card, EUIExporter.ExportType type) {
        super(card.cardID, card, card.color, card.name);
        Asset_URL = card.assetUrl;
        Type = EUIGameUtils.textForType(card.type);
        Rarity = EUIGameUtils.textForRarity(card.rarity);
        Card_Target = TargetFilter.forCard(card).name;
        Damage = card.baseDamage;
        Block = card.baseBlock;
        Magic_Number = card.magicNumber;
        Heal = card.baseHeal;
        Cost = card.cost;

        try {
            AbstractCard upgrade = card.makeSameInstanceOf();
            upgrade.upgrade();
            Damage_Upgrade = upgrade.baseDamage - Damage;
            Block_Upgrade = upgrade.baseBlock - Block;
            Magic_Number_Upgrade = upgrade.baseMagicNumber - Magic_Number;
            Heal_Upgrade = upgrade.baseHeal - Heal;
            Cost_Upgrade = upgrade.cost - Cost;
            Effects = parseCardString(card, upgrade);
        }
        catch (Exception e) {
            e.printStackTrace();
            Damage_Upgrade = 0;
            Block_Upgrade = 0;
            Magic_Number_Upgrade = 0;
            Heal_Upgrade = 0;
            Cost_Upgrade = 0;
            Effects = parseCardString(card, null);
        }

        if (type == EUIExporter.ExportType.CSV) {
            Card_Tags = EUIUtils.joinStringsMap("/", AbstractCard.CardTags::name, card.tags);
        }
        else {
            Card_Tags = card.tags;
        }
    }

    private static String getDynavarString(String key, AbstractCard card, AbstractCard upgrade) {
        DynamicVariable var = BaseMod.cardDynamicVariableMap.get(key);
        if (var != null) {
            int base = var.baseValue(card);
            int upVal = upgrade != null ? var.baseValue(upgrade) : base;
            return base != upVal ? base + " (" + upVal + ")" : String.valueOf(base);
        }
        return EUIUtils.EMPTY_STRING;
    }

    private static String parseCardString(AbstractCard card, AbstractCard upgrade) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbd = new StringBuilder();
        boolean isDynavar = false;
        for (int i = 0; i < card.rawDescription.length(); i++) {
            char c = card.rawDescription.charAt(i);
            switch (c) {
                case '!':
                    sbd.setLength(0);
                    while (i + 1 < card.rawDescription.length()) {
                        i++;
                        c = card.rawDescription.charAt(i);
                        if (c == '!') {
                            sb.append(getDynavarString(sbd.toString(), card, upgrade));
                            break;
                        }
                        else {
                            sbd.append(c);
                        }
                    }
                    break;
                case '*':
                case '[':
                case ']':
                    continue;
                default:
                    // I hate NL
                    if (Character.isWhitespace(c) && i + 3 < card.rawDescription.length() &&
                    card.rawDescription.charAt(i + 1) == 'N' && card.rawDescription.charAt(i + 2) == 'L' && Character.isWhitespace(card.rawDescription.charAt(i + 3))) {
                        i += 3;
                        sb.append(" ");
                    }
                    else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

}
