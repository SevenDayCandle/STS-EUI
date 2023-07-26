package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUISearchableDropdown<T> extends EUIDropdown<T> {
    protected EUITextInput searchInput;
    public ArrayList<EUIDropdownRow<T>> originalRows;

    public EUISearchableDropdown(EUIHitbox hb) {
        super(hb);
        initialize();
    }

    public EUISearchableDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction) {
        super(hb, labelFunction);
        initialize();
    }

    public EUISearchableDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        super(hb, labelFunction, options);
        initialize();
    }

    public EUISearchableDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, maxRows, canAutosizeButton);
        initialize();
    }

    public EUISearchableDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, float fontScale, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, fontScale, maxRows, canAutosizeButton);
        initialize();
    }

    @Override
    protected ArrayList<EUIDropdownRow<T>> getRowsForSelectionUpdate() {
        return originalRows;
    }

    public void openOrCloseMenu() {
        super.openOrCloseMenu();
        button.showText = !this.isOpen;
        this.rows = this.originalRows;
        searchInput.text = "";
        if (this.isOpen) {
            searchInput.start();
        }
        else {
            searchInput.end();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.searchInput.tryRender(sb);
        if (isOpen && searchInput.text.isEmpty()) {
            EUIRenderHelpers.writeCentered(sb, font, EUIRM.strings.misc_TypeToSearch, hb, Color.GRAY);
        }
    }

    public EUISearchableDropdown<T> setCanAutosizeButton(boolean value) {
        super.setCanAutosizeButton(value);
        return this;
    }

    public EUISearchableDropdown<T> setPosition(float x, float y) {
        super.setPosition(x, y);
        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.searchInput.tryUpdate();
    }

    public EUISearchableDropdown<T> setOnChange(ActionT1<List<T>> onChange) {
        super.setOnChange(onChange);
        return this;
    }

    public EUISearchableDropdown<T> setOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        super.setOnOpenOrClose(onOpenOrClose);
        return this;
    }

    protected void initialize() {
        this.originalRows = this.rows;
        searchInput = (EUITextInput) new EUITextInput(button.label.font, new RelativeHitbox(button.hb, button.hb.width, button.hb.height, button.hb.width / 2f, button.hb.height / 4f))
                .setOnUpdate(this::onUpdate)
                .setLabel("");
    }

    protected void onUpdate(String searchInput) {
        if (searchInput == null || searchInput.isEmpty()) {
            this.rows = this.originalRows;
        }
        else {
            this.rows = EUIUtils.filter(this.originalRows, row -> row.getText() != null && row.getText().toLowerCase().contains(searchInput.toLowerCase()));
            this.topVisibleRowIndex = 0;
        }
    }

}
