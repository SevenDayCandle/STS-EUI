package extendedui.exporter;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.utilities.RelicGroup;

public class EUIExporterRelicRow extends EUIExporterRow {
    public String tier;
    public String landingSFX;
    public String description;

    public EUIExporterRelicRow(AbstractRelic relic) {
        super(relic.relicId, String.valueOf(EUIGameUtils.getRelicColor(relic.relicId)), relic.name);
        tier = String.valueOf(relic.tier);
        landingSFX = String.valueOf(getLandingSound(relic));
        description = relic.description;
    }

    public EUIExporterRelicRow(RelicGroup.RelicInfo relic) {
        super(relic.relic.relicId, String.valueOf(relic.relicColor), relic.relic.name);
        tier = String.valueOf(relic.relic.tier);
        landingSFX = String.valueOf(getLandingSound(relic.relic));
        description = relic.relic.description;
    }

    protected static AbstractRelic.LandingSound getLandingSound(AbstractRelic relic) {
        return ReflectionHacks.getPrivate(relic, AbstractRelic.class, "landingSFX");
    }
}
