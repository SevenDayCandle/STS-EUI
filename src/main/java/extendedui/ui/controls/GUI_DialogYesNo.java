package extendedui.ui.controls;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class GUI_DialogYesNo extends GUI_Dialog<Boolean>
{
    public GUI_DialogYesNo(String headerText)
    {
        this(headerText, "");
    }

    public GUI_DialogYesNo(String headerText, String descriptionText)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), headerText, descriptionText);
    }

    public GUI_DialogYesNo(AdvancedHitbox hb, String headerText, String descriptionText)
    {
        super(hb, headerText, descriptionText);
    }

    @Override
    public Boolean GetConfirmValue()
    {
        return true;
    }

    @Override
    public Boolean GetCancelValue()
    {
        return false;
    }
}
