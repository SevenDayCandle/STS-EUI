package extendedui.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.ui.screens.CustomCardLibraryScreen;

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
            EUIExporter.ExportType format = EUIExporter.ExportType.valueOf(tokens[2]);

            switch (type) {
                case TYPE_CARD:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        EUIExporter.exportCard(color, format);
                    }
                    else {
                        EUIExporter.exportCard(CardLibrary.getAllCards(), format);
                    }
                    break;
                case TYPE_POTION:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        EUIExporter.exportPotion(color, format);
                    }
                    else {
                        EUIExporter.exportPotion(EUIExporter.getPotionInfos(), format);
                    }
                    break;
                case TYPE_RELIC:
                    if (tokens.length > 3) {
                        AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(tokens[3]);
                        EUIExporter.exportRelic(color, format);
                    }
                    else {
                        EUIExporter.exportRelic(EUIExporter.getRelicInfos(), format);
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
        ArrayList<String> options = EUIUtils.map(CustomCardLibraryScreen.getAllKeys(), Enum::toString);
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
