package extendedui.ui.controls;

import com.megacrit.cardcrawl.core.Settings;

public abstract class EUICanvasGrid extends EUICanvas {
    protected float padY;
    protected int rowSize = 5;
    protected int sizeCache;

    public EUICanvasGrid(int rowSize, float padY) {
        super();
        this.rowSize = Math.max(1, rowSize);
        this.padY = padY;
    }

    public void refreshOffset() {
        sizeCache = currentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
        lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize) {
            int offset = (sizeCache - 1) / rowSize;
            upperScrollBound += padY * (offset + 2);
            lowerScrollBound -= padY * (offset - 1);
        }
    }

    protected void updateScrolling(boolean isDraggingScrollBar) {
        if (sizeCache != currentSize()) {
            refreshOffset();
        }
        super.updateScrolling(isDraggingScrollBar);
    }

    abstract public int currentSize();
}
