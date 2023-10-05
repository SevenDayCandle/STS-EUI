package extendedui.exporter;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUIGameUtils;

public class EUIExporterBlightRow extends EUIExporterRow {
    public boolean unique;
    public String description;

    public EUIExporterBlightRow(AbstractBlight blight) {
        super(blight.blightID, EUIGameUtils.getModID(blight), "", blight.name);
        unique = blight.unique;
        description = sanitizeDescription(blight.description);
    }
}
