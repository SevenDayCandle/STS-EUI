package extendedui.ui.cardFilter;

import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.GUI_CardGrid;

// TODO Screw CardLibraryScreen, make your own
public class CustomCardLibraryScreen extends AbstractScreen
{
    // TODO Use CardKeywordFilters.GetActingCardColor
    public CustomCardLibSortHeader sortHeader;
    public GUI_CardGrid cardGrid;
    public MenuCancelButton button;

    public CustomCardLibraryScreen() {
        cardGrid = new GUI_CardGrid();


    }



    public void Open() {

    }
}
