package extendedui.exporter;

import com.megacrit.cardcrawl.blights.AbstractBlight;

public class EUIExporterBlightRow extends EUIExporterRow {
    public boolean unique;

    public EUIExporterBlightRow(AbstractBlight relic) {
        super(relic.blightID, "", relic.name);
        unique = relic.unique;
    }
}
