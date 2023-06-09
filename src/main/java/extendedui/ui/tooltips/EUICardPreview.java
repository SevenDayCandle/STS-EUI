package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.CardObject;
import extendedui.interfaces.markers.KeywordProvider;

public class EUICardPreview implements CardObject {
    public AbstractCard defaultPreview;
    public AbstractCard upgradedPreview;
    public boolean isMultiPreview;

    public EUICardPreview(AbstractCard card, boolean upgrade) {
        this.defaultPreview = card;
        if (defaultPreview instanceof KeywordProvider) {
            ((KeywordProvider) this.defaultPreview).setIsPreview(true);
        }

        if (upgrade) {
            this.upgradedPreview = defaultPreview.makeStatEquivalentCopy();
            this.upgradedPreview.upgrade();
            this.upgradedPreview.displayUpgrades();
            if (upgradedPreview instanceof KeywordProvider) {
                ((KeywordProvider) this.upgradedPreview).setIsPreview(true);
            }
        }
    }

    public static EUICardPreview generatePreviewCard(AbstractCard card) {
        return generatePreviewCard(card, false);
    }

    public static EUICardPreview generatePreviewCard(AbstractCard card, boolean upgrade) {
        return new EUICardPreview(card, upgrade);
    }

    @Override
    public AbstractCard getCard() {
        return defaultPreview;
    }

    public AbstractCard getPreview(boolean upgraded) {
        return upgraded && upgradedPreview != null ? upgradedPreview : defaultPreview;
    }

    public void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded) {
        AbstractCard preview = getPreview(upgraded);

        preview.current_x = x;
        preview.current_y = y;
        preview.drawScale = scale;
        preview.render(sb);

        if (isMultiPreview) {
            String cyclePreviewText = EUIRM.strings.keyToCycle(EUIHotkeys.cycle.getKeyString());
            BitmapFont font = EUIRenderHelpers.getDescriptionFont(preview, 0.9f);
            EUIRenderHelpers.drawOnCardAuto(sb, preview, EUIRM.images.panel.texture(), new Vector2(0, -AbstractCard.RAW_H * 0.55f),
                    AbstractCard.IMG_WIDTH * 0.6f, font.getLineHeight() * 1.8f, Color.DARK_GRAY, 0.75f, 1);
            EUIRenderHelpers.writeOnCard(sb, preview, font, cyclePreviewText, 0, -AbstractCard.RAW_H * 0.55f, Color.MAGENTA);
            EUIRenderHelpers.resetFont(font);
        }
    }

    public void render(SpriteBatch sb, AbstractCard card, boolean upgraded, boolean isPopup) {
        if (isPopup) {
            float x = (float) Settings.WIDTH * 0.2f - 10f * Settings.scale;
            float y = (float) Settings.HEIGHT * 0.25f;
            float scale = 1f;
            render(sb, x, y, scale, upgraded);
        }
        else if (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard) {
            float x = card.current_x + (AbstractCard.IMG_WIDTH * 0.9f + 16f) * ((card.current_x > Settings.WIDTH * 0.7f) ? card.drawScale : -card.drawScale);
            float y = card.current_y + (AbstractCard.IMG_HEIGHT * 0.1f) * card.drawScale;
            float scale = card.drawScale * 0.8f;
            render(sb, x, y, scale, upgraded);
        }
    }
}
