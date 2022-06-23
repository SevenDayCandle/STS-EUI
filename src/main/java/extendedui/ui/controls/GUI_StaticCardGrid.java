package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.Mathf;

public class GUI_StaticCardGrid extends GUI_CardGrid
{
    protected int currentRow;
    public int visibleRowCount = 4;

    public GUI_StaticCardGrid()
    {
        this(0.5f, true);
    }

    public GUI_StaticCardGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public GUI_StaticCardGrid(float horizontalAlignment, boolean autoShowScrollbar)
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

        for (int i = Math.max(0, currentRow * ROW_SIZE); i < Math.min((currentRow + visibleRowCount) * ROW_SIZE, cards.group.size()); i++) {
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
            if (column >= ROW_SIZE)
            {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    protected void RenderCards(SpriteBatch sb)
    {
        for (int i = Math.max(0, currentRow * ROW_SIZE); i < Math.min((currentRow + visibleRowCount) * ROW_SIZE, cards.group.size()); i++)
        {
            AbstractCard card = cards.group.get(i);
            if (card != hoveredCard)
            {
                RenderCard(sb, card, i);
            }
        }
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
            min = (prevRow + visibleRowCount) * ROW_SIZE;
            max = min + (currentRow - prevRow) * ROW_SIZE;
        }
        else if (currentRow < prevRow) {
            max = prevRow * ROW_SIZE;
            min = max - (prevRow - currentRow) * ROW_SIZE;
        }
        else {
            return;
        }

        for (int i = Math.max(0, min); i < Math.min(max, cards.group.size()); i++) {
            AbstractCard card = cards.group.get(i);
            card.drawScale = card.targetDrawScale = target_scale;
        }

    }

    public int GetRowCount() {
        return (this.cards.size() - 1) / rowSize;
    }

}
