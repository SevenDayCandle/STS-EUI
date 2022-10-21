package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import extendedui.EUI;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;
import java.util.List;

public class EUIContextMenu<T> extends EUIDropdown<T>
{
    public EUIContextMenu(AdvancedHitbox hb) {
        super(hb);
        Initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction) {
        super(hb, labelFunction);
        Initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        super(hb, labelFunction, options);
        Initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, maxRows, canAutosizeButton);
        Initialize();
    }

    public EUIContextMenu(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options, BitmapFont font, float fontScale, int maxRows, boolean canAutosizeButton) {
        super(hb, labelFunction, options, font, fontScale, maxRows, canAutosizeButton);
        Initialize();
    }


    protected void Initialize() {
        this.button.SetActive(false);
    }

    public EUIContextMenu<T> SetCanAutosizeButton(boolean value) {
        super.SetCanAutosizeButton(value);
        return this;
    }

    public EUIContextMenu<T> SetOnChange(ActionT1<List<T>> onChange) {
        super.SetOnChange(onChange);
        return this;
    }

    public EUIContextMenu<T> SetOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        super.SetOnOpenOrClose(onOpenOrClose);
        return this;
    }

    public EUIContextMenu<T> SetPosition(float x, float y) {
        super.SetPosition(x, y);
        return this;
    }

    public void PositionToOpen()
    {
        SetPosition(InputHelper.mX, InputHelper.mY);
        OpenOrCloseMenu();
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
