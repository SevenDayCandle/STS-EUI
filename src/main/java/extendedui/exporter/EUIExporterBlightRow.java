package extendedui.exporter;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class EUIExporterBlightRow extends EUIExporterRow {
    public boolean Unique;
    public String Description;

    public EUIExporterBlightRow(AbstractBlight blight) {
        super(blight.blightID, blight, AbstractCard.CardColor.COLORLESS, blight.name);
        Unique = blight.unique;
        Description = sanitizeDescription(blight.description);
    }
}
