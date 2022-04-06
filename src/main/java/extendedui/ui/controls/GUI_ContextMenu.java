package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;
import java.util.List;

public class GUI_ContextMenu<T> extends GUI_Dropdown<T>
{
    public GUI_ContextMenu(AdvancedHitbox hb) {
        super(hb);
        Initialize();
    }

    public GUI_ContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction) {
        super(hb, labelFunction);
        Initialize();
    }

    public GUI_ContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        super(hb, labelFunction, options);
        Initialize();
    }

    public GUI_ContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, maxRows, canAutosizeButton);
        Initialize();
    }

    protected void Initialize() {
        this.button.SetActive(false);
    }

    public GUI_ContextMenu<T> SetCanAutosizeButton(boolean value) {
        super.SetCanAutosizeButton(value);
        return this;
    }

    public GUI_ContextMenu<T> SetOnChange(ActionT1<List<T>> onChange) {
        super.SetOnChange(onChange);
        return this;
    }

    public GUI_ContextMenu<T> SetOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        super.SetOnOpenOrClose(onOpenOrClose);
        return this;
    }

    public GUI_ContextMenu<T> SetPosition(float x, float y) {
        super.SetPosition(x, y);
        return this;
    }

    @Override
    protected void RenderArrows(SpriteBatch sb) {
    }

    @Override
    public void Render(SpriteBatch sb) {

        this.hb.render(sb);
        this.button.TryRender(sb);
        this.header.TryRender(sb);
        if ((this.isMultiSelect || this.showClearForSingle) && currentIndices.size() != 0) {
            this.clearButton.Render(sb);
        }
        if (this.rows.size() > 0) {
            EUI.AddPriorityPostRender(this::RenderRowContent);
        }
    }

}
