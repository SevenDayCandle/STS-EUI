package extendedui.exporter;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.utilities.RelicInfo;

public class EUIExporterRelicRow extends EUIExporterRow {
    public String tier;
    public String landingSFX;
    public String description;

    public EUIExporterRelicRow(RelicInfo relic) {
        super(relic.relic.relicId, relic.relic, relic.relicColor, relic.relic.name);
        tier = EUIGameUtils.textForRelicTier(relic.relic.tier);
        landingSFX = EUIGameUtils.textForRelicLandingSound(EUIGameUtils.getLandingSound(relic.relic));
        description = sanitizeDescription(relic.relic.description);
    }
}
