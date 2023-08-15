package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.CardObject;
import extendedui.interfaces.markers.KeywordProvider;

import static extendedui.ui.tooltips.EUITooltip.TIP_Y_LIMIT;

public class EUICardPreview extends EUIPreview implements CardObject {
    public AbstractCard defaultPreview;
    public AbstractCard upgradedPreview;

    public EUICardPreview(AbstractCard card) {
        this(card, false);
    }

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

    @Override
    public AbstractCard getCard() {
        return defaultPreview;
    }

    public AbstractCard getPreview(boolean upgraded) {
        return upgraded && upgradedPreview != null ? upgradedPreview : defaultPreview;
    }

    @Override
    public boolean matches(String preview) {
        return defaultPreview.cardID.equals(preview);
    }

    public void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded) {
        AbstractCard preview = getPreview(upgraded);

        preview.current_x = x;
        preview.current_y = y;
        preview.drawScale = scale;

        // Ensure that entire preview is shown on screen
        float half = preview.hb.height / 2f;
        if (preview.current_y < half) {
            preview.current_y = half;
        }
        else if (preview.current_y + half > TIP_Y_LIMIT) {
            preview.current_y = TIP_Y_LIMIT - half;
        }

        preview.render(sb);

        if (isMultiPreview) {
            String cyclePreviewText = EUIRM.strings.keyToCycle(EUIHotkeys.cycle.getKeyString());
            BitmapFont font = EUIRenderHelpers.getDescriptionFont(preview, 0.9f);
            EUIRenderHelpers.drawOnCardAuto(sb, preview, EUIRM.images.panelRounded.texture(), 0, -AbstractCard.RAW_H * 0.55f,
                    AbstractCard.IMG_WIDTH * 0.6f, font.getLineHeight() * 1.8f, Color.DARK_GRAY, 0.75f, 1);
            EUIRenderHelpers.writeOnCard(sb, preview, font, cyclePreviewText, 0, -AbstractCard.RAW_H * 0.55f, Color.MAGENTA);
            EUIRenderHelpers.resetFont(font);
        }
    }
}
