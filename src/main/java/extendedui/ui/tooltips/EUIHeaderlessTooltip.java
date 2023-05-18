package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.configuration.EUIHotkeys;
import extendedui.text.EUISmartText;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class EUIHeaderlessTooltip extends EUITooltip {
    public EUIHeaderlessTooltip(String title, String... descriptions) {
        super(title, descriptions);
    }

    public EUIHeaderlessTooltip(String title, Collection<String> descriptions) {
        super(title, descriptions);
    }

    public EUIHeaderlessTooltip(EUITooltip other) {
        super(other);
    }

    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.cardTooltipFont;
            String desc = description();
            lastTextHeight = EUISmartText.getSmartHeight(descFont, desc, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastHeight = (!canRender || StringUtils.isEmpty(desc)) ? (-40f * Settings.scale) : (-(lastTextHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        if (EUIHotkeys.cycle.isJustPressed()) {
            cycleDescription();
        }
        if (descriptions.size() > 1 && (subText == null || subText.text == null || subText.text.isEmpty())) {
            updateCycleText();
        }

        verifyFonts();
        final float h = height();

        renderBg(sb, x, y, h);
        renderSubtext(sb, x, y);
        renderDescription(sb, x, y);

        return h;
    }
}
