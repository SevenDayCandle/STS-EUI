package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.hitboxes.EUIHitbox;

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
        this(new EUIHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogDropdown(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.dropdown = new EUIDropdown<T>(new EUIHitbox(hb.x + hb.width / 4, hb.y + hb.height / 4, hb.width / 2, scale(48)))
                .setCanAutosize(false, true);
    }

    public EUIDialogDropdown<T> setOptions(boolean isMultiSelect, boolean canAutosize) {
        this.dropdown.isMultiSelect = isMultiSelect;
        this.dropdown.canAutosizeButton = canAutosize;
        if (this.dropdown.canAutosizeButton) {
            this.dropdown.autosize();
            this.hb.width = this.dropdown.hb.width * 2;
            this.hb.x = (Settings.WIDTH - this.hb.width) / 2;
            this.dropdown.setPosition(hb.x + hb.width / 4, hb.y + hb.height / 4);
        }
        return this;
    }

    @SafeVarargs
    public final EUIDialogDropdown<T> setItems(T... items) {
        this.dropdown.setItems(items);
        return this;
    }

    public EUIDialogDropdown<T> setItems(List<T> items) {
        this.dropdown.setItems(items);
        return this;
    }

    public EUIDialogDropdown<T> setLabelFunctionForButton(FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton, FuncT1<Color, List<T>> colorFunctionButton, boolean isSmartText) {
        this.dropdown.setLabelFunctionForButton(labelFunctionButton, colorFunctionButton, isSmartText);
        return this;
    }

    public EUIDialogDropdown<T> setLabelFunctionForOption(FuncT1<String, T> labelFunction, boolean isSmartText) {
        this.dropdown.setLabelFunctionForOption(labelFunction, isSmartText);
        return this;
    }

    @Override
    public ArrayList<T> getConfirmValue()
    {
        return dropdown.getCurrentItems();
    }

    @Override
    public ArrayList<T> getCancelValue()
    {
        return null;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.dropdown.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        this.dropdown.tryRender(sb);
    }
}
