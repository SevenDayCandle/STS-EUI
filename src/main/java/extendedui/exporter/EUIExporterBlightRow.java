package extendedui.exporter;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.utilities.RelicInfo;

public class EUIExporterBlightRow extends EUIExporterRow {
    public boolean unique;

    public EUIExporterBlightRow(AbstractBlight relic) {
        super(relic.blightID, "", relic.name);
        unique = relic.unique;
    }
}
