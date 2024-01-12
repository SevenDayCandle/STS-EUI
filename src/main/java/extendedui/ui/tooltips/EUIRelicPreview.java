package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.text.EUITextHelper;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import static extendedui.ui.tooltips.EUITooltip.*;

public class EUIRelicPreview extends EUIPreview {
    private static final float BOX_W = AbstractCard.IMG_WIDTH * 0.8f;
    private static final float BODY_TEXT_WIDTH = BOX_W - Settings.scale * 40f;
    private static final float HEADER_OFFSET_Y = -33.0F * Settings.scale;
    private static final float BODY_OFFSET_Y = -60f * Settings.scale;
    private static AbstractRelic last;
    private static float lastHeight;
    public AbstractRelic preview;

    public EUIRelicPreview(AbstractRelic relic) {
        this.preview = relic;
    }

    protected static float getHeight(AbstractRelic relic) {
        if (last != relic) {
            last = relic;
            float lastTextHeight = EUITextHelper.getSmartHeight(EUIFontHelper.tooltipFont, relic.description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastHeight = (StringUtils.isEmpty(relic.description)) ? (-40f * Settings.scale) : (-(lastTextHeight) - 7f * Settings.scale);
            lastHeight += AbstractRelic.PAD_X;
        }
        return lastHeight;
    }

    @Override
    public boolean matches(String preview) {
        return this.preview.relicId.equals(preview);
    }

    @Override
    public void render(SpriteBatch sb, float curX, float curY, float drawScale, boolean upgraded, boolean fromPopup) {
        if (fromPopup) {
            float x = (float) Settings.WIDTH * 0.2f - 10f * Settings.scale;
            float y = (float) Settings.HEIGHT * 0.25f;
            float scale = 1f;
            render(sb, x, y, scale, upgraded);
        }
        else if (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard) {
            float x = curX + (AbstractCard.IMG_WIDTH * 0.9f + 16f) * ((curX > Settings.WIDTH * 0.7f) ? drawScale : -drawScale);
            float y = curY + (AbstractCard.IMG_HEIGHT * 0.2f) * drawScale;
            float scale = drawScale * 0.8f;
            render(sb, x, y, scale, upgraded);
        }
    }

    @Override
    public void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded) {
        final float h = getHeight(preview);

        x -= BOX_W / 2 + TEXT_OFFSET_X;

        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);

        float c = x + BOX_W / 2;
        preview.currentX = c;
        preview.currentY = y;
        if (preview.currentY - h < 0) {
            preview.currentY = h;
        }

        preview.scale = scale;
        preview.render(sb, false, EUIColors.black(0.33f));

        FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, preview.name, c, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        if (!StringUtils.isEmpty(preview.description)) {
            EUITextHelper.renderSmart(sb, EUIFontHelper.tooltipFont, preview.description, x + TEXT_OFFSET_X, y + BODY_OFFSET_Y, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
        }
    }
}
