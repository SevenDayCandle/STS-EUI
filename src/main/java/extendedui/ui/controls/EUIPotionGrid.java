package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.utilities.PotionGroup;

import java.util.ArrayList;

public class EUIPotionGrid extends EUICanvasGrid {
    public static final int ROW_SIZE = 12;
    public static final int LERP_SPEED = 8;
    protected static final float PAD = scale(64);
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * scale(AbstractRelic.RAW_W)) - (4f * PAD);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    public boolean shouldEnlargeHovered = true;
    public float padX = PAD;
    public float padY = PAD;
    public PotionGroup potionGroup;
    public PotionGroup.PotionInfo hoveredPotion = null;
    public String message = null;
    public float targetScale = 1;
    public float startingScale = targetScale;
    protected ActionT1<AbstractPotion> onPotionClick;
    protected ActionT1<AbstractPotion> onPotionHovered;
    protected ActionT1<AbstractPotion> onPotionRightClick;
    protected ActionT2<SpriteBatch, AbstractPotion> onPotionRender;
    protected float drawX = DRAW_START_X;
    protected float drawTopY = DRAW_START_Y;
    protected int hoveredIndex;

    public EUIPotionGrid() {
        this(0.5f, true);
    }

    public EUIPotionGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(ROW_SIZE, PAD);
        this.autoShowScrollbar = autoShowScrollbar;
        this.potionGroup = new PotionGroup();

        setHorizontalAlignment(horizontalAlignment);
    }

    public EUIPotionGrid setHorizontalAlignment(float percentage) {
        this.drawX = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.setPosition(screenW((percentage < 0.5f) ? 0.05f : 0.9f), screenH(0.5f));

        return this;
    }

    public EUIPotionGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public EUIPotionGrid addPadX(float padX) {
        this.padX += padX;

        return this;
    }

    public EUIPotionGrid addPadY(float padY) {
        this.padY += padY;

        return this;
    }

    public void clear() {
        this.sizeCache = 0;
        this.hoveredPotion = null;
        this.hoveredIndex = 0;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the potions from any outside potion group given to it
        this.potionGroup = new PotionGroup();


        refreshOffset();
    }

    @Override
    public int currentSize() {
        return potionGroup.size();
    }

    @Override
    public void refreshOffset() {
        sizeCache = currentSize();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;

        if (sizeCache > rowSize * 2) {
            int offset = ((sizeCache / rowSize) - ((sizeCache % rowSize > 0) ? 1 : 2));
            upperScrollBound += yPadding * offset;
        }
    }

    public void forceUpdatePotionPositions() {
        int row = 0;
        int column = 0;
        for (PotionGroup.PotionInfo potion : potionGroup) {
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
    public boolean isHovered() {
        return super.isHovered() || hoveredPotion != null;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();

        updatePotions();
        updateNonMouseInput();

        if (hoveredPotion != null && hoveredPotion.potion.hb.hovered) {
            if (EUIInputManager.rightClick.isJustPressed() && onPotionRightClick != null) {
                onPotionRightClick.invoke(hoveredPotion.potion);
                return;
            }

            if (InputHelper.justClickedLeft) {
                hoveredPotion.potion.hb.clickStarted = true;
            }

            if (hoveredPotion.potion.hb.clicked || CInputActionSet.select.isJustPressed()) {
                hoveredPotion.potion.hb.clicked = false;

                if (onPotionClick != null) {
                    onPotionClick.invoke(hoveredPotion.potion);
                }
            }
        }
    }

    protected void updatePotions() {
        hoveredPotion = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < potionGroup.size(); i++) {
            PotionGroup.PotionInfo potion = potionGroup.group.get(i);
            float targetX = (DRAW_START_X * drawX) + (column * PAD);
            float targetY = drawTopY + scrollDelta - (row * padY);
            potion.potion.posX = EUIUtils.lerpSnap(potion.potion.posX, targetX, LERP_SPEED);
            potion.potion.posY = EUIUtils.lerpSnap(potion.potion.posY, targetY, LERP_SPEED);
            updateHoverLogic(potion, i);

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
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
                targetIndex = MathUtils.clamp(targetIndex, 0, potionGroup.size() - 1);
                PotionGroup.PotionInfo potion = potionGroup.group.get(targetIndex);
                if (potion != null) {
                    float distance = getScrollDistance(potion.potion, targetIndex);
                    if (distance != 0) {
                        this.scrollBar.scroll(scrollBar.currentScrollPercent + distance, true);
                    }
                    EUIInputManager.setCursor(potion.potion.hb.cX, distance == 0 ? Settings.HEIGHT - potion.potion.hb.cY : Gdx.input.getY());
                }
            }
        }
    }

    protected void updateHoverLogic(PotionGroup.PotionInfo potion, int i) {
        potion.potion.hb.update();
        potion.potion.hb.move(potion.potion.posX, potion.potion.posY);

        if (potion.potion.hb.hovered) {
            hoveredPotion = potion;
            hoveredIndex = i;
            if (!shouldEnlargeHovered) {
                potion.potion.scale = targetScale;
            }
        }
    }

    protected float getScrollDistance(AbstractPotion potion, int index) {
        if (potion != null) {
            float scrollDistance = 1f / getRowCount();
            if (potion.posY > drawTopY) {
                return -scrollDistance;
            }
            else if (potion.posY < 0) {
                return scrollDistance;
            }
        }
        return 0;
    }

    public int getRowCount() {
        return (potionGroup.size() - 1) / rowSize;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        renderPotions(sb);

        if (hoveredPotion != null) {
            EUIGameUtils.renderPotionTip(hoveredPotion.potion);
        }

        if (message != null) {
            FontHelper.renderDeckViewTip(sb, message, scale(96f), Settings.CREAM_COLOR);
        }
    }

    protected void renderPotions(SpriteBatch sb) {
        for (PotionGroup.PotionInfo potionInfo : potionGroup) {
            renderPotion(sb, potionInfo);
        }
    }

    protected void renderPotion(SpriteBatch sb, PotionGroup.PotionInfo potion) {
        potion.potion.render(sb);

        if (onPotionRender != null) {
            onPotionRender.invoke(sb, potion.potion);
        }
    }

    public EUIPotionGrid removePotion(AbstractPotion potion) {
        potionGroup.group.removeIf(rInfo -> rInfo.potion == potion);

        return this;
    }

    public EUIPotionGrid setOnPotionClick(ActionT1<AbstractPotion> onPotionClick) {
        this.onPotionClick = onPotionClick;

        return this;
    }

    public EUIPotionGrid setOnPotionHover(ActionT1<AbstractPotion> onPotionHovered) {
        this.onPotionHovered = onPotionHovered;

        return this;
    }

    public EUIPotionGrid setOnPotionRightClick(ActionT1<AbstractPotion> onPotionRightClick) {
        this.onPotionRightClick = onPotionRightClick;

        return this;
    }

    public EUIPotionGrid setPotionScale(float startingScale, float targetScale) {
        this.startingScale = startingScale;
        this.targetScale = targetScale;

        return this;
    }

    public EUIPotionGrid setPotions(Iterable<AbstractPotion> potions) {
        potionGroup.clear();
        return addPotions(potions);
    }

    public EUIPotionGrid addPotions(Iterable<AbstractPotion> potions) {
        for (AbstractPotion potion : potions) {
            addPotion(potion);
        }

        return this;
    }

    public EUIPotionGrid addPotion(AbstractPotion potion) {
        potionGroup.add(new PotionGroup.PotionInfo(potion));

        return this;
    }

    public EUIPotionGrid setVerticalStart(float posY) {
        this.drawTopY = posY;

        return this;
    }

}
