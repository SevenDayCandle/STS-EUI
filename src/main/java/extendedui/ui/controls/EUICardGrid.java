package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CacheableCard;

import java.util.Collection;
import java.util.HashMap;

public class EUICardGrid extends EUIItemGrid<AbstractCard> {
    protected static final float CARD_SCALE = 0.75f;
    protected static final float DRAW_START_X = Settings.WIDTH - (3f * AbstractCard.IMG_WIDTH) - (4f * Settings.CARD_VIEW_PAD_X);
    protected static final float DRAW_START_Y = (float) Settings.HEIGHT * 0.7f;
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = AbstractCard.IMG_HEIGHT * 0.75f + Settings.CARD_VIEW_PAD_Y;
    protected static final int ROW_SIZE = 5;
    protected final HashMap<AbstractCard, AbstractCard> upgradeCards;
    public boolean canRenderUpgrades = false;

    public EUICardGrid() {
        this(0.355f, true);
    }

    public EUICardGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public EUICardGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(ROW_SIZE, PAD_X, PAD_Y, horizontalAlignment, autoShowScrollbar);
        setItemScale(0.75f, 0.75f, 1f);
        this.upgradeCards = new HashMap<>();
    }

    public EUICardGrid add(AbstractCard card) {
        super.add(card);
        addImpl(card);

        return this;
    }

    public EUICardGrid add(Collection<? extends AbstractCard> items) {
        super.add(items);
        for (AbstractCard i : items) {
            addImpl(i);
        }

        return this;
    }

    protected void addImpl(AbstractCard card) {
        card.drawScale = startingScale;
        card.targetDrawScale = targetScale;
        card.setAngle(0, true);
        card.lighten(true);
        addUpgrade(card);
    }

    protected void addUpgrade(AbstractCard card) {
        if (canRenderUpgrades) {
            try {
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
            catch (Exception e) {
                EUIUtils.logError(this, "Card upgrade cannot be rendered: " + card.getClass());
                upgradeCards.put(card, card);
            }
        }
    }

    public void clear() {
        super.clear();
        upgradeCards.clear();
    }

    @Override
    public void forceUpdateItemPosition(AbstractCard card, float x, float y) {
        card.current_x = card.target_x = x;
        card.current_y = card.target_y = y;
        card.hb.update();
        card.hb.move(card.current_x, card.target_y);
    }

    @Override
    public Hitbox getHitbox(AbstractCard item) {
        return item.hb;
    }

    @Override
    protected float getScrollDistance(AbstractCard card, int index) {
        float scrollDistance = 1f / getRowCount();
        if (card.target_y > drawTopY) {
            return -scrollDistance;
        }
        else if (card.target_y < 0) {
            return scrollDistance;
        }
        return 0;
    }

    public AbstractCard getUpgrade(AbstractCard card) {
        return upgradeCards.get(card);
    }

    public CardGroup makeCardGroup() {
        return EUIGameUtils.makeCardGroup(group.group);
    }

    @Override
    protected void renderItem(SpriteBatch sb, AbstractCard card) {
        // renderInLibrary continually creates copies of upgraded cards -_-
        // So we use a cache of the upgraded cards to show in compendium screens
        if (canRenderUpgrades && SingleCardViewPopup.isViewingUpgrade) {
            AbstractCard upgrade = upgradeCards.get(card);
            if (upgrade != null) {
                upgrade.current_x = card.current_x;
                upgrade.current_y = card.current_y;
                upgrade.drawScale = card.drawScale;
                upgrade.transparency = card.transparency;
                upgrade.render(sb);
            }
        }
        else {
            card.render(sb);
        }
    }

    // Ensure hovered is rendered over other items
    @Override
    protected void renderItems(SpriteBatch sb) {
        for (AbstractCard item : group.group) {
            if (item != hovered) {
                renderItem(sb, item);
                if (onRender != null) {
                    onRender.invoke(sb, item);
                }
            }
        }
        if (hovered != null) {
            hovered.renderHoverShadow(sb);
            renderItem(sb, hovered);
            if (onRender != null) {
                onRender.invoke(sb, hovered);
            }
        }
    }

    @Override
    protected void renderTip(SpriteBatch sb) {
        if (hovered != null) {
            hovered.renderCardTip(sb);
        }
    }

    public EUICardGrid setCanRenderUpgrades(boolean val) {
        this.canRenderUpgrades = val;
        return this;
    }

    public EUICardGrid setCardGroup(CardGroup cardGroup) {
        super.setItems(cardGroup.group);
        return this;
    }

    @Override
    protected void updateHoverLogic(AbstractCard card, int i) {
        card.fadingOut = false;
        card.update();
        card.updateHoverLogic();

        if (card.hb.hovered) {
            hovered = card;
            hoveredIndex = i;
        }
    }

    @Override
    public void updateItemPosition(AbstractCard card, float x, float y) {
        card.target_x = x;
        card.target_y = y;
        // Current x/y handled by card update
    }
}
