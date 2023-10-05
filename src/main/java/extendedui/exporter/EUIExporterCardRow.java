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
        effects = parseCardString(card);

        try {
            AbstractCard upgrade = card.makeSameInstanceOf();
            upgrade.upgrade();
            damageUpgrade = card.baseDamage - damage;
            blockUpgrade = card.baseBlock - block;
            magicNumberUpgrade = card.baseMagicNumber - magicNumber;
            healUpgrade = card.baseHeal - heal;
            costUpgrade = card.cost - cost;
        }
        catch (Exception e) {
            damageUpgrade = 0;
            blockUpgrade = 0;
            magicNumberUpgrade = 0;
            healUpgrade = 0;
            costUpgrade = 0;
        }
    }

    public static String parseCardString(AbstractCard card) {
        return card.rawDescription
                .replace("!D!", String.valueOf(card.baseDamage))
                .replace("!B!", String.valueOf(card.baseBlock))
                .replace("!M!", String.valueOf(card.baseMagicNumber))
                .replace(" NL ", " ");
    }
}
