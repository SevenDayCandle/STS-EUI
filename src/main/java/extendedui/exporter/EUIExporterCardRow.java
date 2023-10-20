package extendedui.exporter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;

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
        super(card.cardID, EUIGameUtils.getModID(card), String.valueOf(card.color), card.name);
        assetURL = card.assetUrl;
        type = String.valueOf(card.type);
        rarity = String.valueOf(card.rarity);
        cardTarget = String.valueOf(card.target);
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
        }
        catch (Exception e) {
            e.printStackTrace();
            damageUpgrade = 0;
            blockUpgrade = 0;
            magicNumberUpgrade = 0;
            healUpgrade = 0;
            costUpgrade = 0;
        }

        effects = parseCardString(card);
    }

    // TODO read from dynamic variable maps instead of doing manual checks
    public String parseCardString(AbstractCard card) {
        return card.rawDescription
                .replace("!D!", damageUpgrade != 0 ? card.baseDamage + " (" + (card.baseDamage + damageUpgrade) + ")" : String.valueOf(card.baseDamage))
                .replace("!B!", blockUpgrade != 0 ? card.baseBlock + " (" + (card.baseBlock + blockUpgrade) + ")" : String.valueOf(card.baseBlock))
                .replace("!M!", magicNumberUpgrade != 0 ? card.baseMagicNumber + " (" + (card.baseMagicNumber + magicNumberUpgrade) + ")" : String.valueOf(card.baseMagicNumber))
                .replace(" NL ", " ")
                .replaceAll("[*\\[\\]]", "");
    }

}
