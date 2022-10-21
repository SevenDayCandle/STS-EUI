package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import eatyourbeets.interfaces.delegates.FuncT1;
import eatyourbeets.interfaces.delegates.FuncT2;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUIDialogDropdown<T> extends EUIDialog<ArrayList<T>>
{
    protected EUIDropdown<T> dropdown;

    public EUIDialogDropdown(String headerText)
    {
        this(headerText, "");
    }

    public EUIDialogDropdown(String headerText, String descriptionText)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogDropdown(AdvancedHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.dropdown = new EUIDropdown<T>(new AdvancedHitbox(hb.x + hb.width / 4, hb.y + hb.height / 4, hb.width / 2, Scale(48)))
                .SetCanAutosize(false, true);
    }

    public EUIDialogDropdown<T> SetOptions(boolean isMultiSelect, boolean canAutosize) {
        this.dropdown.isMultiSelect = isMultiSelect;
        this.dropdown.canAutosizeButton = canAutosize;
        if (this.dropdown.canAutosizeButton) {
            this.dropdown.Autosize();
            this.hb.width = this.dropdown.hb.width * 2;
            this.hb.x = (Settings.WIDTH - this.hb.width) / 2;
            this.dropdown.SetPosition(hb.x + hb.width / 4, hb.y + hb.height / 4);
        }
        return this;
    }

    @SafeVarargs
    public final EUIDialogDropdown<T> SetItems(T... items) {
        this.dropdown.SetItems(items);
        return this;
    }

    public EUIDialogDropdown<T> SetItems(List<T> items) {
        this.dropdown.SetItems(items);
        return this;
    }

    public EUIDialogDropdown<T> SetLabelFunctionForButton(FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton, FuncT1<Color, List<T>> colorFunctionButton, boolean isSmartText) {
        this.dropdown.SetLabelFunctionForButton(labelFunctionButton, colorFunctionButton, isSmartText);
        return this;
    }

    public EUIDialogDropdown<T> SetLabelFunctionForOption(FuncT1<String, T> labelFunction, boolean isSmartText) {
        this.dropdown.SetLabelFunctionForOption(labelFunction, isSmartText);
        return this;
    }

    @Override
    public ArrayList<T> GetConfirmValue()
    {
        return dropdown.GetCurrentItems();
    }

    @Override
    public ArrayList<T> GetCancelValue()
    {
        return null;
    }

    @Override
    public void Update()
    {
        super.Update();
        this.dropdown.TryUpdate();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);
        this.dropdown.TryRender(sb);
    }
}
