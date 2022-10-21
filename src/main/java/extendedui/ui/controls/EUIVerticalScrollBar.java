package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class EUIVerticalScrollBar extends EUIHoverable
{
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

    public EUIVerticalScrollBar(AdvancedHitbox hb)
    {
        super(hb);
        this.isBackgroundVisible = true;
        this.cursorWidth = hb.width * 0.7f;
        this.cursorHeight = hb.width * 1.1f;
        this.borderHeight = cursorHeight * 0.25f;
    }

    public EUIVerticalScrollBar SetPosition(float x, float y)
    {
        this.hb.move(x, y);

        return this;
    }

    public EUIVerticalScrollBar SetOnScroll(ActionT1<Float> onScroll)
    {
        this.onScroll = onScroll;

        return this;
    }

    public void Update()
    {
        cursorDrawPosition = MathHelper.scrollSnapLerpSpeed(cursorDrawPosition, FromPercentage(currentScrollPercent));

        super.Update();

        if (isDragging)
        {
            if (!InputHelper.isMouseDown)
            {
                isDragging = false;
            }
            else
            {
                Scroll(ToPercentage(CardCrawlGame.isPopupOpen ? CardCrawlGame.popupMY : InputHelper.mY), true);
            }
        }
        else if (hb.hovered && InputHelper.isMouseDown)
        {
            isDragging = true;
        }
    }

    public void Scroll(float percent, boolean triggerEvent)
    {
        currentScrollPercent = Clamp(percent);

        if (triggerEvent && onScroll != null)
        {
            onScroll.Invoke(currentScrollPercent);
        }
    }

    public void Render(SpriteBatch sb)
    {
        if (isBackgroundVisible)
        {
            centerImage.Render(sb, hb);
            topImage.Render(sb, hb.x, hb.y + hb.height, hb.width, topImage.srcHeight);
            bottomImage.Render(sb, hb.x, hb.y - bottomImage.srcHeight, hb.width, bottomImage.srcHeight);
        }

        cursorImage.Render(sb,hb.cX - (cursorWidth / 2f), cursorDrawPosition, cursorWidth, cursorHeight);

        hb.render(sb);
    }

    private float ToPercentage(float position)
    {
        float minY = this.hb.y + this.hb.height - borderHeight;
        float maxY = this.hb.y + borderHeight;

        return Clamp(MathHelper.percentFromValueBetween(minY, maxY, position));
    }

    private float FromPercentage(float percent)
    {
        float topY = this.hb.y + this.hb.height - cursorHeight + borderHeight;
        float bottomY = this.hb.y - borderHeight;

        return MathHelper.valueFromPercentBetween(topY, bottomY, Clamp(percent));
    }

    private static float Clamp(float percent)
    {
        return MathUtils.clamp(percent, 0f, 1f);
    }
}
