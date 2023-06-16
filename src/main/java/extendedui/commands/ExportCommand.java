package extendedui.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.ui.cardFilter.CustomCardLibraryScreen;

import java.util.ArrayList;

public class ExportCommand extends ConsoleCommand {
    private static final String TYPE_CARD = "card";
    private static final String TYPE_POTION = "potion";
    private static final String TYPE_RELIC = "relic";

    public ExportCommand() {
        this.requiresPlayer = false;
        this.minExtraTokens = 2;
        this.maxExtraTokens = 3;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        try {
            String type = tokens[1];
            String format = tokens[2];

            switch (type) {
                case TYPE_CARD:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportCardCsv(color);
                        }
                        else {
                            EUIExporter.exportCardJson(color);
                        }
                    }
                    else {
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportCardCsv(CardLibrary.getAllCards());
                        }
                        else {
                            EUIExporter.exportCardJson(CardLibrary.getAllCards());
                        }
                    }
                    break;
                case TYPE_POTION:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportPotionCsv(color);
                        }
                        else {
                            EUIExporter.exportPotionJson(color);
                        }
                    }
                    else {
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportPotionCsv(EUIExporter.getPotionInfos());
                        }
                        else {
                            EUIExporter.exportPotionJson(EUIExporter.getPotionInfos());
                        }
                    }
                    break;
                case TYPE_RELIC:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportRelicCsv(color);
                        }
                        else {
                            EUIExporter.exportRelicJson(color);
                        }
                    }
                    else {
                        if (EUIExporter.EXT_CSV.equals(format)) {
                            EUIExporter.exportRelicCsv(EUIExporter.getRelicInfos());
                        }
                        else {
                            EUIExporter.exportRelicJson(EUIExporter.getRelicInfos());
                        }
                    }
                    break;
            }

            DevConsole.log("Exported items");
        }
        catch (Exception e) {
            DevConsole.log("Could not export items.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = EUIUtils.map(CustomCardLibraryScreen.CardLists.keySet(), Enum::toString);
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
