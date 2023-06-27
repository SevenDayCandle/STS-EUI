package extendedui.exporter;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
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
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EUIExporter {
    public static final String EXT_CSV = "csv";
    public static final String EXT_JSON = "json";
    public static final String NEWLINE = System.getProperty("line.separator");
    private static final Exportable<AbstractBlight> blightExportable = new Exportable<>(EUIExporter::exportBlightCsv, EUIExporter::exportBlightJson);
    private static final Exportable<AbstractCard> cardExportable = new Exportable<>(EUIExporter::exportCardCsv, EUIExporter::exportCardJson);
    private static final Exportable<PotionInfo> potionExportable = new Exportable<>(EUIExporter::exportPotionCsv, EUIExporter::exportPotionJson);
    private static final Exportable<RelicInfo> relicExportable = new Exportable<>(EUIExporter::exportRelicCsv, EUIExporter::exportRelicJson);
    private static Exportable<?> current = cardExportable;
    public static EUIButton exportBlightButton;
    public static EUIButton exportCardButton;
    public static EUIButton exportPotionButton;
    public static EUIButton exportRelicButton;
    public static EUIContextMenu<EUIExporter.ContextOption> exportDropdown;

    public static void exportBlightCsv(AbstractBlight c) {
        exportBlightCsv(Collections.singleton(c));
    }

    public static void exportBlightCsv(Iterable<? extends AbstractBlight> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportBlightCsv(cards, file.getAbsolutePath());
        }
    }

    private static void exportBlightCsv(Iterable<? extends AbstractBlight> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporter::getRowForBlight);
        exportImplCsv(rows, path);
    }

    public static void exportBlightJson(AbstractBlight c) {
        exportBlightJson(Collections.singleton(c));
    }

    public static void exportBlightJson(Iterable<? extends AbstractBlight> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportBlightJson(cards, file.getAbsolutePath());
        }
    }

    public static void exportBlightJson(Iterable<? extends AbstractBlight> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporter::getRowForBlight);
        exportImplJson(rows, path);
    }

    public static void exportCardCsv(AbstractCard c) {
        exportCardCsv(Collections.singleton(c));
    }

    public static void exportCardCsv(AbstractCard.CardColor c) {
        CardGroup group = CustomCardLibraryScreen.CardLists.get(c);
        if (group != null) {
            exportCardCsv(group.group);
        }
    }

    public static void exportCardCsv(Iterable<? extends AbstractCard> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportCardCsv(cards, file.getAbsolutePath());
        }
    }

    private static void exportCardCsv(Iterable<? extends AbstractCard> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporter::getRowForCard);
        exportImplCsv(rows, path);
    }

    public static void exportCardJson(AbstractCard c) {
        exportCardJson(Collections.singleton(c));
    }

    public static void exportCardJson(AbstractCard.CardColor c) {
        CardGroup group = CustomCardLibraryScreen.CardLists.get(c);
        if (group != null) {
            exportCardJson(group.group);
        }
    }

    public static void exportCardJson(Iterable<? extends AbstractCard> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportCardJson(cards, file.getAbsolutePath());
        }
    }

    public static void exportCardJson(Iterable<? extends AbstractCard> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporter::getRowForCard);
        exportImplJson(rows, path);
    }

    public static void exportImplCsv(List<? extends EUIExporterRow> items, String path) {
        if (!items.isEmpty()) {
            items.sort(EUIExporterRow::compareTo);
            try {
                FileHandle handle = getExportFile(path);
                handle.writeString(items.get(0).getCsvHeaderRow(), true);
                for (EUIExporterRow row : items) {
                    handle.writeString(row.toString(), true);
                }
                EUIUtils.logInfo(EUIExporter.class, "Export items as CSV to " + path);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(EUIExporter.class, "Failed to export items as CSV.");
            }
        }
    }

    public static void exportImplJson(List<? extends EUIExporterRow> items, String path) {
        if (!items.isEmpty()) {
            items.sort(EUIExporterRow::compareTo);
            try {
                FileHandle handle = getExportFile(path);
                handle.writeString(EUIUtils.serialize(items), true);
                EUIUtils.logInfo(EUIExporter.class, "Export items as JSON to " + path);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(EUIExporter.class, "Failed to export items as JSON.");
            }
        }
    }

    public static void exportPotionCsv(PotionInfo c) {
        exportPotionCsv(Collections.singleton(c));
    }

    public static void exportPotionCsv(AbstractCard.CardColor c) {
        exportPotionCsv(getPotionInfos(c));
    }

    public static void exportPotionCsv(Iterable<? extends PotionInfo> potions) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotionCsv(potions, file.getAbsolutePath());
        }
    }

    public static void exportPotionCsv(Iterable<? extends PotionInfo> potions, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(potions, EUIExporter::getRowForPotion);
        exportImplCsv(rows, path);
    }

    public static void exportPotionJson(PotionInfo c) {
        exportPotionJson(Collections.singleton(c));
    }

    public static void exportPotionJson(AbstractCard.CardColor c) {
        exportPotionJson(getPotionInfos(c));
    }

    public static void exportPotionJson(Iterable<? extends PotionInfo> potions) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotionJson(potions, file.getAbsolutePath());
        }
    }

    public static void exportPotionJson(Iterable<? extends PotionInfo> potions, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(potions, EUIExporter::getRowForPotion);
        exportImplJson(rows, path);
    }

    public static void exportRelicCsv(RelicInfo c) {
        exportRelicCsv(Collections.singleton(c));
    }

    public static void exportRelicCsv(AbstractCard.CardColor c) {
        exportRelicCsv(getRelicInfos(c));
    }

    public static void exportRelicCsv(Iterable<? extends RelicInfo> relics) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportRelicCsv(relics, file.getAbsolutePath());
        }
    }

    public static void exportRelicCsv(Iterable<? extends RelicInfo> relics, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(relics, EUIExporter::getRowForRelic);
        exportImplCsv(rows, path);
    }

    public static void exportRelicJson(RelicInfo c) {
        exportRelicJson(Collections.singleton(c));
    }

    public static void exportRelicJson(AbstractCard.CardColor c) {
        exportRelicJson(getRelicInfos(c));
    }

    public static void exportRelicJson(Iterable<? extends RelicInfo> relics) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportRelicJson(relics, file.getAbsolutePath());
        }
    }

    public static void exportRelicJson(Iterable<? extends RelicInfo> relics, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(relics, EUIExporter::getRowForRelic);
        exportImplJson(rows, path);
    }

    public static EUIExporterRow getRowForBlight(AbstractBlight c) {
        return new EUIExporterBlightRow(c);
    }

    public static EUIExporterRow getRowForCard(AbstractCard c) {
        return new EUIExporterCardRow(c);
    }

    public static EUIExporterRow getRowForPotion(PotionInfo c) {
        return new EUIExporterPotionRow(c);
    }

    public static EUIExporterRow getRowForRelic(RelicInfo c) {
        return new EUIExporterRelicRow(c);
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

    private static FileHandle getExportFile(String path) {
        FileHandle handle = Gdx.files.absolute(path);
        if (handle.exists()) {
            handle.delete();
        }
        return handle;
    }

    private static void writeConfig(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && parentFile.isDirectory()) {
            EUIConfiguration.lastExportPath.set(parentFile.getAbsolutePath());
        }
    }

    public static void initialize() {
        EUITooltip tip = new EUITooltip(EUIRM.strings.misc_export, EUIRM.strings.misc_exportDesc);
        exportBlightButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.misc_export)
                .setTooltip(tip)
                .setColor(Color.GRAY);
        exportCardButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.misc_export)
                .setTooltip(tip)
                .setColor(Color.GRAY);
        exportPotionButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.misc_export)
                .setTooltip(tip)
                .setColor(Color.GRAY);
        exportRelicButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.12f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.misc_export)
                .setTooltip(tip)
                .setColor(Color.GRAY);
        exportDropdown = (EUIContextMenu<EUIExporter.ContextOption>) new EUIContextMenu<EUIExporter.ContextOption>(new EUIHitbox(0, 0, 0, 0), c -> c.baseName)
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect();
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setItems(ContextOption.values())
                .setCanAutosizeButton(true);
    }

    public static void positionExport() {
        exportDropdown.setPosition(InputHelper.mX - exportDropdown.hb.width, InputHelper.mY - exportDropdown.hb.height * 3);
        exportDropdown.openOrCloseMenu();
    }
    public static void openForBlights(Iterable<? extends AbstractBlight> items) {
        blightExportable.open(items);
        positionExport();
    }

    public static void openForCards(Iterable<? extends AbstractCard> items) {
        cardExportable.open(items);
        positionExport();
    }

    public static void openForPotions(Iterable<? extends PotionInfo> items) {
        potionExportable.open(items);
        positionExport();
    }

    public static void openForRelics(Iterable<? extends RelicInfo> items) {
        relicExportable.open(items);
        positionExport();
    }

    public enum ContextOption {
        CSV(EUIRM.strings.misc_exportCSV),
        JSON(EUIRM.strings.misc_exportJSON);

        public final String baseName;

        ContextOption(String name) {
            this.baseName = name;
        }

        public void onSelect() {
            switch (this) {
                case CSV:
                    current.exportCsv();
                    break;
                case JSON:
                    current.exportJson();
                    break;
            }
        }
    }

    public static class Exportable<T> {
        public Iterable<? extends T> items;
        public final ActionT1<Iterable<? extends T>> exportCsv;
        public final ActionT1<Iterable<? extends T>> exportJson;

        public Exportable(ActionT1<Iterable<? extends T>> exportCsv, ActionT1<Iterable<? extends T>> exportJson) {
            this.exportCsv = exportCsv;
            this.exportJson = exportJson;
        }

        public void open(Iterable<? extends T> items) {
            current.clear();
            this.items = items;
            current = this;
        }

        public void clear() {items = null;}

        public void exportCsv() {
            exportCsv.invoke(items);
        }

        public void exportJson() {
            exportJson.invoke(items);
        }
    }
}
