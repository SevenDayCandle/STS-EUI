package stseffekseer.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import stseffekseer.EUI;
import stseffekseer.EUIInputManager;
import stseffekseer.interfaces.delegates.ActionT1;
import stseffekseer.interfaces.delegates.ActionT2;
import stseffekseer.ui.GUI_Base;
import stseffekseer.ui.hitboxes.AdvancedHitbox;

import java.util.Collection;

public class GUI_CardGrid extends GUI_Base
{
    private static final float DRAW_START_X = (Settings.WIDTH - (5f * AbstractCard.IMG_WIDTH * 0.75f) - (4f * Settings.CARD_VIEW_PAD_X) + AbstractCard.IMG_WIDTH * 0.75f);
    private static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    private static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    private static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    private static final float SCROLL_BAR_THRESHOLD = 500f * Settings.scale;
    public static final int ROW_SIZE = 5;

    public final GUI_VerticalScrollBar scrollBar;
    public CardGroup cards;
    public boolean autoShowScrollbar;
    public boolean draggingScreen;
    public boolean shouldEnlargeHovered = true;
    public AbstractCard hoveredCard = null;
    public String message = null;
    public float pad_x = PAD_X;
    public float pad_y = PAD_Y;

    protected ActionT1<AbstractCard> onCardClick;
    protected ActionT1<AbstractCard> onCardRightClick;
    protected ActionT1<AbstractCard> onCardHovered;
    protected ActionT2<SpriteBatch, AbstractCard> onCardRender;
    protected boolean canDragScreen = true;
    protected float draw_x;
    protected float lowerScrollBound = -Settings.DEFAULT_SCROLL_LIMIT;
    protected float upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;
    protected float scrollStart;
    protected float scrollDelta;
    protected int deckSizeCache;

    public GUI_CardGrid()
    {
        this(0.5f, true);
    }

    public GUI_CardGrid(float horizontalAlignment)
    {
        this(horizontalAlignment, true);
    }

    public GUI_CardGrid(float horizontalAlignment, boolean autoShowScrollbar)
    {
        this.cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.scrollBar = new GUI_VerticalScrollBar(new AdvancedHitbox(ScreenW(0.03f), ScreenH(0.7f)))
                .SetOnScroll(this::OnScroll);
        this.autoShowScrollbar = autoShowScrollbar;

        SetHorizontalAlignment(horizontalAlignment);
    }

    public GUI_CardGrid AddPadX(float padX)
    {
        this.pad_x += padX;

        return this;
    }

    public GUI_CardGrid AddPadY(float padY)
    {
        this.pad_y += padY;

        return this;
    }

    public GUI_CardGrid SetOnCardHover(ActionT1<AbstractCard> onCardHovered)
    {
        this.onCardHovered = onCardHovered;

        return this;
    }

    public GUI_CardGrid SetOnCardClick(ActionT1<AbstractCard> onCardClicked)
    {
        this.onCardClick = onCardClicked;

        return this;
    }

    public GUI_CardGrid SetOnCardRightClick(ActionT1<AbstractCard> onCardRightClicked)
    {
        this.onCardRightClick = onCardRightClicked;

        return this;
    }

    public GUI_CardGrid SetOnCardRender(ActionT2<SpriteBatch, AbstractCard> onCardRender)
    {
        this.onCardRender = onCardRender;

        return this;
    }

