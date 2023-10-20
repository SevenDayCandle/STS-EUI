package extendedui.exporter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;

import java.lang.reflect.Field;

public class EUIExporterRow implements Comparable<EUIExporterRow> {
    public String ID;
    public String modID;
    public String color;
    public String name;

    public EUIExporterRow(String id, Object modID, AbstractCard.CardColor color, String name) {
        this(id, EUIGameUtils.getModID(modID), EUIGameUtils.getColorName(color), name);
    }

    public EUIExporterRow(String id, String modID, String color, String name) {
        ID = id;
        this.modID = modID;
        this.color = color;
        this.name = name;
    }

    public static String sanitizeDescription(String description) {
        return description
                .replaceAll("#[a-z]", "")
                .replaceAll("[\\[\\]{}]", "");
    }

    @Override
    public int compareTo(EUIExporterRow o) {
        return ID.compareTo(o.ID);
    }

    public String getCsvHeaderRow() {
        return EUIUtils.joinStringsMapLists(",", Field::getName, EUIExporter.BASE_FIELDS, this.getClass().getDeclaredFields()) + EUIExporter.NEWLINE;
    }

    public String[] getCsvHeaderRowAsCells() {
        return EUIUtils.arrayMapAll(String.class, Field::getName, EUIExporter.BASE_FIELDS, this.getClass().getDeclaredFields());
    }

    public Object[] toArray() {
        return EUIUtils.arrayMapAll(Object.class, field -> {
            try {
                return field.get(this);
            }
            catch (IllegalAccessException e) {
                return EUIUtils.EMPTY_STRING;
            }
        }, EUIExporter.BASE_FIELDS, this.getClass().getDeclaredFields());
    }

    @Override
    public String toString() {
        // Ensure that the base fields come first
        return EUIUtils.joinStringsMapLists(",", field -> {
            try {
                return String.valueOf(field.get(this));
            }
            catch (IllegalAccessException e) {
                return EUIUtils.EMPTY_STRING;
            }
        }, EUIExporter.BASE_FIELDS, this.getClass().getDeclaredFields()) + EUIExporter.NEWLINE;
    }
}
