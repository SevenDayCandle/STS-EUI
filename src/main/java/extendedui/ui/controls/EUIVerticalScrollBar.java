package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUIVerticalScrollBar extends EUIHoverable {
    protected final float cursorWidth;
    protected final float cursorHeight;
    protected final float borderHeight;
    public final EUIImage topImage = new EUIImage(ImageMaster.SCROLL_BAR_TOP);
    public final EUIImage centerImage = new EUIImage(ImageMaster.SCROLL_BAR_MIDDLE);
    public final EUIImage bottomImage = new EUIImage(ImageMaster.SCROLL_BAR_BOTTOM);
    public final EUIImage cursorImage = new EUIImage(ImageMaster.SCROLL_BAR_TRAIN);
    public ActionT1<Float> onScroll;
    public boolean isBackgroundVisible;
    public boolean isDragging;
    public float currentScrollPercent;
    public float cursorDrawPosition;

    public EUIVerticalScrollBar(EUIHitbox hb) {
        super(hb);
        this.isBackgroundVisible = true;
        this.cursorWidth = hb.width * 0.7f;
        this.cursorHeight = hb.width * 1.1f;
        this.borderHeight = cursorHeight * 0.25f;
    }

    private static float clamp(float percent) {
        return MathUtils.clamp(percent, 0f, 1f);
    }

    private float fromPercentage(float percent) {
        float topY = this.hb.y + this.hb.height - cursorHeight + borderHeight;
        float bottomY = this.hb.y - borderHeight;

        return MathHelper.valueFromPercentBetween(topY, bottomY, clamp(percent));
    }

    public void renderImpl(SpriteBatch sb) {
        if (isBackgroundVisible) {
            centerImage.render(sb, hb);
            topImage.render(sb, hb.x, hb.y + hb.height, hb.width, topImage.srcHeight);
            bottomImage.render(sb, hb.x, hb.y - bottomImage.srcHeight, hb.width, bottomImage.srcHeight);
        }

        cursorImage.render(sb, hb.cX - (cursorWidth / 2f), cursorDrawPosition, cursorWidth, cursorHeight);

        hb.render(sb);
    }

    public void scroll(float percent, boolean triggerEvent) {
        currentScrollPercent = clamp(percent);

        if (triggerEvent && onScroll != null) {
            onScroll.invoke(currentScrollPercent);
        }
    }

    public EUIVerticalScrollBar setOnScroll(ActionT1<Float> onScroll) {
        this.onScroll = onScroll;

        return this;
    }

    public EUIVerticalScrollBar setPosition(float x, float y) {
        this.hb.move(x, y);

        return this;
    }

    public void updateImpl() {
        cursorDrawPosition = MathHelper.scrollSnapLerpSpeed(cursorDrawPosition, fromPercentage(currentScrollPercent));

        super.updateImpl();

        if (isDragging) {
            if (!InputHelper.isMouseDown) {
                isDragging = false;
            }
            else {
                scroll(toPercentage(CardCrawlGame.isPopupOpen ? CardCrawlGame.popupMY : InputHelper.mY), true);
            }
        }
        else if (hb.hovered && InputHelper.isMouseDown) {
            isDragging = true;
        }
    }

    private float toPercentage(float position) {
        float minY = this.hb.y + this.hb.height - borderHeight;
        float maxY = this.hb.y + borderHeight;

        return clamp(MathHelper.percentFromValueBetween(minY, maxY, position));
    }
}
