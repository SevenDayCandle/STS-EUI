package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.text.EUITextHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;

public class FilterSortToggle extends EUIHoverable {
    private final String text;
    private final ActionT1<Boolean> onClick;
    public final FilterSortHeader header;
    private final float textWidth;
    private Boolean isAscending;

    public FilterSortToggle(float x, String text, FilterSortHeader header, ActionT1<Boolean> onClick) {
        this(new EUIHitbox(x, header.baseY, 135.0F * Settings.xScale, 48.0F * Settings.scale), text, header, onClick);
    }

    public FilterSortToggle(EUIHitbox hb, String text, FilterSortHeader header, ActionT1<Boolean> onClick) {
        super(hb);
        this.text = text;
        this.header = header;
        this.onClick = onClick;
        this.textWidth = EUITextHelper.getSmartWidth(FontHelper.topPanelInfoFont, text, 3.4028235E38F, 0.0F);
    }

    public float getSize() {
        return Math.max(hb.width, textWidth + scale(32));
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        Color color = !this.hb.hovered && isAscending == null ? Settings.CREAM_COLOR : Settings.GOLD_COLOR;
        EUITextHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, this.text, this.hb.cX, this.hb.cY, color);
        sb.setColor(color);
        if (isAscending != null) {
            sb.draw(ImageMaster.FILTER_ARROW, this.hb.cX - 16.0F + this.textWidth / 2.0F + 16.0F * Settings.xScale, this.hb.cY - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, !this.isAscending);
        }
        this.hb.render(sb);
    }

    public void select(Boolean val) {
        this.isAscending = val;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.playA("UI_HOVER", -0.3F);
        }

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            this.hb.clickStarted = true;
        }

        if (this.hb.clicked || this.hb.hovered && CInputActionSet.select.isJustPressed()) {
            this.hb.clicked = false;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2F);
            onClick.invoke(isAscending != null && !isAscending);
            this.header.didChangeOrder(this);
        }
    }
}
