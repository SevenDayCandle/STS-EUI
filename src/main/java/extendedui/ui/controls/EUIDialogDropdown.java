package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUIDialogDropdown<T> extends EUIDialog<ArrayList<T>> {
    protected EUIDropdown<T> dropdown;

    public EUIDialogDropdown(String headerText) {
        this(headerText, "");
    }

    public EUIDialogDropdown(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(300), scale(390));
    }

    public EUIDialogDropdown(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogDropdown(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.dropdown = new EUIDropdown<T>(new RelativeHitbox(hb, hb.width / 2, scale(48), hb.width / 4, hb.height / 4))
                .setCanAutosize(false, true);
    }

    protected String getCancelText() {
        return CHOICE_TEXT[3];
    }

    protected String getConfirmText() {
        return CHOICE_TEXT[2];
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.dropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.dropdown.tryUpdate();
    }

    @Override
    public ArrayList<T> getConfirmValue() {
        return dropdown.getCurrentItems();
    }

    @Override
    public ArrayList<T> getCancelValue() {
        return null;
    }

    public EUIDialogDropdown<T> setItems(List<T> items) {
        this.dropdown.setItems(items);
        return this;
    }

    @SafeVarargs
    public final EUIDialogDropdown<T> setItems(T... items) {
        this.dropdown.setItems(items);
        return this;
    }

    public EUIDialogDropdown<T> setLabelFunctionForButton(FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton, FuncT1<Color, List<T>> colorFunctionButton, boolean isSmartText) {
        this.dropdown.setLabelFunctionForButton(labelFunctionButton, isSmartText);
        return this;
    }

    public EUIDialogDropdown<T> setLabelFunctionForOption(FuncT1<String, T> labelFunction, boolean isSmartText) {
        this.dropdown.setLabelFunctionForOption(labelFunction, isSmartText);
        return this;
    }

    public EUIDialogDropdown<T> setOptions(boolean isMultiSelect, boolean canAutosize) {
        this.dropdown.isMultiSelect = isMultiSelect;
        this.dropdown.canAutosizeButton = canAutosize;
        if (this.dropdown.canAutosizeButton) {
            this.dropdown.autosize();
            this.hb.width = this.dropdown.hb.width * 2;
            this.hb.x = (Settings.WIDTH - this.hb.width) / 2;
            this.dropdown.setOffset(hb.width / 4, hb.height / 4);
        }
        return this;
    }
}
