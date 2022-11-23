package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;

import java.util.ArrayList;

// TODO controller/keyboard support
public class EUIRelicGrid extends EUICanvasGrid
{
    protected static final float PAD = scale(80);
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * scale(AbstractRelic.RAW_W)) - (4f * PAD);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    public static final int ROW_SIZE = 10;

    protected ActionT1<AbstractRelic> onRelicClick;
    protected ActionT1<AbstractRelic> onRelicHovered;
    protected ActionT1<AbstractRelic> onRelicRightClick;
    protected ActionT2<SpriteBatch, AbstractRelic> onRelicRender;
    protected float drawX = DRAW_START_X;
    protected float drawTopY = DRAW_START_Y;
    protected int hoveredIndex;
    public boolean shouldEnlargeHovered = true;
    public float padX = PAD;
    public float padY = PAD;
    public ArrayList<RelicInfo> relicGroup;
    public RelicInfo hoveredRelic = null;
    public String message = null;
    public float targetScale = 1;
    public float startingScale = targetScale;

    public EUIRelicGrid()
    {
        this(0.5f, true);
    }

    public EUIRelicGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public EUIRelicGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        super(ROW_SIZE, PAD);
        this.autoShowScrollbar = autoShowScrollbar;
        this.relicGroup = new ArrayList<>();

        setHorizontalAlignment(horizontalAlignment);
    }

    public EUIRelicGrid addPadX(float padX)
    {
        this.padX += padX;

        return this;
    }

    public EUIRelicGrid addPadY(float padY)
    {
        this.padY += padY;

        return this;
    }

    public EUIRelicGrid setOnRelicHover(ActionT1<AbstractRelic> onRelicHovered)
    {
        this.onRelicHovered = onRelicHovered;

        return this;
    }

    public EUIRelicGrid setOnRelicClick(ActionT1<AbstractRelic> onRelicClick)
    {
        this.onRelicClick = onRelicClick;

        return this;
    }

    public EUIRelicGrid setOnRelicRightClick(ActionT1<AbstractRelic> onRelicRightClick)
    {
        this.onRelicRightClick = onRelicRightClick;

        return this;
    }

    public EUIRelicGrid setHorizontalAlignment(float percentage)
    {
        this.drawX = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.setPosition(screenW((percentage < 0.5f) ? 0.05f : 0.9f), screenH(0.5f));

        return this;
    }

    public EUIRelicGrid setVerticalStart(float posY) {
        this.drawTopY = posY;

        return this;
    }

    public void clear()
    {
        this.sizeCache = 0;
        this.hoveredRelic = null;
        this.hoveredIndex = 0;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the relics from any outside relic group given to it
        this.relicGroup = new ArrayList<>();


        refreshOffset();
    }

    public EUIRelicGrid setRelics(Iterable<AbstractRelic> relics)
    {
        relicGroup.clear();
        return addRelics(relics);
    }

    public EUIRelicGrid addRelics(Iterable<AbstractRelic> relics)
    {
        for (AbstractRelic relic : relics)
        {
            addRelic(relic);
        }

        return this;
    }

    public EUIRelicGrid addRelic(AbstractRelic relic)
    {
        relicGroup.add(new RelicInfo(relic));

        return this;
    }

    public EUIRelicGrid removeRelic(AbstractRelic relic)
    {
        relicGroup.removeIf(rInfo -> rInfo.relic == relic);

        return this;
    }

    public EUIRelicGrid setRelicScale(float startingScale, float targetScale) {
        this.startingScale = startingScale;
        this.targetScale = targetScale;

        return this;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();

        updateRelics();

        if (hoveredRelic != null && EUI.tryHover(hoveredRelic.relic.hb))
        {
            if (EUIInputManager.RightClick.isJustPressed() && onRelicRightClick != null)
            {
                onRelicRightClick.invoke(hoveredRelic.relic);
                return;
            }

            if (InputHelper.justClickedLeft)
            {
                hoveredRelic.relic.hb.clickStarted = true;
            }

            if (hoveredRelic.relic.hb.clicked || CInputActionSet.select.isJustPressed())
            {
                hoveredRelic.relic.hb.clicked = false;

                if (onRelicClick != null)
                {
                    onRelicClick.invoke(hoveredRelic.relic);
                }
            }
        }
    }

    protected void updateRelics()
    {
        hoveredRelic = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < relicGroup.size(); i++)
        {
            RelicInfo relic = relicGroup.get(i);
            relic.relic.targetX = (DRAW_START_X * drawX) + (column * PAD);
            relic.relic.targetY = drawTopY + scrollDelta - (row * padY);
            updateHoverLogic(relic, i);

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    protected void updateHoverLogic(RelicInfo relic, int i)
    {
        relic.relic.update();
        relic.relic.hb.update();
        relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);

        if (relic.relic.hb.hovered)
        {
            hoveredRelic = relic;
            hoveredIndex = i;
            if (!shouldEnlargeHovered) {
                relic.relic.scale = targetScale;
            }
        }
    }

    public void forceUpdateRelicPositions()
    {
        int row = 0;
        int column = 0;
        for (RelicInfo relic : relicGroup)
        {
            relic.relic.currentX = relic.relic.targetX = (DRAW_START_X * drawX) + (column * PAD);
            relic.relic.currentY = relic.relic.targetY = drawTopY + scrollDelta - (row * padY);
            relic.relic.hb.update();
            relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);

        renderRelics(sb);

        if (hoveredRelic != null)
        {
            hoveredRelic.relic.renderTip(sb);
        }

        if (message != null)
        {
            FontHelper.renderDeckViewTip(sb, message, scale(96f), Settings.CREAM_COLOR);
        }
    }

    protected void renderRelics(SpriteBatch sb) {
        for (int i = 0; i < relicGroup.size(); i++)
        {
            renderRelic(sb, relicGroup.get(i));
        }
    }

    protected void renderRelic(SpriteBatch sb, RelicInfo relic)
    {
        if (relic.locked)
        {
            switch (relic.relicColor)
            {
                case RED:
                    relic.relic.renderLock(sb, Settings.RED_RELIC_COLOR);
                    break;
                case GREEN:
                    relic.relic.renderLock(sb, Settings.GREEN_RELIC_COLOR);
                    break;
                case BLUE:
                    relic.relic.renderLock(sb, Settings.BLUE_RELIC_COLOR);
                    break;
                case PURPLE:
                    relic.relic.renderLock(sb, Settings.PURPLE_RELIC_COLOR);
                    break;
                default:
                    relic.relic.renderLock(sb, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
            }
        }
        else
        {
            switch (relic.relicColor)
            {
                case RED:
                    relic.relic.render(sb, false, Settings.RED_RELIC_COLOR);
                    break;
                case GREEN:
                    relic.relic.render(sb, false, Settings.GREEN_RELIC_COLOR);
                    break;
                case BLUE:
                    relic.relic.render(sb, false, Settings.BLUE_RELIC_COLOR);
                    break;
                case PURPLE:
                    relic.relic.render(sb, false, Settings.PURPLE_RELIC_COLOR);
                    break;
                default:
                    relic.relic.render(sb, false, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
            }
        }

        if (onRelicRender != null)
        {
            onRelicRender.invoke(sb, relic.relic);
        }
    }

    @Override
    public void refreshOffset()
    {
        sizeCache = currentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize * 2)
        {
            int offset = ((sizeCache / rowSize) - ((sizeCache % rowSize > 0) ? 1 : 2));
            upperScrollBound += yPadding * offset;
        }
    }

    @Override
    public boolean isHovered() {return super.isHovered() || hoveredRelic != null;}

    @Override
    public int currentSize()
    {
        return relicGroup.size();
    }

    public static class RelicInfo
    {
        public final AbstractRelic relic;
        public final AbstractCard.CardColor relicColor;
        public final boolean locked;

        public RelicInfo(AbstractRelic relic)
        {
            this.relic = relic;
            this.relicColor = EUIGameUtils.getRelicColor(relic.relicId);
            this.locked = UnlockTracker.isRelicLocked(relic.relicId);
        }
    }
}
