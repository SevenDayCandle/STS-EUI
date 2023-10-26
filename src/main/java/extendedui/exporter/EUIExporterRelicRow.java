package extendedui.exporter;

import extendedui.EUIGameUtils;
import extendedui.utilities.RelicInfo;

public class EUIExporterRelicRow extends EUIExporterRow {
    public String Tier;
    public String Landing_SFX;
    public String Description;

    public EUIExporterRelicRow(RelicInfo relic) {
        super(relic.relic.relicId, relic.relic, relic.relicColor, relic.relic.name);
        Tier = EUIGameUtils.textForRelicTier(relic.relic.tier);
        Landing_SFX = EUIGameUtils.textForRelicLandingSound(EUIGameUtils.getLandingSound(relic.relic));
        Description = sanitizeDescription(relic.relic.description);
    }
}
