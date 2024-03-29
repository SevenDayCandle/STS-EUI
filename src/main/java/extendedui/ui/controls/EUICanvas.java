package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.ui.EUIBase;
import extendedui.ui.hitboxes.EUIHitbox;

public abstract class EUICanvas extends EUIBase {
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    public final EUIVerticalScrollBar scrollBar;
    protected boolean canDragScreen = true;
    protected float lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;
    protected float scrollDelta;
    protected float scrollStart;
    protected float upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
    public boolean autoShowScrollbar = true;
    public boolean draggingScreen;
    public boolean instantSnap;


    public EUICanvas() {
        this.scrollBar = new EUIVerticalScrollBar(new EUIHitbox(screenW(0.93f), screenH(0.15f), screenW(0.03f), screenH(0.7f)))
                .setOnScroll(this::updateScrollbarDrag);
    }

    public EUICanvas canDragScreen(boolean canDrag) {
        this.canDragScreen = canDrag;

        return this;
    }

    public float getScrollDelta() {
        return scrollDelta;
    }

    public boolean isHovered() {
        return scrollBar.hb.hovered;
    }

    public void moveToTop() {
        scrollBar.scroll(0, true);
    }

    protected void onScroll(float scrollDelta) {
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (shouldShowScrollbar()) {
            scrollBar.renderImpl(sb);
        }
    }

    public EUICanvas setScrollbarAlignment(float percentage) {
        this.scrollBar.setPosition(screenW(percentage), screenH(0.5f));

        return this;
    }

    public EUICanvas setScrollBounds(float lowerScrollBound, float upperScrollBound) {
        this.lowerScrollBound = lowerScrollBound;
        this.upperScrollBound = Math.max(lowerScrollBound, upperScrollBound);
        return this;
    }

    protected boolean shouldShowScrollbar() {
        return autoShowScrollbar && upperScrollBound > SCROLL_BAR_THRESHOLD;
    }

    public EUICanvas showScrollbar(boolean showScrollbar) {
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    @Override
    public void updateImpl() {
        if (!EUI.doesActiveElementExist()) {
            if (shouldShowScrollbar()) {
                scrollBar.updateImpl();
                updateScrolling(scrollBar.isDragging);
            }
            else {
                updateScrolling(false);
            }
        }
    }

    protected void updateScrollbarDrag(float newPercent) {
        if (!EUI.doesActiveElementExist()) {
            scrollDelta = MathHelper.valueFromPercentBetween(lowerScrollBound, upperScrollBound, newPercent);
            onScroll(scrollDelta);
        }
    }

    protected void updateScrolling(boolean isDraggingScrollBar) {
        float prevScroll = scrollDelta;
        if (!isDraggingScrollBar) {
            if (draggingScreen) {
                if (InputHelper.isMouseDown && EUI.tryDragging()) {
                    scrollDelta = InputHelper.mY - scrollStart;
                }
                else {
                    draggingScreen = false;
                }
            }
            else {
                if (InputHelper.scrolledDown) {
                    scrollDelta += Settings.SCROLL_SPEED;
                }
                else if (InputHelper.scrolledUp) {
                    scrollDelta -= Settings.SCROLL_SPEED;
                }

                if (canDragScreen && InputHelper.justClickedLeft && EUI.tryDragging()) {
                    draggingScreen = true;
                    scrollStart = InputHelper.mY - scrollDelta;
                }
            }
        }

        if (scrollDelta < lowerScrollBound) {
            scrollDelta = instantSnap ? lowerScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, lowerScrollBound);
        }
        else if (scrollDelta > upperScrollBound) {
            scrollDelta = instantSnap ? upperScrollBound : MathHelper.scrollSnapLerpSpeed(scrollDelta, upperScrollBound);
        }

        if (prevScroll != scrollDelta) {
            scrollBar.scroll(MathHelper.percentFromValueBetween(lowerScrollBound, upperScrollBound, scrollDelta), false);
            onScroll(scrollDelta);
        }
    }
}
