package extendedui.ui.hitboxes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.ui.EUIBase;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;

public class AdvancedHitbox extends Hitbox
{
    public float lerpSpeed;
    public float targetCx;
    public float targetCy;
    public EUIBase parentElement;
    public boolean isPopupCompatible;

    public AdvancedHitbox(Hitbox hb)
    {
        this(hb.x, hb.y, hb.width, hb.height);
    }

    public AdvancedHitbox(float width, float height)
    {
        this(-9999, -9999, width, height);
    }

    public AdvancedHitbox(Hitbox hb, float width, float height)
    {
        this(hb.x, hb.y, width, height);
    }

    public AdvancedHitbox(float x, float y, float width, float height)
    {
        super(x, y, width, height);

        this.targetCx = cX;
        this.targetCy = cY;
        this.lerpSpeed = 9f;
    }

    public AdvancedHitbox setPosition(float cX, float cY)
    {
        move(cX, cY);

        return this;
    }

    public AdvancedHitbox setTargetPosition(float cX, float cY)
    {
        this.targetCx = cX;
        this.targetCy = cY;

        return this;
    }

    public AdvancedHitbox setIsPopupCompatible(boolean value) {
        this.isPopupCompatible = value;

        return this;
    }

    public AdvancedHitbox setParentElement(EUIBase element) {
        this.parentElement = element;

        return this;
    }

    @Override
    public void update()
    {
        this.update(this.x, this.y);
        if (this.clickStarted && InputHelper.justReleasedClickLeft) {
            if (this.hovered) {
                this.clicked = true;
            }

            this.clickStarted = false;
        }

        if (cX != targetCx || cY != targetCy)
        {
            moveInternal(lerp(cX, targetCx), lerp(cY, targetCy));
        }
    }

    @Override
    public void update(float x, float y) {
        if (!AbstractDungeon.isFadingOut) {
            this.x = x;
            this.y = y;
            if (this.justHovered) {
                this.justHovered = false;
            }

            float actualMX;
            float actualMY;
            if (!EUI.isInActiveElement(this)) {
                this.hovered = false;
                return;
            }

            if (isPopupCompatible && CardCrawlGame.isPopupOpen) {
                actualMX = popupMX;
                actualMY = popupMY;
            }
            else {
                actualMX = InputHelper.mX;
                actualMY = InputHelper.mY;
            }

            if (!this.hovered) {
                this.hovered = actualMX > x && actualMX < x + this.width && actualMY > y && actualMY < y + this.height;
                if (this.hovered) {
                    this.justHovered = true;
                }
            } else {
                this.hovered = actualMX > x && actualMX < x + this.width && actualMY > y && actualMY < y + this.height;
            }

        }
    }

    @Override
    public void translate(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.targetCx = this.cX = x + this.width / 2f;
        this.targetCy = this.cY = y + this.height / 2f;
    }

    @Override
    public void resize(float w, float h)
    {
        this.width = w;
        this.height = h;
        this.targetCx = this.cX = x + this.width / 2f;
        this.targetCy = this.cY = y + this.height / 2f;
    }

    @Override
    public void move(float cX, float cY)
    {
        this.targetCx = this.cX = cX;
        this.targetCy = this.cY = cY;
        this.x = cX - this.width / 2f;
        this.y = cY - this.height / 2f;
    }

    protected void moveInternal(float cX, float cY)
    {
        this.cX = cX;
        this.cY = cY;
        this.x = cX - this.width / 2f;
        this.y = cY - this.height / 2f;
    }

    protected float lerp(float current, float target)
    {
        if (lerpSpeed < 0 || Math.abs(current - target) < Settings.UI_SNAP_THRESHOLD)
        {
            return target;
        }

        return MathUtils.lerp(current, target, lerpSpeed * Gdx.graphics.getDeltaTime());
    }
}
