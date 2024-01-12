package extendedui.exporter;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EUIExporter {
    private static DocumentBuilderFactory DBFACTORY;
    private static TransformerFactory TRFACTORY;
    private static final String ROOT_NAME = "Items";
    static final Field[] BASE_FIELDS = EUIExporterRow.class.getDeclaredFields();
    public static final String EXT_CSV = "csv";
    public static final String EXT_JSON = "json";
    public static final String EXT_XML = "xml";
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final Exportable<AbstractBlight> blightExportable = new Exportable<>(EUIExporter::exportBlight);
    public static final Exportable<AbstractCard> cardExportable = new Exportable<>(EUIExporter::exportCard);
    public static final Exportable<PotionInfo> potionExportable = new Exportable<>(EUIExporter::exportPotion);
    public static final Exportable<RelicInfo> relicExportable = new Exportable<>(EUIExporter::exportRelic);
    private static Exportable<?> current = cardExportable;
    public static EUIButton exportButton;
    public static EUIContextMenu<ExportType> exportDropdown;

    public static void exportBlight(Iterable<? extends AbstractBlight> cards, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportBlight(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportBlight(Iterable<? extends AbstractBlight> cards, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = getRowsForBlight(cards, type);
        type.exportRows(rows, path);
    }

    public static void exportCard(AbstractCard.CardColor c, ExportType type) {
        ArrayList<AbstractCard> group = CustomCardLibraryScreen.getCards(c);
        if (group != null) {
            exportCard(group, type);
        }
    }

    public static void exportCard(Iterable<? extends AbstractCard> cards, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportCard(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportCard(Iterable<? extends AbstractCard> cards, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = getRowsForCard(cards, type);
        type.exportRows(rows, path);
    }

    public static void exportPotion(AbstractCard.CardColor c, ExportType type) {
        exportPotion(getPotionInfos(c), type);
    }

    public static void exportPotion(Iterable<? extends PotionInfo> potions, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotion(potions, type, file.getAbsolutePath());
        }
    }

    public static void exportPotion(Iterable<? extends PotionInfo> potions, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = getRowsForPotion(potions, type);
        type.exportRows(rows, path);
    }

    public static void exportRelic(AbstractCard.CardColor c, ExportType type) {
        exportRelic(getRelicInfos(c), type);
    }

    public static void exportRelic(Iterable<? extends RelicInfo> relics, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportRelic(relics, type, file.getAbsolutePath());
        }
    }

    public static void exportRelic(Iterable<? extends RelicInfo> relics, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = getRowsForRelic(relics, type);
        type.exportRows(rows, path);
    }

    private static FileHandle getExportFile(String path) {
        FileHandle handle = Gdx.files.absolute(path);
        if (handle.exists()) {
            handle.delete();
        }
        return handle;
    }

    public static ArrayList<PotionInfo> getPotionInfos() {
        ArrayList<PotionInfo> potions = new ArrayList<>();
        for (String potionID : PotionHelper.getPotions(null, true)) {
            AbstractPotion original = PotionHelper.getPotion(potionID);
            PotionInfo info = new PotionInfo(original);
            potions.add(info);
        }
        return potions;
    }

    public static ArrayList<PotionInfo> getPotionInfos(AbstractCard.CardColor color) {
        ArrayList<PotionInfo> potions = new ArrayList<>();
        for (String potionID : PotionHelper.getPotions(null, true)) {
            AbstractPotion original = PotionHelper.getPotion(potionID);
            PotionInfo info = new PotionInfo(original);
            if (info.potionColor == color) {
                potions.add(info);
            }
        }
        return potions;
    }

    public static ArrayList<RelicInfo> getRelicInfos() {
        ArrayList<RelicInfo> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getInGameRelicIDs()) {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet) {
                original = BaseMod.getCustomRelic(relicID);
            }
            RelicInfo info = new RelicInfo(original);
            newRelics.add(info);
        }
        return newRelics;
    }

    public static ArrayList<RelicInfo> getRelicInfos(AbstractCard.CardColor color) {
        ArrayList<RelicInfo> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getInGameRelicIDs()) {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet) {
                original = BaseMod.getCustomRelic(relicID);
            }
            RelicInfo info = new RelicInfo(original);
            if (info.relicColor == color) {
                newRelics.add(info);
            }
        }
        return newRelics;
    }

    public static ArrayList<EUIExporterRow> getRowsForBlight(Iterable<? extends AbstractBlight> items, ExportType format) {
        return EUIUtils.map(items, EUIExporterBlightRow::new);
    }

    public static ArrayList<EUIExporterRow> getRowsForCard(Iterable<? extends AbstractCard> items, ExportType format) {
        return EUIUtils.map(items, i -> new EUIExporterCardRow(i, format));
    }

    public static ArrayList<EUIExporterRow> getRowsForPotion(Iterable<? extends PotionInfo> items, ExportType format) {
        return EUIUtils.map(items, EUIExporterPotionRow::new);
    }

    public static ArrayList<EUIExporterRow> getRowsForRelic(Iterable<? extends RelicInfo> items, ExportType format) {
        return EUIUtils.map(items, EUIExporterRelicRow::new);
    }

    public static void initialize() {
        EUITooltip tip = new EUITooltip(EUIRM.strings.misc_export, EUIRM.strings.misc_exportDesc);
        exportButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setLabel(EUIFontHelper.buttonFont, 0.7f, EUIRM.strings.misc_export)
                .setTooltip(tip)
                .setColor(Color.GRAY);
        exportDropdown = (EUIContextMenu<ExportType>) new EUIContextMenu<ExportType>(new EUIHitbox(0, 0, 0, 0), c -> c.baseName)
                .setOnChange(options -> {
                    for (ExportType o : options) {
                        o.onSelect();
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setItems(ExportType.values())
                .setCanAutosizeButton(true);
    }

    public static void positionExport() {
        exportDropdown.setPosition(InputHelper.mX - exportDropdown.hb.width, InputHelper.mY - exportDropdown.hb.height * 3);
        exportDropdown.openOrCloseMenu();
    }

    private static void writeConfig(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && parentFile.isDirectory()) {
            EUIConfiguration.lastExportPath.set(parentFile.getAbsolutePath());
        }
    }

    public enum ExportType {
        CSV(EUIRM.strings.misc_exportCSV, EXT_CSV),
        JSON(EUIRM.strings.misc_exportJSON, EXT_JSON),
        XML(EUIRM.strings.misc_exportXML, EXT_XML);

        public final String baseName;
        public final String type;

        ExportType(String name, String type) {
            this.baseName = name;
            this.type = type;
        }

        private static Node createDomNode(Document doc, String[] cellNames, EUIExporterRow item) {
            Element row = doc.createElement(item.getClass().getSimpleName());
            row.setAttribute("id", item.ID.replace(':', '_')); // Avoid using colons in XML IDs
            Object[] properties = item.toArray();
            for (int i = 0; i < Math.min(cellNames.length, properties.length); i++) {
                Element node = doc.createElement(cellNames[i]);
                Object property = properties[i];
                if (property instanceof Iterable) {
                    for (Object subprop : (Iterable<?>) property) {
                        node.appendChild(doc.createTextNode(String.valueOf(subprop)));
                    }
                }
                else if (property != null && property.getClass().isArray()) {
                    int size = Array.getLength(property);
                    for (int j = 0; j < size; j++) {
                        node.appendChild(doc.createTextNode(String.valueOf(Array.get(property, j))));
                    }
                }
                else {
                    node.appendChild(doc.createTextNode(String.valueOf(properties[i])));
                }
                row.appendChild(node);
            }
            return row;
        }

        private static void exportImplCsv(List<? extends EUIExporterRow> items, String path) {
            try {
                FileHandle handle = getExportFile(path);
                handle.writeString(items.get(0).getCsvHeaderRow(), true, HttpParametersUtils.defaultEncoding);
                for (EUIExporterRow row : items) {
                    handle.writeString(row.toString(), true, HttpParametersUtils.defaultEncoding);
                }
                EUIUtils.logInfo(EUIExporter.class, "Exported items as CSV to " + path);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(EUIExporter.class, "Failed to export items as CSV.");
            }
        }

        private static void exportImplJson(List<? extends EUIExporterRow> items, String path) {
            try {
                FileHandle handle = getExportFile(path);
                handle.writeString(EUIUtils.serialize(items), true, HttpParametersUtils.defaultEncoding);
                EUIUtils.logInfo(EUIExporter.class, "Exported items as JSON to " + path);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(EUIExporter.class, "Failed to export items as JSON.");
            }
        }

        private static void exportImplXml(List<? extends EUIExporterRow> items, String path) {
            try {
                if (DBFACTORY == null) {
                    DBFACTORY = DocumentBuilderFactory.newInstance();
                }
                if (TRFACTORY == null) {
                    TRFACTORY = TransformerFactory.newInstance();
                }

                DocumentBuilder dBuilder = DBFACTORY.newDocumentBuilder();
                Document doc = dBuilder.newDocument();
                Element rootElement = doc.createElement(ROOT_NAME);
                doc.appendChild(rootElement);

                String[] cellNames = items.get(0).getXmlHeaderRowCells();
                for (EUIExporterRow row : items) {
                    rootElement.appendChild(createDomNode(doc, cellNames, row));
                }

                Transformer transformer = TRFACTORY.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult file = new StreamResult(new File(path));
                transformer.transform(source, file);

                EUIUtils.logInfo(EUIExporter.class, "Exported items as XML to " + path);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(EUIExporter.class, "Failed to export items as XML.");
            }
        }

        public void exportRows(List<? extends EUIExporterRow> items, String path) {
            if (!items.isEmpty()) {
                items.sort(EUIExporterRow::compareTo);
                // Ensure that the path ends with the proper extension
                if (!path.endsWith("." + this.type)) {
                    path = path + "." + this.type;
                }
                switch (this) {
                    case CSV:
                        exportImplCsv(items, path);
                        return;
                    case JSON:
                        exportImplJson(items, path);
                        return;
                    case XML:
                        exportImplXml(items, path);
                }
            }
        }

        public void onSelect() {
            current.export(this);
        }
    }

    public static class Exportable<T> {
        public final ActionT2<Iterable<? extends T>, ExportType> exportFunc;
        public Iterable<? extends T> items;

        public Exportable(ActionT2<Iterable<? extends T>, ExportType> exportFunc) {
            this.exportFunc = exportFunc;
        }

        public void clear() {
            items = null;
        }

        public void export(ExportType type) {
            exportFunc.invoke(items, type);
        }

        public void open(Iterable<? extends T> items) {
            current.clear();
            this.items = items;
            current = this;
        }

        public void openAndPosition(Iterable<? extends T> items) {
            open(items);
            positionExport();
        }
    }
}
