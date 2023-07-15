package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.utilities.RotatingList;

public abstract class EUIPreview {
    public static final RotatingList<EUIPreview> PREVIEWS = new RotatingList<>();
    private static TooltipProvider lastProvider = null;
    public boolean isMultiPreview;

    public static EUIPreview getPreview(TooltipProvider card) {
        setPreviews(card);

        EUIPreview preview;
        if (PREVIEWS.size() > 1) {
            if (EUIHotkeys.cycle.isJustPressed()) {
                preview = PREVIEWS.next(true);
            }
            else {
                preview = PREVIEWS.current();
            }
            preview.isMultiPreview = true;
        }
        else {
            preview = PREVIEWS.current();
        }

        return preview;
    }

    public static void invalidate() {
        PREVIEWS.clear();
        lastProvider = null;
    }

    public static void setPreviews(TooltipProvider card) {
        if (card != null && lastProvider != card) {
            lastProvider = card;
            PREVIEWS.clear();
            lastProvider.fillPreviews(PREVIEWS);
        }
    }

    public void render(SpriteBatch sb, AbstractCard card, boolean upgraded, boolean isPopup) {
        render(sb, card.current_x, card.current_y, 0.83f, upgraded, isPopup);
    }

    public void render(SpriteBatch sb, AbstractPotion card, boolean upgraded, boolean isPopup) {
        render(sb, card.posX + card.hb.width, card.posY, 0.83f, upgraded, isPopup);
    }

    public void render(SpriteBatch sb, AbstractRelic card, boolean upgraded, boolean isPopup) {
        render(sb, card.currentX + card.hb.width, card.currentY, 0.83f, upgraded, isPopup);
    }

    public void render(SpriteBatch sb, float curX, float curY, float drawScale, boolean upgraded, boolean isPopup) {
        if (isPopup) {
            float x = (float) Settings.WIDTH * 0.2f - 10f * Settings.scale;
            float y = (float) Settings.HEIGHT * 0.25f;
            float scale = 1f;
            render(sb, x, y, scale, upgraded);
        }
        else if (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard) {
            float x = curX + (AbstractCard.IMG_WIDTH * 0.9f + 16f) * ((curX > Settings.WIDTH * 0.7f) ? drawScale : -drawScale);
            float y = curY + (AbstractCard.IMG_HEIGHT * 0.1f) * drawScale;
            float scale = drawScale * 0.8f;
            render(sb, x, y, scale, upgraded);
        }
    }

    public abstract boolean matches(String preview);
    public abstract void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded);
}
