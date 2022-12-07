package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUIDialogYesNo extends EUIDialog<Boolean>
{
    public EUIDialogYesNo(String headerText)
    {
        this(headerText, "");
    }

    public EUIDialogYesNo(String headerText, String descriptionText)
    {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogYesNo(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
    }

    @Override
    public Boolean getConfirmValue()
    {
        return true;
    }

    @Override
    public Boolean getCancelValue()
    {
        return false;
    }
}
