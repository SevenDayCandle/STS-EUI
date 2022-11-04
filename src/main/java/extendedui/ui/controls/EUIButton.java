package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT0;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.ActionT2;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRenderHelpers;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.GenericCallback;
import org.apache.commons.lang3.StringUtils;

public class EUIButton extends EUIHoverable
{
    public EUIImage background;
    public EUIImage border;

    public float clickDelay = 0f;
    public float targetAlpha = 1f;
    public float currentAlpha = 1f;
    public boolean interactable = true;
    public boolean isSmartText;
    public boolean showText = true;
    public boolean smartTextResize;
    public GenericCallback<EUIButton> onLeftClick;
    public GenericCallback<EUIButton> onRightClick;
    public String text;
    public Color textColor = Color.WHITE.cpy();

    protected BitmapFont font;
    protected EUILabel label;
    protected float fontScale;
    protected float currentClickDelay = 0f;

    public EUIButton(Texture buttonTexture, float x, float y)
    {
        this(buttonTexture, new AdvancedHitbox(x, y, Scale(buttonTexture.getWidth()), Scale(buttonTexture.getHeight())));
    }

    public EUIButton(Texture buttonTexture, AdvancedHitbox hitbox)
    {
        super(hitbox);
        this.background = EUIRenderHelpers.ForTexture(buttonTexture, hitbox, Color.WHITE);
        this.text = "";
        this.font = FontHelper.buttonLabelFont;
        this.fontScale = 1f;
    }

    public EUIButton SetAlpha(float currentAlpha, float targetAlpha) {
        this.currentAlpha = currentAlpha;
        this.targetAlpha = targetAlpha;
        return this;
    }

    public EUIButton SetAlpha(float targetAlpha) {
        this.targetAlpha = targetAlpha;
        return this;
    }

    public EUIButton SetBackground(Texture borderTexture)
    {
        this.background = EUIRenderHelpers.ForTexture(borderTexture);

        return this;
    }

    public EUIButton SetBorder(Texture borderTexture, Color color)
    {
        if (borderTexture == null)
        {
            this.border = null;
        }
        else
        {
            this.border = EUIRenderHelpers.ForTexture(borderTexture, color).SetHitbox(hb);
        }

        return this;
    }

    public EUIButton SetFont(BitmapFont font, float fontScale)
    {
        if (font != null)
        {
            this.font = font;
        }

        this.fontScale = fontScale;

        return this;
    }

    public EUIButton SetInteractable(boolean interactable)
    {
        this.interactable = interactable;

        return this;
    }

    public EUIButton SetSmartText(boolean isSmartText)
    {
        this.isSmartText = isSmartText;

        return this;
    }

    public EUIButton SetSmartText(boolean isSmartText, boolean smartTextResize)
    {
        this.isSmartText = isSmartText;
        this.smartTextResize = smartTextResize;

        return this;
    }

    public EUIButton SetButtonScale(float scaleX, float scaleY)
    {
        this.background.SetScale(scaleX, scaleY);

        return this;
    }

    public EUIButton SetDimensions(float width, float height)
    {
        this.hb.resize(width, height);

        return this;
    }

    public EUIButton SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIButton SetText(String text)
    {
        this.text = text;

        return this;
    }

    public EUIButton SetClickDelay(float delay)
    {
        this.clickDelay = delay;

        return this;
    }

    public EUIButton SetOnClick(ActionT0 onClick)
    {
        this.onLeftClick = GenericCallback.FromT0(onClick);

        return this;
    }

    public EUIButton SetOnClick(ActionT1<EUIButton> onClick)
    {
        this.onLeftClick = GenericCallback.FromT1(onClick);

        return this;
    }

    public <T> EUIButton SetOnClick(T state, ActionT2<T, EUIButton> onClick)
    {
        this.onLeftClick = GenericCallback.FromT2(onClick, state);

        return this;
    }

    public EUIButton SetOnRightClick(ActionT0 onClick)
    {
        this.onRightClick = GenericCallback.FromT0(onClick);

        return this;
    }

    public EUIButton SetOnRightClick(ActionT1<EUIButton> onClick)
    {
        this.onRightClick = GenericCallback.FromT1(onClick);

        return this;
    }

    public <T> EUIButton SetOnRightClick(T state, ActionT2<T, EUIButton> onClick)
    {
        this.onRightClick = GenericCallback.FromT2(onClick, state);

        return this;
    }

    public EUIButton SetColor(Color buttonColor)
    {
        background.SetColor(buttonColor);

        return this;
    }

    public EUIButton SetTargetColor(Color buttonColor)
    {
        background.SetTargetColor(buttonColor);

        return this;
    }

    public EUIButton SetShaderMode(EUIRenderHelpers.ShaderMode shaderMode) {
        this.background.SetShaderMode(shaderMode);
        return this;
    }

    public EUIButton SetTextColor(Color textColor)
    {
        this.textColor = textColor.cpy();

        return this;
    }

    public EUIButton SetTooltip(String title, String description)
    {
        return SetTooltip(new EUITooltip(title, description));
    }

    public EUIButton SetTooltip(EUITooltip tooltip)
    {
        super.SetTooltip(tooltip);

        return this;
    }

