package extendedui.exporter;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
import extendedui.ui.cardFilter.CustomCardLibraryScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionGroup;
import extendedui.utilities.RelicGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EUIExporter {
    public static final String EXT_CSV = "csv";
    public static final String EXT_JSON = "json";
    public static final String NEWLINE = System.getProperty("line.separator");
    private static Iterable<? extends AbstractCard> currentCards;
    private static Iterable<? extends PotionGroup.PotionInfo> currentPotions;
    private static Iterable<? extends RelicGroup.RelicInfo> currentRelics;
    public static EUIButton exportCardButton;
    public static EUIButton exportPotionButton;
    public static EUIButton exportRelicButton;
    public static EUIContextMenu<EUIExporter.ContextOption> exportDropdown;

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

    private static void exportCardJson(Iterable<? extends AbstractCard> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporter::getRowForCard);
        exportImplJson(rows, path);
    }

    private static void exportImplCsv(List<? extends EUIExporterRow> items, String path) {
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

    private static void exportImplJson(List<? extends EUIExporterRow> items, String path) {
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

    public static void exportPotionCsv(PotionGroup.PotionInfo c) {
        exportPotionCsv(Collections.singleton(c));
    }

    public static void exportPotionCsv(AbstractCard.CardColor c) {
        exportPotionCsv(getPotionInfos(c));
    }

    public static void exportPotionCsv(Iterable<? extends PotionGroup.PotionInfo> potions) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotionCsv(potions, file.getAbsolutePath());
        }
    }

    private static void exportPotionCsv(Iterable<? extends PotionGroup.PotionInfo> potions, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(potions, EUIExporter::getRowForPotion);
        exportImplCsv(rows, path);
    }

    public static void exportPotionJson(PotionGroup.PotionInfo c) {
        exportPotionJson(Collections.singleton(c));
    }

    public static void exportPotionJson(AbstractCard.CardColor c) {
        exportPotionJson(getPotionInfos(c));
    }

    public static void exportPotionJson(Iterable<? extends PotionGroup.PotionInfo> potions) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPotionJson(potions, file.getAbsolutePath());
        }
    }

    private static void exportPotionJson(Iterable<? extends PotionGroup.PotionInfo> potions, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(potions, EUIExporter::getRowForPotion);
        exportImplJson(rows, path);
    }

    public static void exportRelicCsv(RelicGroup.RelicInfo c) {
        exportRelicCsv(Collections.singleton(c));
    }

    public static void exportRelicCsv(AbstractCard.CardColor c) {
        exportRelicCsv(getRelicInfos(c));
    }

    public static void exportRelicCsv(Iterable<? extends RelicGroup.RelicInfo> relics) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportRelicCsv(relics, file.getAbsolutePath());
        }
    }

    private static void exportRelicCsv(Iterable<? extends RelicGroup.RelicInfo> relics, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(relics, EUIExporter::getRowForRelic);
        exportImplCsv(rows, path);
    }

    public static void exportRelicJson(RelicGroup.RelicInfo c) {
        exportRelicJson(Collections.singleton(c));
    }

    public static void exportRelicJson(AbstractCard.CardColor c) {
        exportRelicJson(getRelicInfos(c));
    }

    public static void exportRelicJson(Iterable<? extends RelicGroup.RelicInfo> relics) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportRelicJson(relics, file.getAbsolutePath());
        }
    }

    private static void exportRelicJson(Iterable<? extends RelicGroup.RelicInfo> relics, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(relics, EUIExporter::getRowForRelic);
        exportImplJson(rows, path);
    }

    private static EUIExporterRow getRowForCard(AbstractCard c) {
        return new EUIExporterCardRow(c);
    }

    private static EUIExporterRow getRowForPotion(PotionGroup.PotionInfo c) {
        return new EUIExporterPotionRow(c);
    }

    private static EUIExporterRow getRowForRelic(RelicGroup.RelicInfo c) {
        return new EUIExporterRelicRow(c);
    }

    public static ArrayList<PotionGroup.PotionInfo> getPotionInfos() {
        ArrayList<PotionGroup.PotionInfo> potions = new ArrayList<>();
        for (String potionID : PotionHelper.getPotions(null, true)) {
            AbstractPotion original = PotionHelper.getPotion(potionID);
            PotionGroup.PotionInfo info = new PotionGroup.PotionInfo(original);
            potions.add(info);
        }
        return potions;
    }

    public static ArrayList<PotionGroup.PotionInfo> getPotionInfos(AbstractCard.CardColor color) {
        ArrayList<PotionGroup.PotionInfo> potions = new ArrayList<>();
        for (String potionID : PotionHelper.getPotions(null, true)) {
            AbstractPotion original = PotionHelper.getPotion(potionID);
            PotionGroup.PotionInfo info = new PotionGroup.PotionInfo(original);
            if (info.potionColor == color) {
                potions.add(info);
            }
        }
        return potions;
    }

    public static ArrayList<RelicGroup.RelicInfo> getRelicInfos() {
        ArrayList<RelicGroup.RelicInfo> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getAllRelicIDs()) {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet) {
                original = BaseMod.getCustomRelic(relicID);
            }
            RelicGroup.RelicInfo info = new RelicGroup.RelicInfo(original);
            newRelics.add(info);
        }
        return newRelics;
    }

    public static ArrayList<RelicGroup.RelicInfo> getRelicInfos(AbstractCard.CardColor color) {
        ArrayList<RelicGroup.RelicInfo> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getAllRelicIDs()) {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet) {
                original = BaseMod.getCustomRelic(relicID);
            }
            RelicGroup.RelicInfo info = new RelicGroup.RelicInfo(original);
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

    public static void openForCards(Iterable<? extends AbstractCard> items) {
        currentCards = items;
        currentRelics = null;
        currentPotions = null;
        exportDropdown.setPosition(InputHelper.mX - exportDropdown.hb.width, InputHelper.mY - exportDropdown.hb.height * 3);
        exportDropdown.openOrCloseMenu();
    }

    public static void openForPotions(Iterable<? extends PotionGroup.PotionInfo> items) {
        currentPotions = items;
        currentRelics = null;
        currentCards = null;
        exportDropdown.setPosition(InputHelper.mX - exportDropdown.hb.width, InputHelper.mY - exportDropdown.hb.height * 3);
        exportDropdown.openOrCloseMenu();
    }

    public static void openForRelics(Iterable<? extends RelicGroup.RelicInfo> items) {
        currentRelics = items;
        currentCards = null;
        currentPotions = null;
        exportDropdown.setPosition(InputHelper.mX - exportDropdown.hb.width, InputHelper.mY - exportDropdown.hb.height * 3);
        exportDropdown.openOrCloseMenu();
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
                    if (currentCards != null) {
                        exportCardCsv(currentCards);
                    }
                    else if (currentPotions != null) {
                        exportPotionCsv(currentPotions);
                    }
                    else if (currentRelics != null) {
                        exportRelicCsv(currentRelics);
                    }
                    break;
                case JSON:
                    if (currentCards != null) {
                        exportCardJson(currentCards);
                    }
                    else if (currentPotions != null) {
                        exportPotionJson(currentPotions);
                    }
                    else if (currentRelics != null) {
                        exportRelicJson(currentRelics);
                    }
                    break;
            }
        }
    }
}
