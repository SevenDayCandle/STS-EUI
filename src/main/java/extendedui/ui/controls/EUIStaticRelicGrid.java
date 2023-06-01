package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.utilities.RelicGroup;

public class EUIStaticRelicGrid extends EUIRelicGrid {
    public int visibleRowCount = 20;
    protected int currentRow;

    public EUIStaticRelicGrid() {
        this(0.5f, true);
    }

    public EUIStaticRelicGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
        instantSnap = true;
    }

    public EUIStaticRelicGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    @Override
    public void forceUpdateRelicPositions() {
        int row = 0;
        int column = 0;
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, relicGroup.size()); i++) {
            RelicGroup.RelicInfo relic = relicGroup.group.get(i);
            relic.relic.currentX = relic.relic.targetX = (DRAW_START_X * drawX) + (column * PAD);
            relic.relic.currentY = relic.relic.targetY = drawTopY + scrollDelta - (row * padY);
            relic.relic.hb.update();
            relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected void updateRelics() {
        hoveredRelic = null;

        int row = 0;
        int column = 0;

        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, relicGroup.size()); i++) {
            RelicGroup.RelicInfo relic = relicGroup.group.get(i);
            relic.relic.currentX = relic.relic.targetX = (DRAW_START_X * drawX) + (column * PAD);
            relic.relic.currentY = relic.relic.targetY = drawTopY - (row * padY);
            updateHoverLogic(relic, i);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected float getScrollDistance(AbstractRelic relic, int index) {
        if (relic != null) {
            float scrollDistance = 1f / getRowCount();
            if (relic.targetY > drawTopY || index < currentRow * rowSize) {
                return -scrollDistance;
            }
            else if (relic.targetY < 0 || index > (currentRow + visibleRowCount) * rowSize) {
                return scrollDistance;
            }
        }
        return 0;
    }

    public int getRowCount() {
        return (relicGroup.size() - 1) / rowSize;
    }

    @Override
    protected void renderRelics(SpriteBatch sb) {
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, relicGroup.size()); i++) {
            renderRelic(sb, relicGroup.group.get(i));
        }
    }

    // TODO Remove, probably not necessary
    @Override
    protected void updateScrolling(boolean isDraggingScrollBar) {
        super.updateScrolling(isDraggingScrollBar);
        int rowCount = getRowCount();
        int prevRow = currentRow;
        currentRow = (int) MathUtils.clamp(scrollBar.currentScrollPercent * rowCount, 0, rowCount - 1);

        // Reset zooming of the cards newly added to the screen
        int min;
        int max;
        if (prevRow < currentRow) {
            min = (prevRow + visibleRowCount) * rowSize;
            max = min + (currentRow - prevRow) * rowSize;
        }
        else if (currentRow < prevRow) {
            max = prevRow * rowSize;
            min = max - (prevRow - currentRow) * rowSize;
        }
        else {
            return;
        }

        for (int i = Math.max(0, min); i < Math.min(max, relicGroup.size()); i++) {
            RelicGroup.RelicInfo card = relicGroup.group.get(i);
            card.relic.scale = targetScale;
        }

    }

}