    public EUIButton ShowTooltip(boolean show)
    {
        if (tooltip != null)
        {
            this.tooltip.canRender = show;
        }

        return this;
    }

    public boolean IsInteractable()
    {
        return interactable && onLeftClick != null;
    }

    @Override
    public void Update()
    {
        if (currentClickDelay > 0)
        {
            this.currentClickDelay -= EUI.Delta();
        }

        this.currentAlpha = MathHelper.fadeLerpSnap(currentAlpha, targetAlpha);
        if ((currentAlpha <= 0))
        {
            return;
        }

        super.Update();
        background.UpdateColor();

        if (IsInteractable() && EUI.TryHover(hb))
        {
            if (this.hb.justHovered)
            {
                OnJustHovered();
            }

            if (this.hb.hovered)
            {
                if (currentClickDelay <= 0)
                {
                    if (EUIInputManager.RightClick.IsJustPressed())
                    {
                        OnRightClick();
                    }
                    else if (InputHelper.justClickedLeft)
                    {
                        OnClickStart();
                    }
                }
            }

            if (this.hb.clicked)
            {
                OnLeftClick();
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        background.color.a = this.textColor.a = currentAlpha;
        if (currentAlpha <= 0)
        {
            return;
        }

        final boolean interactable = IsInteractable();
        if (StringUtils.isNotEmpty(text) && showText)
        {
            this.RenderButton(sb, interactable);

            font.getData().setScale(fontScale);
            final Color color = interactable ? textColor : TEXT_DISABLED_COLOR;
            if (isSmartText) {
                EUISmartText.Write(sb, font, text, hb.cX - (hb.width * 0.4f), hb.y + (hb.height * 0.65f), hb.width, font.getLineHeight(), color);
            }
            else if (FontHelper.getSmartWidth(font, text, Integer.MAX_VALUE, 0f) > (hb.width * 0.7))
            {
                EUIRenderHelpers.WriteCentered(sb, font, text, hb, color, 0.8f);
            }
            else
            {
                EUIRenderHelpers.WriteCentered(sb, font, text, hb, color);
            }
            EUIRenderHelpers.ResetFont(font);
        }
        else
        {
            if (interactable) {
                this.RenderButton(sb, interactable);
            }
            else {
                EUIRenderHelpers.DrawGrayscale(sb, (s) -> this.RenderButton(s, interactable));
            }

        }

        this.hb.render(sb);
    }

    public void RenderCentered(SpriteBatch sb)
    {
        background.color.a = this.textColor.a = currentAlpha;
        if (currentAlpha <= 0)
        {
            return;
        }

        final boolean interactable = IsInteractable();
        if (StringUtils.isNotEmpty(text) && showText)
        {
            this.RenderButtonCentered(sb, interactable);

            font.getData().setScale(fontScale);
            final Color color = interactable ? textColor : TEXT_DISABLED_COLOR;
            if (isSmartText) {
                EUISmartText.Write(sb, font, text, hb.cX - (hb.width * 0.4f), hb.y + (hb.height * 0.65f), hb.width, font.getLineHeight(), color, smartTextResize);
            }
            else if (FontHelper.getSmartWidth(font, text, Integer.MAX_VALUE, 0f) > (hb.width * 0.7))
            {
                EUIRenderHelpers.WriteCentered(sb, font, text, hb, color, 0.8f);
            }
            else
            {
                EUIRenderHelpers.WriteCentered(sb, font, text, hb, color);
            }
            EUIRenderHelpers.ResetFont(font);
        }
        else
        {
            if (interactable) {
                this.RenderButtonCentered(sb, interactable);
            }
            else {
                EUIRenderHelpers.DrawGrayscale(sb, (s) -> this.RenderButtonCentered(s, interactable));
            }

        }

        this.hb.render(sb);
    }

    protected void RenderButton(SpriteBatch sb, boolean interactable)
    {
        background.Render(sb, hb);

        if (border != null)
        {
            border.Render(sb);
        }

        if (interactable && this.hb.hovered && !this.hb.clickStarted)
        {
            background.Render(sb, EUIRenderHelpers.ShaderMode.Bright, hb, HOVER_BLEND_COLOR);
        }
    }

    protected void RenderButtonCentered(SpriteBatch sb, boolean interactable)
    {
        background.RenderCentered(sb, hb);

        if (border != null)
        {
            border.RenderCentered(sb);
        }

        if (interactable && this.hb.hovered && !this.hb.clickStarted)
        {
            background.RenderCentered(sb, EUIRenderHelpers.ShaderMode.Bright, hb, HOVER_BLEND_COLOR);
        }
    }


    protected void OnJustHovered()
    {
        CardCrawlGame.sound.play("UI_HOVER");
    }

    protected void OnClickStart()
    {
        this.hb.clickStarted = true;
        CardCrawlGame.sound.play("UI_CLICK_1");
    }

    protected void OnLeftClick()
    {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onLeftClick != null)
        {
            this.onLeftClick.Complete(this);
        }
    }

    protected void OnRightClick()
    {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onRightClick != null)
        {
            this.onRightClick.Complete(this);
        }
    }

    public boolean TryRenderCentered(SpriteBatch sb)
    {
        if (isActive)
        {
            this.hb.render(sb);
            RenderCentered(sb);
        }

        return isActive;
    }
}