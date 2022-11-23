package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.EUI;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUIContextMenu<T> extends EUIDropdown<T>
{
    public EUIContextMenu(AdvancedHitbox hb) {
        super(hb);
        initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction) {
        super(hb, labelFunction);
        initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        super(hb, labelFunction, options);
        initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, maxRows, canAutosizeButton);
        initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, float fontScale, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, fontScale, maxRows, canAutosizeButton);
        initialize();
    }


    protected void initialize() {
        this.button.setActive(false);
    }

    public EUIContextMenu<T> setCanAutosizeButton(boolean value) {
        super.setCanAutosizeButton(value);
        return this;
    }

    public EUIContextMenu<T> setOnChange(ActionT1<List<T>> onChange) {
        super.setOnChange(onChange);
        return this;
    }

    public EUIContextMenu<T> setOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        super.setOnOpenOrClose(onOpenOrClose);
        return this;
    }

    public EUIContextMenu<T> setPosition(float x, float y) {
        super.setPosition(x, y);
        return this;
    }

    public void positionToOpen()
    {
        this.setPosition(InputHelper.mX, InputHelper.mY);
        openOrCloseMenu();
    }

    @Override
    protected void renderArrows(SpriteBatch sb) {
    }

    @Override
    public void renderImpl(SpriteBatch sb) {

        this.hb.render(sb);
        this.button.tryRender(sb);
        this.header.tryRender(sb);
        if ((this.isMultiSelect || this.showClearForSingle) && currentIndices.size() != 0) {
            this.clearButton.renderImpl(sb);
        }
        if (this.rows.size() > 0) {
            EUI.addPriorityPostRender(this::renderRowContent);
        }
    }

}
