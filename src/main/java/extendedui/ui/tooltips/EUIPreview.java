package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.utilities.RotatingList;

public abstract class EUIPreview {
    public void render(SpriteBatch sb, float curX, float curY, float drawScale, boolean upgraded, boolean fromPopup) {
        if (fromPopup) {
            render(sb, curX, curY, 1f, upgraded);
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
