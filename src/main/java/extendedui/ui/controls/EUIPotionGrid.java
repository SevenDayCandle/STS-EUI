package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;

import java.util.ArrayList;

public class EUIPotionGrid extends EUICanvasGrid
{
    protected static final float PAD = scale(80);
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * scale(AbstractRelic.RAW_W)) - (4f * PAD);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    public static final int ROW_SIZE = 10;

    protected ActionT1<AbstractPotion> onPotionClick;
    protected ActionT1<AbstractPotion> onPotionHovered;
    protected ActionT1<AbstractPotion> onPotionRightClick;
    protected ActionT2<SpriteBatch, AbstractPotion> onPotionRender;
    protected float drawX = DRAW_START_X;
    protected float drawTopY = DRAW_START_Y;
    protected int hoveredIndex;
    public boolean shouldEnlargeHovered = true;
    public float padX = PAD;
    public float padY = PAD;
    public ArrayList<PotionInfo> potionGroup;
    public PotionInfo hoveredPotion = null;
    public String message = null;
    public float targetScale = 1;
    public float startingScale = targetScale;

    public EUIPotionGrid()
    {
        this(0.5f, true);
    }

    public EUIPotionGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public EUIPotionGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        super(ROW_SIZE, PAD);
        this.autoShowScrollbar = autoShowScrollbar;
        this.potionGroup = new ArrayList<>();

        setHorizontalAlignment(horizontalAlignment);
    }

    public EUIPotionGrid addPadX(float padX)
    {
        this.padX += padX;

        return this;
    }

    public EUIPotionGrid addPadY(float padY)
    {
        this.padY += padY;

        return this;
    }

    public EUIPotionGrid setOnPotionHover(ActionT1<AbstractPotion> onPotionHovered)
    {
        this.onPotionHovered = onPotionHovered;

        return this;
    }

    public EUIPotionGrid setOnPotionClick(ActionT1<AbstractPotion> onPotionClick)
    {
        this.onPotionClick = onPotionClick;

        return this;
    }

    public EUIPotionGrid setOnPotionRightClick(ActionT1<AbstractPotion> onPotionRightClick)
    {
        this.onPotionRightClick = onPotionRightClick;

        return this;
    }

    public EUIPotionGrid setHorizontalAlignment(float percentage)
    {
        this.drawX = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.setPosition(screenW((percentage < 0.5f) ? 0.05f : 0.9f), screenH(0.5f));

        return this;
    }

    public EUIPotionGrid setVerticalStart(float posY) {
        this.drawTopY = posY;

        return this;
    }

    public void clear()
    {
        this.sizeCache = 0;
        this.hoveredPotion = null;
        this.hoveredIndex = 0;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the potions from any outside potion group given to it
        this.potionGroup = new ArrayList<>();


        refreshOffset();
    }

    public EUIPotionGrid setPotions(Iterable<AbstractPotion> potions)
    {
        potionGroup.clear();
        return addPotions(potions);
    }

    public EUIPotionGrid addPotions(Iterable<AbstractPotion> potions)
    {
        for (AbstractPotion potion : potions)
        {
            addPotion(potion);
        }

        return this;
    }

    public EUIPotionGrid addPotion(AbstractPotion potion)
    {
        potionGroup.add(new PotionInfo(potion));

        return this;
    }

    public EUIPotionGrid removePotion(AbstractPotion potion)
    {
        potionGroup.removeIf(rInfo -> rInfo.potion == potion);

        return this;
    }

    public EUIPotionGrid setPotionScale(float startingScale, float targetScale) {
        this.startingScale = startingScale;
        this.targetScale = targetScale;

        return this;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();

        updatePotions();
        updateNonMouseInput();

        if (hoveredPotion != null && hoveredPotion.potion.hb.hovered)
        {
            if (EUIInputManager.rightClick.isJustPressed() && onPotionRightClick != null)
            {
                onPotionRightClick.invoke(hoveredPotion.potion);
                return;
            }

            if (InputHelper.justClickedLeft)
            {
                hoveredPotion.potion.hb.clickStarted = true;
            }

            if (hoveredPotion.potion.hb.clicked || CInputActionSet.select.isJustPressed())
            {
                hoveredPotion.potion.hb.clicked = false;

                if (onPotionClick != null)
                {
                    onPotionClick.invoke(hoveredPotion.potion);
                }
            }
        }
    }

    protected void updatePotions()
    {
        hoveredPotion = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < potionGroup.size(); i++)
        {
            PotionInfo potion = potionGroup.get(i);
            potion.potion.posX = (DRAW_START_X * drawX) + (column * PAD);
            potion.potion.posY = drawTopY + scrollDelta - (row * padY);
            updateHoverLogic(potion, i);

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    protected void updateHoverLogic(PotionInfo potion, int i)
    {
        potion.potion.update();
        potion.potion.hb.update();
        potion.potion.hb.move(potion.potion.posX, potion.potion.posY);

        if (potion.potion.hb.hovered)
        {
            hoveredPotion = potion;
            hoveredIndex = i;
            if (!shouldEnlargeHovered) {
                potion.potion.scale = targetScale;
            }
        }
    }

    public void forceUpdatePotionPositions()
    {
        int row = 0;
        int column = 0;
        for (PotionInfo potion : potionGroup)
        {
            potion.potion.posX = (DRAW_START_X * drawX) + (column * PAD);
            potion.potion.posY = drawTopY + scrollDelta - (row * padY);
            potion.potion.hb.update();
            potion.potion.hb.move(potion.potion.posX, potion.potion.posY);

            column += 1;
            if (column >= rowSize)
            {
                column = 0;
                row += 1;
            }
        }
    }

    protected void updateNonMouseInput()
    {
        if (EUIInputManager.isUsingNonMouseControl())
        {
            int targetIndex = hoveredIndex;
            if (EUIInputManager.didInputDown())
            {
                targetIndex += rowSize;
            }
            if (EUIInputManager.didInputUp())
            {
                targetIndex -= rowSize;
            }
            if (EUIInputManager.didInputLeft())
            {
                targetIndex -= 1;
            }
            if (EUIInputManager.didInputRight())
            {
                targetIndex += 1;
            }

            if (targetIndex != hoveredIndex)
            {
                targetIndex = MathUtils.clamp(targetIndex, 0, potionGroup.size() - 1);
                PotionInfo potion = potionGroup.get(targetIndex);
                if (potion != null)
                {
                    float distance = getScrollDistance(potion.potion, targetIndex);
                    if (distance != 0)
                    {
                        this.scrollBar.scroll(scrollBar.currentScrollPercent + distance, true);
                    }
                    EUIInputManager.setCursor(potion.potion.hb.cX, distance == 0 ? Settings.HEIGHT - potion.potion.hb.cY : Gdx.input.getY());
                }
            }
        }
    }

    protected float getScrollDistance(AbstractPotion potion, int index)
    {
        if (potion != null)
        {
            float scrollDistance = 1f / getRowCount();
            if (potion.posY > drawTopY)
            {
                return -scrollDistance;
            }
            else if (potion.posY < 0)
            {
                return scrollDistance;
            }
        }
        return 0;
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);

        renderPotions(sb);

        if (message != null)
        {
            FontHelper.renderDeckViewTip(sb, message, scale(96f), Settings.CREAM_COLOR);
        }
    }

    protected void renderPotions(SpriteBatch sb) {
        for (PotionInfo potionInfo : potionGroup)
        {
            renderPotion(sb, potionInfo);
        }
    }

    protected void renderPotion(SpriteBatch sb, PotionInfo potion)
    {
        potion.potion.render(sb);

        if (onPotionRender != null)
        {
            onPotionRender.invoke(sb, potion.potion);
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
    public boolean isHovered() {return super.isHovered() || hoveredPotion != null;}

    @Override
    public int currentSize()
    {
        return potionGroup.size();
    }

    public int getRowCount() {
        return (potionGroup.size() - 1) / rowSize;
    }

    public static class PotionInfo
    {
        public final AbstractPotion potion;
        public final AbstractCard.CardColor potionColor;

        public PotionInfo(AbstractPotion potion)
        {
            this.potion = potion;
            this.potionColor = EUIGameUtils.getPotionColor(potion.ID);
        }
    }
}
