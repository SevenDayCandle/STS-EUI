package extendedui.ui;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

// Variant of AbstractScreen to be used for screens that can be opened in dungeons
public abstract class AbstractDungeonScreen extends AbstractScreen
{
    protected void open()
    {
        open(true, true);
    }

    protected void open(boolean hideTopBar, boolean hideRelics)
    {
        Settings.hideTopBar = hideTopBar;
        Settings.hideRelics = hideRelics;
        super.open();
    }

    public void onEscape()
    {
        super.onEscape();
        AbstractDungeon.closeCurrentScreen();
    }
}
