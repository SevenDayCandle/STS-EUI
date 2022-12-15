package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CacheableCard;

import java.util.HashMap;

public class EUICardGrid extends EUICanvasGrid
{
    protected static final float CARD_SCALE = 0.75f;
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * AbstractCard.IMG_WIDTH) - (4f * Settings.CARD_VIEW_PAD_X);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    public static final int ROW_SIZE = 5;

    protected ActionT1<AbstractCard> onCardClick;
    protected ActionT1<AbstractCard> onCardHovered;
    protected ActionT1<AbstractCard> onCardRightClick;
    protected ActionT2<SpriteBatch, AbstractCard> onCardRender;
    protected HashMap<AbstractCard, AbstractCard> upgradeCards;
    protected float drawTopY = DRAW_START_Y;
    protected float drawX;
    protected int hoveredIndex;
    public AbstractCard hoveredCard = null;
    public CardGroup cards;
    public String message = null;
    public boolean canRenderUpgrades = false;
    public boolean shouldEnlargeHovered = true;
    public float padX = PAD_X;
    public float padY = PAD_Y;
    public float targetScale = CARD_SCALE;
    public float startingScale = targetScale;

    public EUICardGrid()
    {
        this(0.5f, true);
    }

    public EUICardGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public EUICardGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        super(ROW_SIZE, PAD_Y);
        this.cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.upgradeCards = new HashMap<>();
        this.autoShowScrollbar = autoShowScrollbar;

        setHorizontalAlignment(horizontalAlignment);
    }

    public EUICardGrid addPadX(float padX)
    {
        this.padX += padX;

        return this;
    }

    public EUICardGrid addPadY(float padY)
    {
        this.padY += padY;

        return this;
    }

    public EUICardGrid setOnCardHover(ActionT1<AbstractCard> onCardHovered)
    {
        this.onCardHovered = onCardHovered;

        return this;
    }

    public EUICardGrid setOnCardClick(ActionT1<AbstractCard> onCardClicked)
    {
        this.onCardClick = onCardClicked;

        return this;
    }

    public EUICardGrid setOnCardRightClick(ActionT1<AbstractCard> onCardRightClicked)
    {
        this.onCardRightClick = onCardRightClicked;

        return this;
    }

    public EUICardGrid setOnCardRender(ActionT2<SpriteBatch, AbstractCard> onCardRender)
    {
        this.onCardRender = onCardRender;

        return this;
    }

    public EUICardGrid setOptions(boolean canDrag, boolean shouldEnlargeHovered, boolean showScrollbar)
    {
        this.canDragScreen = canDrag;
        this.shouldEnlargeHovered = shouldEnlargeHovered;
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    public EUICardGrid setEnlargeOnHover(boolean shouldEnlargeHovered)
    {
        this.shouldEnlargeHovered = shouldEnlargeHovered;

        return this;
    }

    public EUICardGrid showScrollbar(boolean showScrollbar)
    {
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    public EUICardGrid canDragScreen(boolean canDrag)
    {
        this.canDragScreen = canDrag;

        return this;
    }

    public EUICardGrid canRenderUpgrades(boolean canRenderUpgrades)
    {
        this.canRenderUpgrades = canRenderUpgrades;

        return this;
    }

    public EUICardGrid setHorizontalAlignment(float percentage)
    {
        this.drawX = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.setPosition(screenW((percentage < 0.5f) ? 0.05f : 0.9f), screenH(0.5f));

        return this;
    }

    public EUICardGrid setVerticalStart(float posY) {
        this.drawTopY = posY;

        return this;
    }

    public EUICardGrid setCardScale(float startingScale, float targetScale) {
        this.startingScale = startingScale;
        this.targetScale = targetScale;

        return this;
    }

    public void clear()
    {
        this.sizeCache = 0;
        this.hoveredCard = null;
        this.hoveredIndex = 0;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the cards from any outside card group given to it
        this.cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.upgradeCards.clear();


        refreshOffset();
    }

    public EUICardGrid setCardGroup(CardGroup cardGroup) {
        this.upgradeCards.clear();
        this.cards = cardGroup;
        for (AbstractCard c : cardGroup.group) {
            c.drawScale = startingScale;
            c.targetDrawScale = targetScale;
            addUpgrade(c);
        }
        return this;
    }

    public EUICardGrid addCards(Iterable<AbstractCard> cards)
    {
        for (AbstractCard card : cards)
        {
            addCard(card);
        }

        return this;
    }

    public EUICardGrid addCard(AbstractCard card)
    {
        card.drawScale = startingScale;
        card.targetDrawScale = targetScale;
        card.setAngle(0, true);
        card.lighten(true);
        cards.addToTop(card);

        return this;
    }

    public EUICardGrid removeCard(AbstractCard card)
    {
        cards.removeCard(card);

        return this;
    }

    protected void addUpgrade(AbstractCard card) {
        if (canRenderUpgrades) {
            AbstractCard copy;
            if (card instanceof CacheableCard) {
                copy = ((CacheableCard) card).getCachedUpgrade();
            }
            else {
                copy = card.makeSameInstanceOf();
                copy.upgrade();
                copy.displayUpgrades();
            }
            upgradeCards.put(card, copy);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);

        renderCards(sb);

        if (hoveredCard != null)
        {
            hoveredCard.renderHoverShadow(sb);
            renderCard(sb, hoveredCard);
            hoveredCard.renderCardTip(sb);
        }

        if (message != null)
        {
            FontHelper.renderDeckViewTip(sb, message, scale(96f), Settings.CREAM_COLOR);
        }
    }

    protected void renderCards(SpriteBatch sb) {
        for (int i = 0; i < cards.group.size(); i++)
        {
            AbstractCard card = cards.group.get(i);
            if (card != hoveredCard)
            {
                renderCard(sb, card);
            }
        }
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();

        updateCards();
        updateNonMouseInput();

        if (hoveredCard != null && hoveredCard.hb.hovered)
        {
            if (EUIInputManager.rightClick.isJustPressed() && onCardRightClick != null)
            {
                onCardRightClick.invoke(hoveredCard);
                return;
            }

            if (InputHelper.justClickedLeft)
            {
                hoveredCard.hb.clickStarted = true;
            }

            if (hoveredCard.hb.clicked || CInputActionSet.select.isJustPressed())
            {
                hoveredCard.hb.clicked = false;

                if (onCardClick != null)
                {
                    onCardClick.invoke(hoveredCard);
                }
            }
        }
    }

    protected void updateCards()
    {
        hoveredCard = null;

        int row = 0;
        int column = 0;
        for (int i = 0; i < cards.group.size(); i++)
        {
            AbstractCard card = cards.group.get(i);
            card.target_x = (DRAW_START_X * drawX) + (column * padX);
            card.target_y = drawTopY + scrollDelta - (row * padY);
            card.fadingOut = false;
            card.update();
            card.updateHoverLogic();

            if (card.hb.hovered)
            {
                hoveredCard = card;
                hoveredIndex = i;
                if (!shouldEnlargeHovered) {
                    card.drawScale = card.targetDrawScale = targetScale;
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

    public void forceUpdateCardPositions()
    {
        int row = 0;
        int column = 0;
        for (int i = 0; i < cards.group.size(); i++)
        {
            AbstractCard card = cards.group.get(i);
            card.current_x = card.target_x = (DRAW_START_X * drawX) + (column * padX);
            card.current_y = card.target_y = drawTopY + scrollDelta - (row * padY);
            card.drawScale = card.targetDrawScale = targetScale;
            card.hb.move(card.current_x, card.current_y);

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
                targetIndex = MathUtils.clamp(targetIndex, 0, cards.group.size() - 1);
                AbstractCard card = cards.group.get(targetIndex);
                if (card != null)
                {
                    float distance = getScrollDistance(card, targetIndex);
                    if (distance != 0)
                    {
                        this.scrollBar.scroll(scrollBar.currentScrollPercent + distance, true);
                    }
                    EUIInputManager.setCursor(card.hb.cX, distance == 0 ? Settings.HEIGHT - card.hb.cY : Gdx.input.getY());
                }
            }
        }
    }

    protected float getScrollDistance(AbstractCard card, int index)
    {
        if (card != null)
        {
            float scrollDistance = 1f / getRowCount();
            if (card.target_y > drawTopY)
            {
                return -scrollDistance;
            }
            else if (card.target_y < 0)
            {
                return scrollDistance;
            }
        }
        return 0;
    }

    protected void renderCard(SpriteBatch sb, AbstractCard card)
    {
        // renderInLibrary continually creates copies of upgraded cards -_-
        // So we use a cache of the upgraded cards to show in compendium screens
        if (canRenderUpgrades && SingleCardViewPopup.isViewingUpgrade) {
            AbstractCard upgrade = upgradeCards.get(card);
            if (upgrade != null) {
                upgrade.current_x = card.current_x;
                upgrade.current_y = card.current_y;
                upgrade.drawScale = card.drawScale;
                upgrade.render(sb);
            }
        }
        else {
            card.render(sb);
        }

        if (onCardRender != null)
        {
            onCardRender.invoke(sb, card);
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
    public boolean isHovered() {return super.isHovered() || hoveredCard != null;}

    @Override
    public int currentSize()
    {
        return cards.size();
    }

    public int getRowCount() {
        return (this.cards.size() - 1) / rowSize;
    }
}
