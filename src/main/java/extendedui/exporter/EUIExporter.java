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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;

public class EUIExporter {
    public static final String EXT_CSV = "csv";
    public static final String EXT_JSON = "json";
    public static final String EXT_XLSX = "xlsx";
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String STATS_NAME = "Stats";
    public static final Exportable<AbstractBlight> blightExportable = new Exportable<>(EUIExporter::exportBlight);
    public static final Exportable<AbstractCard> cardExportable = new Exportable<>(EUIExporter::exportCard);
    private static Exportable<?> current = cardExportable;
    public static final Exportable<PotionInfo> potionExportable = new Exportable<>(EUIExporter::exportPotion);
    public static final Exportable<RelicInfo> relicExportable = new Exportable<>(EUIExporter::exportRelic);
    public static EUIButton exportButton;
    public static EUIContextMenu<ExportType> exportDropdown;

    public static void exportBlight(Iterable<? extends AbstractBlight> cards, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportBlight(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportBlight(Iterable<? extends AbstractBlight> cards, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, c -> getRowForBlight(c, type));
        type.exportRows(rows, path);
    }

    public static void exportCard(AbstractCard.CardColor c, ExportType type) {
        ArrayList<AbstractCard> group = CustomCardLibraryScreen.getCards(c);
        if (group != null) {
            exportCard(group, type);
        }
    }

    public static void exportCard(Iterable<? extends AbstractCard> cards, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportCard(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportCard(Iterable<? extends AbstractCard> cards, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, c -> getRowForCard(c, type));
        type.exportRows(rows, path);
    }

    public static void exportPotion(AbstractCard.CardColor c, ExportType type) {
        exportPotion(getPotionInfos(c), type);
    }

    public static void exportPotion(Iterable<? extends PotionInfo> potions, ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotion(potions, type, file.getAbsolutePath());
        }
    }

    public static void exportPotion(Iterable<? extends PotionInfo> potions, ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(potions, c -> getRowForPotion(c, type));
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
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(relics, c -> getRowForRelic(c, type));
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

    public static EUIExporterRow getRowForBlight(AbstractBlight c, ExportType format) {
        return new EUIExporterBlightRow(c);
    }

    public static EUIExporterRow getRowForCard(AbstractCard c, ExportType format) {
        return new EUIExporterCardRow(c);
    }

    public static EUIExporterRow getRowForPotion(PotionInfo c, ExportType format) {
        return new EUIExporterPotionRow(c);
    }

    public static EUIExporterRow getRowForRelic(RelicInfo c, ExportType format) {
        return new EUIExporterRelicRow(c);
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
        XLSX(EUIRM.strings.misc_exportXLSX, EXT_XLSX);

        public final String baseName;
        public final String type;

        ExportType(String name, String type) {
            this.baseName = name;
            this.type = type;
        }

        private static void exportImplCsv(List<? extends EUIExporterRow> items, String path) {
            if (!items.isEmpty()) {
                items.sort(EUIExporterRow::compareTo);
                try {
                    FileHandle handle = getExportFile(path);
                    handle.writeString(items.get(0).getCsvHeaderRow(), true, HttpParametersUtils.defaultEncoding);
                    for (EUIExporterRow row : items) {
                        handle.writeString(row.toString(), true, HttpParametersUtils.defaultEncoding);
                    }
                    EUIUtils.logInfo(EUIExporter.class, "Export items as CSV to " + path);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    EUIUtils.logError(EUIExporter.class, "Failed to export items as CSV.");
                }
            }
        }

        private static void exportImplJson(List<? extends EUIExporterRow> items, String path) {
            if (!items.isEmpty()) {
                items.sort(EUIExporterRow::compareTo);
                try {
                    FileHandle handle = getExportFile(path);
                    handle.writeString(EUIUtils.serialize(items), true, HttpParametersUtils.defaultEncoding);
                    EUIUtils.logInfo(EUIExporter.class, "Export items as JSON to " + path);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    EUIUtils.logError(EUIExporter.class, "Failed to export items as JSON.");
                }
            }
        }

        private static void exportImplXlsx(List<? extends EUIExporterRow> items, String path) {
            if (!items.isEmpty()) {
                items.sort(EUIExporterRow::compareTo);
                try {
                    FileHandle handle = getExportFile(path);
                    XSSFWorkbook workbook = new XSSFWorkbook(handle.file());

                    // Main sheet
                    final String mainSheetName = items.getClass().getComponentType().getName();
                    XSSFSheet sheet1 = workbook.createSheet(mainSheetName);
                    XSSFRow headerRow = sheet1.createRow(0);
                    String[] headers = items.get(0).getCsvHeaderRowAsCells();
                    for (int i = 0; i < headers.length; i++) {
                        XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                    }
                    String[][] results = new String[items.size()][];
                    for (int i = 0; i < items.size(); i++) {
                        XSSFRow row = sheet1.createRow(i + 1);
                        String[] values = items.get(0).toStringList();
                        for (int j = 0; j < values.length; j++) {
                            XSSFCell cell = row.createCell(j);
                            cell.setCellValue(values[j]);
                        }
                        results[i] = values;
                    }

                    // Stats sheet
                    XSSFSheet sheet2 = workbook.createSheet(STATS_NAME);
                    Integer[] sortIndices = items.get(0).getStatIndices();
                    int rowInd = 0;
                    for (Integer stat : sortIndices) {
                        XSSFRow categoryRow = sheet2.createRow(rowInd);
                        String catName = headers[stat];
                        XSSFCell cell = categoryRow.createCell(rowInd);
                        cell.setCellValue(catName);
                        rowInd++;

                        HashSet<String> uniques = new HashSet<>();
                        for (String[] resRow : results) {
                            uniques.add(resRow[stat]);
                        }
                        for (String u : uniques) {
                            XSSFRow row = sheet2.createRow(rowInd);
                            XSSFCell uCell = row.createCell(0);
                            uCell.setCellValue(u);

                            char cellChar = (char) ('A' + stat);
                            XSSFCell uCell2 = row.createCell(1);
                            uCell2.setCellFormula("SUMIF(" + cellChar + ":" + cellChar + "," + u + ")");
                            rowInd++;
                        }

                        rowInd++;
                    }

                    EUIUtils.logInfo(EUIExporter.class, "Export items as XLSX to " + path);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    EUIUtils.logError(EUIExporter.class, "Failed to export items as XLSX.");
                }
            }
        }

        public void exportRows(List<? extends EUIExporterRow> items, String path) {
            switch (this) {
                case CSV:
                    exportImplCsv(items, path);
                    return;
                case JSON:
                    exportImplJson(items, path);
                    return;
                case XLSX:
                    exportImplXlsx(items, path);
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
