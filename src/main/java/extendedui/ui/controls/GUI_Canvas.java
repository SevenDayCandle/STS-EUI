package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.ui.GUI_Base;
import extendedui.ui.hitboxes.AdvancedHitbox;

import java.util.ArrayList;

public abstract class GUI_Canvas extends GUI_Base
{
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;

    protected boolean canDragScreen = true;
    protected float lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;
    protected float scrollDelta;
    protected float scrollStart;
    protected float upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
    protected float yPadding;
    protected int rowSize = 5;
    protected int sizeCache;
    public boolean autoShowScrollbar;
    public boolean draggingScreen;
    public boolean instantSnap;
    public final GUI_VerticalScrollBar scrollBar;


    public GUI_Canvas(int rowSize, float yPadding) {
        this.rowSize = Math.max(1, rowSize);
        this.yPadding = yPadding;
        this.scrollBar = new GUI_VerticalScrollBar(new AdvancedHitbox(ScreenW(0.03f), ScreenH(0.7f)))
                .SetOnScroll(this::OnScroll);
    }

    @Override
    public void Update()
    {
        if (!EUI.DoesActiveElementExist()) {
            if (ShouldShowScrollbar())
            {
                scrollBar.Update();
                UpdateScrolling(scrollBar.isDragging);
            }
            else
            {
                UpdateScrolling(false);
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        if (ShouldShowScrollbar())
        {
            scrollBar.Render(sb);
        }
    }

    protected void UpdateScrolling(boolean isDraggingScrollBar)
    {
        if (!isDraggingScrollBar)
        {
            if (draggingScreen)
            {
                if (InputHelper.isMouseDown && EUI.TryDragging())
                {
                    scrollDelta = InputHelper.mY - scrollStart;
                }
                else
                {
                    draggingScreen = false;
                }
            }
            else
            {
                if (InputHelper.scrolledDown)
                {
                    scrollDelta += Settings.SCROLL_SPEED;
                }
                else if (InputHelper.scrolledUp)
                {
                    scrollDelta -= Settings.SCROLL_SPEED;
                }

                if (canDragScreen && InputHelper.justClickedLeft && EUI.TryDragging())
                {
                    draggingScreen = true;
                    scrollStart = InputHelper.mY - scrollDelta;
                }
            }
        }

        if (sizeCache != CurrentSize())
        {
            RefreshOffset();
        }

        if (scrollDelta < lowerScrollBound)
        {
            scrollDelta = instantSnap ? lowerScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, lowerScrollBound);
        }
        else if (scrollDelta > upperScrollBound)
        {
            scrollDelta = instantSnap ? upperScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, upperScrollBound);
        }

        scrollBar.Scroll(MathHelper.percentFromValueBetween(lowerScrollBound, upperScrollBound, scrollDelta), false);
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

    public float GetScrollDelta()
    {
        return scrollDelta;
    }

    protected void OnScroll(float newPercent)
    {
        if (!EUI.DoesActiveElementExist())
        {
            scrollDelta = MathHelper.valueFromPercentBetween(lowerScrollBound, upperScrollBound, newPercent);
        }
    }

    protected boolean ShouldShowScrollbar()
    {
        return autoShowScrollbar && upperScrollBound > SCROLL_BAR_THRESHOLD;
    }

    abstract public int CurrentSize();
}
