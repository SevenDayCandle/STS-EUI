package extendedui.ui.controls;

import com.megacrit.cardcrawl.core.Settings;

public abstract class EUICanvasGrid extends EUICanvas {
    protected float yPadding;
    protected int rowSize = 5;
    protected int sizeCache;

    public EUICanvasGrid(int rowSize, float yPadding) {
        super();
        this.rowSize = Math.max(1, rowSize);
        this.yPadding = yPadding;
    }

    protected void updateScrolling(boolean isDraggingScrollBar) {
        if (sizeCache != currentSize()) {
            refreshOffset();
        }
        super.updateScrolling(isDraggingScrollBar);
    }

    abstract public int currentSize();

    public void refreshOffset() {
        sizeCache = currentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
        lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize) {
            int offset = (sizeCache - 1) / rowSize;
            upperScrollBound += yPadding * (offset + 2);
            lowerScrollBound -= yPadding * (offset - 1);
        }
    }
}
