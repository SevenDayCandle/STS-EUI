package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
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

        preview.current_x = x - AbstractCard.IMG_WIDTH * 0.05f; // Cards appear slightly to the right compared to other previews
        preview.current_y = y;
        preview.drawScale = scale;

        // Ensure that entire preview is shown on screen
        float lim = preview.hb.height / 2f + EUITooltip.minPreviewY();
        if (preview.current_y < lim) {
            preview.current_y = lim;
        }
        else {
            lim = preview.hb.height / 2f;
            if (preview.current_y + lim > TIP_Y_LIMIT) {
                preview.current_y = TIP_Y_LIMIT - lim;
            }
        }

        preview.render(sb);
    }
}
