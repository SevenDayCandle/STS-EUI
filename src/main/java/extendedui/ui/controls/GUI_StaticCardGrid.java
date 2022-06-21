package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUI;
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

        for (int i = currentRow * ROW_SIZE; i < Math.min((currentRow + visibleRowCount) * ROW_SIZE, cards.group.size()); i++) {
            AbstractCard card = cards.group.get(i);
            card.current_x = card.target_x = (DRAW_START_X * draw_x) + (column * PAD_X);
            card.current_y = card.target_y = DRAW_START_Y - (row * pad_y);
            card.fadingOut = false;
            card.update();
            card.updateHoverLogic();

            if (card.hb.hovered)
            {
                hoveredCard = card;
                if (!shouldEnlargeHovered) {
                    card.drawScale = card.targetDrawScale = 0.8f;
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
        for (int i = currentRow * ROW_SIZE; i < Math.min((currentRow + visibleRowCount) * ROW_SIZE, cards.group.size()); i++)
        {
            AbstractCard card = cards.group.get(i);
            if (card != hoveredCard)
            {
                RenderCard(sb, card);
            }
        }
    }

    @Override
    protected void UpdateScrolling(boolean isDraggingScrollBar)
    {
        super.UpdateScrolling(isDraggingScrollBar);
        int rowCount = GetRowCount();
        currentRow = (int) Mathf.Clamp(scrollBar.currentScrollPercent * rowCount, 0, rowCount - 1);
    }

    public int GetRowCount() {
        return (this.cards.size() - 1) / rowSize;
    }

}