    public GUI_CardGrid SetOptions(boolean canDrag, boolean shouldEnlargeHovered, boolean showScrollbar)
    {
        this.canDragScreen = canDrag;
        this.shouldEnlargeHovered = shouldEnlargeHovered;
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    public GUI_CardGrid SetEnlargeOnHover(boolean shouldEnlargeHovered)
    {
        this.shouldEnlargeHovered = shouldEnlargeHovered;

        return this;
    }

    public GUI_CardGrid ShowScrollbar(boolean showScrollbar)
    {
        this.autoShowScrollbar = showScrollbar;

        return this;
    }

    public GUI_CardGrid CanDragScreen(boolean canDrag)
    {
        this.canDragScreen = canDrag;

        return this;
    }

    public GUI_CardGrid SetHorizontalAlignment(float percentage)
    {
        this.draw_x = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.SetPosition(ScreenW((percentage < 0.5f) ? 0.05f : 0.9f), ScreenH(0.5f));

        return this;
    }

    public void Clear()
    {
        this.deckSizeCache = 0;
        this.hoveredCard = null;
        this.scrollDelta = 0f;
        this.scrollStart = 0f;
        this.draggingScreen = false;
        this.message = null;
        // Unlink the cards from any outside card group given to it
        this.cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        RefreshDeckSize();
    }

    public GUI_CardGrid SetCardGroup(CardGroup cardGroup) {
        this.cards = cardGroup;
        return this;
    }

    public GUI_CardGrid AddCards(Collection<AbstractCard> cards)
    {
        for (AbstractCard card : cards)
        {
            AddCard(card);
        }

        return this;
    }

    public GUI_CardGrid AddCard(AbstractCard card)
    {
        card.targetDrawScale = card.drawScale = 0.75f;
        card.setAngle(0, true);
        card.lighten(true);
        cards.addToTop(card);

        return this;
    }

    public GUI_CardGrid RemoveCard(AbstractCard card)
    {
        cards.removeCard(card);

        return this;
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        if (ShouldShowScrollbar())
        {
            scrollBar.Render(sb);
        }

        for (AbstractCard card : cards.group)
        {
            if (card != hoveredCard)
            {
                RenderCard(sb, card);
            }
        }

        if (hoveredCard != null)
        {
            hoveredCard.renderHoverShadow(sb);
            RenderCard(sb, hoveredCard);
            hoveredCard.renderCardTip(sb);
        }

        if (message != null)
        {
            FontHelper.renderDeckViewTip(sb, message, Scale(96f), Settings.CREAM_COLOR);
        }
    }

    @Override
    public void Update()
    {
        if (ShouldShowScrollbar())
        {
            scrollBar.Update();
            UpdateScrolling(scrollBar.isDragging);
        }
        else
        {
            UpdateScrolling(false);
        }

        UpdateCards();

        if (hoveredCard != null && EUI.TryHover(hoveredCard.hb))
        {
            if (EUIInputManager.RightClick.IsJustPressed() && onCardRightClick != null)
            {
                onCardRightClick.Invoke(hoveredCard);
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
                    onCardClick.Invoke(hoveredCard);
                }
            }
        }
    }

    protected void UpdateCards()
    {
        hoveredCard = null;

        int row = 0;
        int column = 0;
        for (AbstractCard card : cards.group)
        {
            card.target_x = (DRAW_START_X * draw_x) + (column * PAD_X);
            card.target_y = DRAW_START_Y + scrollDelta - (row * pad_y);
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

    protected void RenderCard(SpriteBatch sb, AbstractCard card)
    {
        card.render(sb);

        if (onCardRender != null)
        {
            onCardRender.Invoke(sb, card);
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

        if (deckSizeCache != cards.size())
        {
            RefreshDeckSize();
        }

        if (scrollDelta < lowerScrollBound)
        {
            scrollDelta = MathHelper.scrollSnapLerpSpeed(scrollDelta, lowerScrollBound);
        }
        else if (scrollDelta > upperScrollBound)
        {
            scrollDelta = MathHelper.scrollSnapLerpSpeed(scrollDelta, upperScrollBound);
        }

        scrollBar.Scroll(MathHelper.percentFromValueBetween(lowerScrollBound, upperScrollBound, scrollDelta), false);
    }

    public void RefreshDeckSize()
    {
        deckSizeCache = cards.size();
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT;

        if (deckSizeCache > 10)
        {
            int offset = ((deckSizeCache / ROW_SIZE) - ((deckSizeCache % ROW_SIZE > 0) ? 1 : 2));
            upperScrollBound += PAD_Y * offset;
        }
    }

    protected void OnScroll(float newPercent)
    {
        scrollDelta = MathHelper.valueFromPercentBetween(lowerScrollBound, upperScrollBound, newPercent);
    }

    protected boolean ShouldShowScrollbar()
    {
        return autoShowScrollbar && upperScrollBound > SCROLL_BAR_THRESHOLD;
    }
}
