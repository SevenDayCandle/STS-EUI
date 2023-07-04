package extendedui.exporter;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.utilities.RelicInfo;

public class EUIExporterRelicRow extends EUIExporterRow {
    public String tier;
    public String landingSFX;
    public String description;

    public EUIExporterRelicRow(AbstractRelic relic) {
        super(relic.relicId, String.valueOf(EUIGameUtils.getRelicColor(relic.relicId)), relic.name);
        tier = String.valueOf(relic.tier);
        landingSFX = String.valueOf(EUIGameUtils.getLandingSound(relic));
        description = sanitizeDescription(relic.description);
    }

    public EUIExporterRelicRow(RelicInfo relic) {
        super(relic.relic.relicId, String.valueOf(relic.relicColor), relic.relic.name);
        tier = String.valueOf(relic.relic.tier);
        landingSFX = String.valueOf(EUIGameUtils.getLandingSound(relic.relic));
        description = sanitizeDescription(relic.relic.description);
    }
}
