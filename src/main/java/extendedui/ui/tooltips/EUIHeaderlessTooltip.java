package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.text.EUISmartText;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

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
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.cardTooltipFont;
            lastTextHeight = EUISmartText.getSmartHeight(descFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastHeight = (!canRender) ? 0 : (-(lastTextHeight) - HEADER_OFFSET_Y);
        }
        return lastHeight;
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        verifyFonts();
        final float h = height();

        renderBg(sb, x, y, h);
        renderSubtext(sb, x, y);
        renderDescription(sb, x, y);

        return h;
    }
}
