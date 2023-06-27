package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.ItemGroup;

import java.util.Collection;

public abstract class EUIItemGrid<T> extends EUICanvasGrid {
    protected static final float PAD = scale(80);
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * scale(AbstractRelic.RAW_W)) - (4f * PAD);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    public static final int ROW_SIZE = 10;
    public static final int LERP_SPEED = 8;
    protected ActionT1<T> onClick;
    protected ActionT1<T> onHover;
    protected ActionT1<T> onRightClick;
    protected ActionT2<SpriteBatch, T> onRender;
    protected float drawX = DRAW_START_X;
    protected float drawTopY = DRAW_START_Y;
    protected int hoveredIndex;
    public float padX = PAD;
    public ItemGroup<T> group;
    public T hovered = null;
    public String message = null;
    public float targetScale = 1;
    public float startingScale = targetScale;
    public float hoveredScale = 1.25f;

    public EUIItemGrid() {
        this(0.5f, true);
    }

    public EUIItemGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(ROW_SIZE, PAD);
        this.autoShowScrollbar = autoShowScrollbar;
        this.group = new ItemGroup<>();

        setHorizontalAlignment(horizontalAlignment);
    }

    public EUIItemGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public EUIItemGrid<T> add(T item) {
        group.add(item);

        return this;
    }

    public EUIItemGrid<T> add(Collection<? extends T> items) {
        group.addAll(items);

        return this;
    }

    public <V> EUIItemGrid<T> add(Iterable<? extends V> items, FuncT1<T, V> makeItem) {
        for (V item : items) {
            group.add(makeItem.invoke(item));
        }

        return this;
    }

    public EUIItemGrid<T> addPadX(float padX) {
        this.padX += padX;

        return this;
    }

    public EUIItemGrid<T> addPadY(float padY) {
        this.padY += padY;

        return this;
    }

    public EUIItemGrid<T> canDragScreen(boolean canDrag) {
        this.canDragScreen = canDrag;

        return this;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() || hovered != null;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        renderItems(sb);

        if (message != null) {
            FontHelper.renderDeckViewTip(sb, message, scale(96f), Settings.CREAM_COLOR);
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();

        updateRelics();
        updateNonMouseInput();
        updateClickLogic();
    }

    public void clear() {
        this.sizeCache = 0;
        this.hovered = null;
        this.hoveredIndex = 0;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the items from any outside item group given to it
        this.group = new ItemGroup<>();


        refreshOffset();
    }

    public void forceUpdatePositions() {
        int row = 0;
        int column = 0;
        for (T item : group.group) {
            forceUpdateItemPosition(item,(DRAW_START_X * drawX) + (column * PAD), drawTopY + scrollDelta - (row * padY));

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    public int getRowCount() {
        return (group.size() - 1) / rowSize;
    }

    @Override
    public void refreshOffset() {
        sizeCache = currentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize * 2) {
            int offset = ((sizeCache / rowSize) - ((sizeCache % rowSize > 0) ? 1 : 2));
            upperScrollBound += this.padY * offset;
        }
    }

    @Override
    public int currentSize() {
        return group.size();
    }

    public EUIItemGrid<T> remove(T item) {
        group.group.removeIf(rInfo -> rInfo == item);

        return this;
    }

    protected void renderItems(SpriteBatch sb) {
        for (T itemInfo : group.group) {
            renderItem(sb, itemInfo);
            if (onRender != null) {
                onRender.invoke(sb, itemInfo);
            }
        }
    }

    public EUIItemGrid<T> setHorizontalAlignment(float percentage) {
        this.drawX = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.setPosition(screenW((percentage < 0.5f) ? 0.05f : 0.9f), screenH(0.5f));

        return this;
    }

    public EUIItemGrid<T> setItemScale(float targetScale) {
        return setItemScale(targetScale, targetScale, targetScale * 1.25f);
    }

    public EUIItemGrid<T> setItemScale(float startingScale, float targetScale) {
        return setItemScale(startingScale, targetScale, targetScale * 1.25f);
    }

    public EUIItemGrid<T> setItemScale(float startingScale, float targetScale, float hoveredScale) {
        this.startingScale = startingScale;
        this.targetScale = targetScale;
        this.hoveredScale = hoveredScale;

        return this;
    }

    public EUIItemGrid<T> setItems(Collection<? extends T> items) {
        group.clear();
        return add(items);
    }

    public <V> EUIItemGrid<T> setItems(Iterable<? extends V> items, FuncT1<T, V> makeItem) {
        group.clear();
        return add(items, makeItem);
    }

    public EUIItemGrid<T> setOnClick(ActionT1<T> onClick) {
        this.onClick = onClick;

        return this;
    }

    public EUIItemGrid<T> setOnHover(ActionT1<T> onRelicHovered) {
        this.onHover = onRelicHovered;

        return this;
    }

    public EUIItemGrid<T> setOnRightClick(ActionT1<T> onRightClick) {
        this.onRightClick = onRightClick;

        return this;
    }

    public EUIItemGrid<T> setVerticalStart(float posY) {
        this.drawTopY = posY;

        return this;
    }

    protected void updateClickLogic() {
        if (hovered != null) {
            Hitbox hb = getHitbox(hovered);
            if (EUIInputManager.rightClick.isJustPressed() && onRightClick != null) {
                onRightClick.invoke(hovered);
                return;
            }

            if (InputHelper.justClickedLeft && !EUITourTooltip.shouldBlockInteract(hb)) {
                hb.clickStarted = true;
            }

            if (hb.clicked || CInputActionSet.select.isJustPressed()) {
                hb.clicked = false;

                if (onClick != null) {
                    onClick.invoke(hovered);
                }
            }
        }
    }

    protected void updateNonMouseInput() {
        if (EUIInputManager.isUsingNonMouseControl()) {
            int targetIndex = hoveredIndex;
            if (EUIInputManager.didInputDown()) {
                targetIndex += rowSize;
            }
            if (EUIInputManager.didInputUp()) {
                targetIndex -= rowSize;
            }
            if (EUIInputManager.didInputLeft()) {
                targetIndex -= 1;
            }
            if (EUIInputManager.didInputRight()) {
                targetIndex += 1;
            }

            if (targetIndex != hoveredIndex) {
                targetIndex = MathUtils.clamp(targetIndex, 0, group.size() - 1);
                T item = group.group.get(targetIndex);
                if (item != null) {
                    float distance = getScrollDistance(item, targetIndex);
                    if (distance != 0) {
                        this.scrollBar.scroll(scrollBar.currentScrollPercent + distance, true);
                    }
                    Hitbox hb = getHitbox(item);
                    EUIInputManager.setCursor(hb.cX, distance == 0 ? Settings.HEIGHT - hb.cY : Gdx.input.getY());
                }
            }
        }
    }

    protected void updateRelics() {
        hovered = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < group.size(); i++) {
            T item = group.group.get(i);
            updateItemPosition(item, (DRAW_START_X * drawX) + (column * PAD), drawTopY + scrollDelta - (row * padY));
            updateHoverLogic(item, i);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
    }

    abstract protected float getScrollDistance(T item, int index);

    abstract public void updateItemPosition(T item, float x, float y);

    abstract public Hitbox getHitbox(T item);

    abstract public void forceUpdateItemPosition(T item, float x, float y);

    abstract protected void updateHoverLogic(T item, int i);

    abstract protected void renderItem(SpriteBatch sb, T item);
}
