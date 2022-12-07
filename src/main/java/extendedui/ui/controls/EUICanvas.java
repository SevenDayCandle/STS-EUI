package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.ui.EUIBase;
import extendedui.ui.hitboxes.EUIHitbox;

public abstract class EUICanvas extends EUIBase
{
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;

    protected boolean canDragScreen = true;
    protected float lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;
    protected float scrollDelta;
    protected float scrollStart;
    protected float upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
    public boolean autoShowScrollbar = true;
    public boolean draggingScreen;
    public boolean instantSnap;
    public final EUIVerticalScrollBar scrollBar;


    public EUICanvas() {
        this.scrollBar = new EUIVerticalScrollBar(new EUIHitbox(screenW(0.03f), screenH(0.7f)))
                .setOnScroll(this::onScroll);
    }

    public EUICanvas setScrollBounds(float lowerScrollBound, float upperScrollBound) {
        this.lowerScrollBound = lowerScrollBound;
        this.upperScrollBound = Math.max(lowerScrollBound, upperScrollBound);
        return this;
    }

    public EUICanvas showScrollbar(boolean showScrollbar)
    {
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    @Override
    public void updateImpl()
    {
        if (!EUI.doesActiveElementExist()) {
            if (shouldShowScrollbar())
            {
                scrollBar.updateImpl();
                updateScrolling(scrollBar.isDragging);
            }
            else
            {
                updateScrolling(false);
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (shouldShowScrollbar())
        {
            scrollBar.renderImpl(sb);
        }
    }

    protected void updateScrolling(boolean isDraggingScrollBar)
    {
        if (!isDraggingScrollBar)
        {
            if (draggingScreen)
            {
                if (InputHelper.isMouseDown && EUI.tryDragging())
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

                if (canDragScreen && InputHelper.justClickedLeft && EUI.tryDragging())
                {
                    draggingScreen = true;
                    scrollStart = InputHelper.mY - scrollDelta;
                }
            }
        }

        if (scrollDelta < lowerScrollBound)
        {
            scrollDelta = instantSnap ? lowerScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, lowerScrollBound);
        }
        else if (scrollDelta > upperScrollBound)
        {
            scrollDelta = instantSnap ? upperScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, upperScrollBound);
        }

        scrollBar.scroll(MathHelper.percentFromValueBetween(lowerScrollBound, upperScrollBound, scrollDelta), false);
    }

    public float getScrollDelta()
    {
        return scrollDelta;
    }

    public void moveToTop() {
        scrollBar.scroll(0, true);
    }

    public boolean isHovered() {return scrollBar.hb.hovered;}

    protected void onScroll(float newPercent)
    {
        if (!EUI.doesActiveElementExist())
        {
            scrollDelta = MathHelper.valueFromPercentBetween(lowerScrollBound, upperScrollBound, newPercent);
        }
    }

    protected boolean shouldShowScrollbar()
    {
        return autoShowScrollbar && upperScrollBound > SCROLL_BAR_THRESHOLD;
    }
}
