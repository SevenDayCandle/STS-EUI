package extendedui.ui;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.EUI;

// Variant of AbstractScreen to be used for screens that can be opened in menus
public abstract class AbstractMenuScreen extends AbstractScreen
{
    public void onEscape()
    {
        super.onEscape();
        CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        if (EUI.currentScreen == this)
        {
            dispose();
        }
    }
}
