package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.utilities.PotionGroup;

public class EUIStaticPotionGrid extends EUIPotionGrid {
    public int visibleRowCount = 20;
    protected int currentRow;

    public EUIStaticPotionGrid() {
        this(0.5f, true);
    }

    public EUIStaticPotionGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
        instantSnap = true;
    }

    public EUIStaticPotionGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    @Override
    public void forceUpdatePotionPositions() {
        int row = 0;
        int column = 0;
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, potionGroup.size()); i++) {
            PotionGroup.PotionInfo potion = potionGroup.group.get(i);
            potion.potion.posX = (DRAW_START_X * drawX) + (column * PAD);
            potion.potion.posY = drawTopY + scrollDelta - (row * padY);
            potion.potion.hb.update();
            potion.potion.hb.move(potion.potion.posX, potion.potion.posY);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected void updatePotions() {
        hoveredPotion = null;

        int row = 0;
        int column = 0;

        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, potionGroup.size()); i++) {
            PotionGroup.PotionInfo potion = potionGroup.group.get(i);
            potion.potion.posX = (DRAW_START_X * drawX) + (column * PAD);
            potion.potion.posY = drawTopY - (row * padY);
            updateHoverLogic(potion, i);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected float getScrollDistance(AbstractPotion relic, int index) {
        if (relic != null) {
            float scrollDistance = 1f / getRowCount();
            if (relic.posY > drawTopY || index < currentRow * rowSize) {
                return -scrollDistance;
            }
            else if (relic.posY < 0 || index > (currentRow + visibleRowCount) * rowSize) {
                return scrollDistance;
            }
        }
        return 0;
    }

    public int getRowCount() {
        return (potionGroup.size() - 1) / rowSize;
    }

    @Override
    protected void renderPotions(SpriteBatch sb) {
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, potionGroup.size()); i++) {
            renderPotion(sb, potionGroup.group.get(i));
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

        for (int i = Math.max(0, min); i < Math.min(max, potionGroup.size()); i++) {
            PotionGroup.PotionInfo card = potionGroup.group.get(i);
            card.potion.scale = targetScale;
        }

    }

}
