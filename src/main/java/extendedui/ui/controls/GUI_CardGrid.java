package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.ActionT2;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.interfaces.markers.CacheableCard;

import java.util.Collection;
import java.util.HashMap;

// TODO controller/keyboard support
public class GUI_CardGrid extends GUI_CanvasGrid
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
    protected float draw_top_y = DRAW_START_Y;
    protected float draw_x;
    protected int hoveredIndex;
    public AbstractCard hoveredCard = null;
    public CardGroup cards;
    public String message = null;
    public boolean canRenderUpgrades = false;
    public boolean shouldEnlargeHovered = true;
    public float pad_x = PAD_X;
    public float pad_y = PAD_Y;
    public float target_scale = CARD_SCALE;
    public float starting_scale = target_scale;

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
        super(ROW_SIZE, PAD_Y);
        this.cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.upgradeCards = new HashMap<>();
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

    public GUI_CardGrid CanRenderUpgrades(boolean canRenderUpgrades)
    {
        this.canRenderUpgrades = canRenderUpgrades;

        return this;
    }

    public GUI_CardGrid SetHorizontalAlignment(float percentage)
    {
        this.draw_x = MathUtils.clamp(percentage, 0.35f, 0.55f);
        this.scrollBar.SetPosition(ScreenW((percentage < 0.5f) ? 0.05f : 0.9f), ScreenH(0.5f));

        return this;
    }

    public GUI_CardGrid SetVerticalStart(float posY) {
        this.draw_top_y = posY;

        return this;
    }

    public GUI_CardGrid SetCardScale(float startingScale, float targetScale) {
        this.starting_scale = startingScale;
        this.target_scale = targetScale;

        return this;
    }

    public void Clear()
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


        RefreshOffset();
    }

    public GUI_CardGrid SetCardGroup(CardGroup cardGroup) {
        this.upgradeCards.clear();
        this.cards = cardGroup;
        for (AbstractCard c : cardGroup.group) {
            c.drawScale = starting_scale;
            c.targetDrawScale = target_scale;
            AddUpgrade(c);
        }
        return this;
    }

    public GUI_CardGrid AddCards(Iterable<AbstractCard> cards)
    {
        for (AbstractCard card : cards)
        {
            AddCard(card);
        }

        return this;
    }

    public GUI_CardGrid AddCard(AbstractCard card)
    {
        card.drawScale = starting_scale;
        card.targetDrawScale = target_scale;
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

    protected void AddUpgrade(AbstractCard card) {
        if (canRenderUpgrades) {
            AbstractCard copy;
            if (card instanceof CacheableCard) {
                copy = ((CacheableCard) card).GetCachedUpgrade();
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
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);

        RenderCards(sb);

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

    protected void RenderCards(SpriteBatch sb) {
        for (int i = 0; i < cards.group.size(); i++)
        {
            AbstractCard card = cards.group.get(i);
            if (card != hoveredCard)
            {
                RenderCard(sb, card);
            }
        }
    }

    @Override
    public void Update()
    {
        super.Update();

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
        for (int i = 0; i < cards.group.size(); i++)
        {
            AbstractCard card = cards.group.get(i);
            card.target_x = (DRAW_START_X * draw_x) + (column * pad_x);
            card.target_y = draw_top_y + scrollDelta - (row * pad_y);
            card.fadingOut = false;
            card.update();
            card.updateHoverLogic();

            if (card.hb.hovered)
            {
                hoveredCard = card;
                hoveredIndex = i;
                if (!shouldEnlargeHovered) {
                    card.drawScale = card.targetDrawScale = target_scale;
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

    protected void RenderCard(SpriteBatch sb, AbstractCard card)
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
            onCardRender.Invoke(sb, card);
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
    public boolean IsHovered() {return super.IsHovered() || hoveredCard != null;}

    @Override
    public int CurrentSize()
    {
        return cards.size();
    }
}
