package extendedui.ui.hitboxes;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.ui.GUI_Base;
import extendedui.utilities.Mathf;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;

public class AdvancedHitbox extends Hitbox
{
    public float lerpSpeed;
    public float target_cX;
    public float target_cY;
    public GUI_Base parentElement;
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

        this.target_cX = cX;
        this.target_cY = cY;
        this.lerpSpeed = 9f;
    }

    public AdvancedHitbox SetPosition(float cX, float cY)
    {
        move(cX, cY);

        return this;
    }

    public AdvancedHitbox SetTargetPosition(float cX, float cY)
    {
        this.target_cX = cX;
        this.target_cY = cY;

        return this;
    }

    public AdvancedHitbox SetIsPopupCompatible(boolean value) {
        this.isPopupCompatible = value;

        return this;
    }

    public AdvancedHitbox SetParentElement(GUI_Base element) {
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

        if (cX != target_cX || cY != target_cY)
        {
            moveInternal(Lerp(cX, target_cX), Lerp(cY, target_cY));
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
            if (!EUI.IsInActiveElement(this)) {
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
        this.target_cX = this.cX = x + this.width / 2f;
        this.target_cY = this.cY = y + this.height / 2f;
    }

    @Override
    public void resize(float w, float h)
    {
        this.width = w;
        this.height = h;
        this.target_cX = this.cX = x + this.width / 2f;
        this.target_cY = this.cY = y + this.height / 2f;
    }

    @Override
    public void move(float cX, float cY)
    {
        this.target_cX = this.cX = cX;
        this.target_cY = this.cY = cY;
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

    protected float Lerp(float current, float target)
    {
        if (lerpSpeed < 0 || Math.abs(current - target) < Settings.UI_SNAP_THRESHOLD)
        {
            return target;
        }

        return Mathf.Lerp(current, target, lerpSpeed * Gdx.graphics.getDeltaTime());
    }
}
