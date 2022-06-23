package extendedui.ui.controls;

import com.megacrit.cardcrawl.core.Settings;

public abstract class GUI_CanvasGrid extends GUI_Canvas
{
    protected float yPadding;
    protected int rowSize = 5;
    protected int sizeCache;

    public GUI_CanvasGrid(int rowSize, float yPadding)
    {
        super();
        this.rowSize = Math.max(1, rowSize);
        this.yPadding = yPadding;
    }

    public void RefreshOffset()
    {
        sizeCache = CurrentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
        lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize)
        {
            int offset = (sizeCache - 1) / rowSize;
            upperScrollBound += yPadding * (offset + 2);
            lowerScrollBound -= yPadding * (offset - 1);
        }
    }

    protected void UpdateScrolling(boolean isDraggingScrollBar) {
        if (sizeCache != CurrentSize())
        {
            RefreshOffset();
        }
        super.UpdateScrolling(isDraggingScrollBar);
    }

    abstract public int CurrentSize();
}
