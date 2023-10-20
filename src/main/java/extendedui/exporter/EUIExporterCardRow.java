package extendedui.exporter;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.utilities.TargetFilter;

public class EUIExporterCardRow extends EUIExporterRow {
    public String assetURL;
    public String type;
    public String rarity;
    public String cardTarget;
    public int damage;
    public int damageUpgrade;
    public int block;
    public int blockUpgrade;
    public int magicNumber;
    public int magicNumberUpgrade;
    public int heal;
    public int healUpgrade;
    public int cost;
    public int costUpgrade;
    public String effects;

    public EUIExporterCardRow(AbstractCard card) {
        super(card.cardID, card, card.color, card.name);
        assetURL = card.assetUrl;
        type = EUIGameUtils.textForType(card.type);
        rarity = EUIGameUtils.textForRarity(card.rarity);
        cardTarget = TargetFilter.forCard(card).name;
        damage = card.baseDamage;
        block = card.baseBlock;
        magicNumber = card.magicNumber;
        heal = card.baseHeal;
        cost = card.cost;

        try {
            AbstractCard upgrade = card.makeSameInstanceOf();
            upgrade.upgrade();
            damageUpgrade = upgrade.baseDamage - damage;
            blockUpgrade = upgrade.baseBlock - block;
            magicNumberUpgrade = upgrade.baseMagicNumber - magicNumber;
            healUpgrade = upgrade.baseHeal - heal;
            costUpgrade = upgrade.cost - cost;
            effects = parseCardString(card, upgrade);
        }
        catch (Exception e) {
            e.printStackTrace();
            damageUpgrade = 0;
            blockUpgrade = 0;
            magicNumberUpgrade = 0;
            healUpgrade = 0;
            costUpgrade = 0;
            effects = parseCardString(card, null);
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
