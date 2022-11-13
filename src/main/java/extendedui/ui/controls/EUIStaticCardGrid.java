package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.Mathf;

public class EUIStaticCardGrid extends EUICardGrid
{
    protected int currentRow;
    public int visibleRowCount = 4;

    public EUIStaticCardGrid()
    {
        this(0.5f, true);
    }

    public EUIStaticCardGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public EUIStaticCardGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        super(horizontalAlignment, autoShowScrollbar);
        instantSnap = true;
    }

    @Override
    protected void UpdateCards()
    {
        hoveredCard = null;

        int row = 0;
        int column = 0;

        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, cards.group.size()); i++) {
            AbstractCard card = cards.group.get(i);
            card.current_x = card.target_x = (DRAW_START_X * draw_x) + (column * PAD_X);
            card.current_y = card.target_y = draw_top_y - (row * pad_y);
            card.fadingOut = false;
            card.update();
            card.updateHoverLogic();

            if (card.hb.hovered)
            {
                hoveredCard = card;
                hoveredIndex = i;
                if (!shouldEnlargeHovered) {
                    card.drawScale = card.targetDrawScale = CARD_SCALE;
                }
            }

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    public void ForceUpdateCardPositions()
    {
        int row = 0;
        int column = 0;
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, cards.group.size()); i++) {
            AbstractCard card = cards.group.get(i);
            card.current_x = card.target_x = (DRAW_START_X * draw_x) + (column * PAD_X);
            card.current_y = card.target_y = draw_top_y - (row * pad_y);
            card.drawScale = card.targetDrawScale = target_scale;
            card.hb.move(card.current_x, card.current_y);

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected void RenderCards(SpriteBatch sb)
    {
        for (int i = Math.max(0, currentRow * rowSize); i < Math.min((currentRow + visibleRowCount) * rowSize, cards.group.size()); i++)
        {
            AbstractCard card = cards.group.get(i);
            if (card != hoveredCard)
            {
                RenderCard(sb, card);
            }
        }
    }

    @Override
    protected float GetScrollDistance(AbstractCard card, int index)
    {
        if (card != null)
        {
            float scrollDistance = 1f / GetRowCount();
            if (card.target_y > draw_top_y || index < currentRow * rowSize)
            {
                return -scrollDistance;
            }
            else if (card.target_y < 0 || index > (currentRow + visibleRowCount) * rowSize)
            {
                return scrollDistance;
            }
        }
        return 0;
    }

    @Override
    protected void UpdateScrolling(boolean isDraggingScrollBar)
    {
        super.UpdateScrolling(isDraggingScrollBar);
        int rowCount = GetRowCount();
        int prevRow = currentRow;
        currentRow = (int) Mathf.Clamp(scrollBar.currentScrollPercent * rowCount, 0, rowCount - 1);

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

        for (int i = Math.max(0, min); i < Math.min(max, cards.group.size()); i++) {
            AbstractCard card = cards.group.get(i);
            card.drawScale = card.targetDrawScale = target_scale;
        }

    }
}
