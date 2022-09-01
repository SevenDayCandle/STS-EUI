package extendedui.ui.controls;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.ActionT2;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;

import java.util.ArrayList;

// TODO controller/keyboard support
public class GUI_RelicGrid extends GUI_CanvasGrid
{
    protected static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractRelic.PAD_X * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractRelic.PAD_X * 0.75f);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    protected static final float PAD = Scale(AbstractRelic.RAW_W) * 0.75f + AbstractRelic.PAD_X;
    public static final int ROW_SIZE = 10;

    protected ActionT1<AbstractRelic> onRelicClick;
    protected ActionT1<AbstractRelic> onRelicHovered;
    protected ActionT1<AbstractRelic> onRelicRightClick;
    protected ActionT2<SpriteBatch, AbstractRelic> onRelicRender;
    protected float draw_x;
    protected float draw_top_y = DRAW_START_Y;
    protected int hoveredIndex;
    public boolean shouldEnlargeHovered = true;
    public float pad_x = PAD;
    public float pad_y = PAD;
    public ArrayList<RelicInfo> relicGroup;
    public RelicInfo hoveredRelic = null;
    public String message = null;
    public float target_scale = 1;
    public float starting_scale = target_scale;

    public GUI_RelicGrid()
    {
        this(0.5f, true);
    }

    public GUI_RelicGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public GUI_RelicGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        super(ROW_SIZE, PAD);
        this.autoShowScrollbar = autoShowScrollbar;
        this.relicGroup = new ArrayList<>();
    }

    public GUI_RelicGrid AddPadX(float padX)
    {
        this.pad_x += padX;

        return this;
    }

    public GUI_RelicGrid AddPadY(float padY)
    {
        this.pad_y += padY;

        return this;
    }

    public GUI_RelicGrid SetHorizontalAlignment(float percentage)
    {
        this.draw_x = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.SetPosition(ScreenW((percentage < 0.5f) ? 0.05f : 0.9f), ScreenH(0.5f));

        return this;
    }

    public GUI_RelicGrid SetVerticalStart(float posY) {
        this.draw_top_y = posY;

        return this;
    }

    public void Clear()
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


        RefreshOffset();
    }

    public GUI_RelicGrid SetRelics(Iterable<AbstractRelic> relics)
    {
        relicGroup.clear();
        return AddRelics(relics);
    }

    public GUI_RelicGrid AddRelics(Iterable<AbstractRelic> relics)
    {
        for (AbstractRelic relic : relics)
        {
            AddRelic(relic);
        }

        return this;
    }

    public GUI_RelicGrid AddRelic(AbstractRelic relic)
    {
        relicGroup.add(new RelicInfo(relic));

        return this;
    }

    public GUI_RelicGrid RemoveRelic(AbstractRelic relic)
    {
        relicGroup.removeIf(rInfo -> rInfo.relic == relic);

        return this;
    }

    public GUI_RelicGrid SetRelicScale(float startingScale, float targetScale) {
        this.starting_scale = startingScale;
        this.target_scale = targetScale;

        return this;
    }

    @Override
    public void Update()
    {
        super.Update();

        UpdateRelics();

        if (hoveredRelic != null && EUI.TryHover(hoveredRelic.relic.hb))
        {
            if (EUIInputManager.RightClick.IsJustPressed() && onRelicRightClick != null)
            {
                onRelicRightClick.Invoke(hoveredRelic.relic);
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
                    onRelicClick.Invoke(hoveredRelic.relic);
                }
            }
        }
    }

    protected void UpdateRelics()
    {
        hoveredRelic = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < relicGroup.size(); i++)
        {
            RelicInfo relic = relicGroup.get(i);
            relic.relic.targetX = (DRAW_START_X * draw_x) + (column * PAD);
            relic.relic.targetY = draw_top_y + scrollDelta - (row * pad_y);
            relic.relic.update();

            if (relic.relic.hb.hovered)
            {
                hoveredRelic = relic;
                hoveredIndex = i;
                if (!shouldEnlargeHovered) {
                    relic.relic.scale = target_scale;
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

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);

        RenderRelics(sb);

        if (hoveredRelic != null)
        {
            hoveredRelic.relic.renderTip(sb);
        }

        if (message != null)
        {
            FontHelper.renderDeckViewTip(sb, message, Scale(96f), Settings.CREAM_COLOR);
        }
    }

    protected void RenderRelics(SpriteBatch sb) {
        for (int i = 0; i < relicGroup.size(); i++)
        {
            RelicInfo relic = relicGroup.get(i);
            RenderRelic(sb, relic);
        }
    }

    protected void RenderRelic(SpriteBatch sb, RelicInfo relic)
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
            relic.relic.render(sb,false, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
        }

        if (onRelicRender != null)
        {
            onRelicRender.Invoke(sb, relic.relic);
        }
    }

    @Override
    public void RefreshOffset()
    {
        sizeCache = CurrentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize * 2)
        {
            int offset = ((sizeCache / rowSize) - ((sizeCache % rowSize > 0) ? 1 : 2));
            upperScrollBound += yPadding * offset;
        }
    }

    @Override
    public int CurrentSize()
    {
        return relicGroup.size();
    }

    protected static class RelicInfo
    {
        public final AbstractRelic relic;
        public final AbstractCard.CardColor relicColor;
        public final boolean locked;

        public RelicInfo(AbstractRelic relic)
        {
            this.relic = relic;
            this.relicColor = EUIGameUtils.GetRelicColor(relic.relicId);
            this.locked = UnlockTracker.isRelicLocked(relic.relicId);
        }
    }
}
