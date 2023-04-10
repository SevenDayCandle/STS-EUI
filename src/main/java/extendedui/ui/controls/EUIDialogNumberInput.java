package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.hitboxes.EUIHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUIDialogNumberInput extends EUIDialog<Integer>
{
    protected EUITextBoxNumericalInput input;

    public EUIDialogNumberInput(String headerText)
    {
        this(headerText, "");
    }

    public EUIDialogNumberInput(String headerText, String descriptionText)
    {
        this(headerText, descriptionText, scale(300), scale(390));
    }

    public EUIDialogNumberInput(String headerText, String descriptionText, float w, float h)
    {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogNumberInput(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.input = new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 4, hb.width / 2, scale(48)));
    }

    public EUIDialogNumberInput setValue(int value) {
        this.input.setValue(value);
        return this;
    }

    @Override
    public Integer getConfirmValue()
    {
        return input.getCachedValue();
    }

    @Override
    public Integer getCancelValue()
    {
        return null;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.input.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        this.input.tryRender(sb);
    }
}
