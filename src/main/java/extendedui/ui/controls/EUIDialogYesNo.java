package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class EUIDialogYesNo extends EUIDialog<Boolean>
{
    public EUIDialogYesNo(String headerText)
    {
        this(headerText, "");
    }

    public EUIDialogYesNo(String headerText, String descriptionText)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogYesNo(AdvancedHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
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
