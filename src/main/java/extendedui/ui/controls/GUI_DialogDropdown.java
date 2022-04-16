package extendedui.ui.controls;

import basemod.interfaces.TextReceiver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GUI_DialogDropdown<T> extends GUI_Dialog<ArrayList<T>>
{
    protected GUI_Dropdown<T> dropdown;

    public GUI_DialogDropdown(String headerText)
    {
        this(headerText, "");
    }

    public GUI_DialogDropdown(String headerText, String descriptionText)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), headerText, descriptionText);
    }

    public GUI_DialogDropdown(AdvancedHitbox hb, String headerText, String descriptionText)
    {
        super(hb, headerText, descriptionText);
        this.dropdown = new GUI_Dropdown<T>(new AdvancedHitbox(hb.x + hb.width / 4, hb.y + hb.height / 4, hb.width / 2, Scale(48)))
                .SetCanAutosize(false, true);
    }

    public GUI_DialogDropdown<T> SetOptions(boolean isMultiSelect, boolean canAutosize) {
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
    public final GUI_DialogDropdown<T> SetItems(T... items) {
        this.dropdown.SetItems(items);
        return this;
    }

    public GUI_DialogDropdown<T> SetItems(List<T> items) {
        this.dropdown.SetItems(items);
        return this;
    }

    public GUI_DialogDropdown<T> SetLabelFunctionForButton(FuncT1<String, List<T>> labelFunctionButton, FuncT1<Color, List<T>> colorFunctionButton, boolean isSmartText) {
        this.dropdown.SetLabelFunctionForButton(labelFunctionButton, colorFunctionButton, isSmartText);
        return this;
    }

    public GUI_DialogDropdown<T> SetLabelFunctionForOption(FuncT1<String, T> labelFunction, boolean isSmartText) {
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
