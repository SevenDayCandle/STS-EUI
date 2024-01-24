package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.EUITextHelper;

public class EUIHeaderlessTooltip extends EUITooltip {

    public EUIHeaderlessTooltip(String description) {
        super(EUIUtils.EMPTY_STRING, description);
    }

    public EUIHeaderlessTooltip(String title, String description) {
        super(title, description);
    }

    public EUIHeaderlessTooltip(EUITooltip other) {
        super(other);
    }

    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.tooltipFont;
            lastTextHeight = EUITextHelper.getSmartHeight(descFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastHeight = (-(lastTextHeight) - HEADER_OFFSET_Y);
        }
        return lastHeight;
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        verifyFonts();
        final float h = height();

        renderBg(sb, Settings.TOP_PANEL_SHADOW_COLOR, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, h);
        renderBg(sb, Color.WHITE, x, y, h);
        renderSubtext(sb, x, y);
        renderDescription(sb, x, y);

        return h;
    }
}
