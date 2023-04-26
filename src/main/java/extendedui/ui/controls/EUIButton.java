package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.GenericCallback;
import org.apache.commons.lang3.StringUtils;

public class EUIButton extends EUIHoverable {
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
    public Color hoverBlendColor;

    protected BitmapFont font;
    protected EUILabel label;
    protected float fontScale;
    protected float currentClickDelay = 0f;

    public EUIButton(Texture buttonTexture, float x, float y) {
        this(buttonTexture, new EUIHitbox(x, y, scale(buttonTexture.getWidth()), scale(buttonTexture.getHeight())));
    }

    public EUIButton(Texture buttonTexture, EUIHitbox hitbox) {
        super(hitbox);
        this.background = new EUIImage(buttonTexture, hitbox, Color.WHITE);
        this.text = "";
        this.font = EUIFontHelper.buttonFont;
        this.fontScale = 1f;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        background.color.a = this.textColor.a = currentAlpha;
        if (currentAlpha <= 0) {
            return;
        }

        final boolean interactable = isInteractable();
        if (StringUtils.isNotEmpty(text) && showText) {
            this.renderButton(sb, interactable);

            font.getData().setScale(fontScale);
            final Color color = interactable ? textColor : TEXT_DISABLED_COLOR;
            if (isSmartText) {
                EUISmartText.write(sb, font, text, hb.cX - (hb.width * 0.4f), hb.y + (hb.height * 0.65f), hb.width, font.getLineHeight(), color);
            }
            else if (FontHelper.getSmartWidth(font, text, Integer.MAX_VALUE, 0f) > (hb.width * 0.7)) {
                EUIRenderHelpers.writeCentered(sb, font, text, hb, color, 0.8f);
            }
            else {
                EUIRenderHelpers.writeCentered(sb, font, text, hb, color);
            }
            EUIRenderHelpers.resetFont(font);
        }
        else {
            if (interactable) {
                this.renderButton(sb, interactable);
            }
            else {
                EUIRenderHelpers.drawGrayscale(sb, (s) -> this.renderButton(s, interactable));
            }

        }

        this.hb.render(sb);
    }

    protected void renderButton(SpriteBatch sb, boolean interactable) {
        background.render(sb, hb);

        if (border != null) {
            border.renderImpl(sb);
        }

        if (interactable && this.hb.hovered && !this.hb.clickStarted) {
            background.render(sb, EUIRenderHelpers.ShaderMode.Bright, hb, hoverBlendColor != null ? hoverBlendColor : HOVER_BLEND_COLOR);
        }
    }

    public EUIButton setAlpha(float currentAlpha, float targetAlpha) {
        this.currentAlpha = currentAlpha;
        this.targetAlpha = targetAlpha;
        return this;
    }

    public EUIButton setAlpha(float targetAlpha) {
        this.targetAlpha = targetAlpha;
        return this;
    }

    public EUIButton setBackground(Texture borderTexture) {
        this.background = new EUIImage(borderTexture, Color.WHITE);

        return this;
    }

    public EUIButton setBorder(Texture borderTexture, Color color) {
        if (borderTexture == null) {
            this.border = null;
        }
        else {
            this.border = new EUIImage(borderTexture, color).setHitbox(hb);
        }

        return this;
    }

    public EUIButton setButtonRotation(float angle) {
        this.background.setRotation(angle);

        return this;
    }

    public EUIButton setButtonScale(float scaleX, float scaleY) {
        this.background.setScale(scaleX, scaleY);

        return this;
    }

    public EUIButton setClickDelay(float delay) {
        this.clickDelay = delay;

        return this;
    }

    public EUIButton setColor(Color buttonColor) {
        background.setColor(buttonColor);

        return this;
    }

    public EUIButton setFont(BitmapFont font, float fontScale) {
        if (font != null) {
            this.font = font;
        }

        this.fontScale = fontScale;

        return this;
    }

    public EUIButton setHoverBlendColor(Color color) {
        this.hoverBlendColor = color;
        return this;
    }

    public EUIButton setOnClick(ActionT0 onClick) {
        this.onLeftClick = GenericCallback.fromT0(onClick);

        return this;
    }

    public EUIButton setOnClick(ActionT1<EUIButton> onClick) {
        this.onLeftClick = GenericCallback.fromT1(onClick);

        return this;
    }

    public <T> EUIButton setOnClick(T state, ActionT2<T, EUIButton> onClick) {
        this.onLeftClick = GenericCallback.fromT2(onClick, state);

        return this;
    }

    public EUIButton setOnRightClick(ActionT0 onClick) {
        this.onRightClick = GenericCallback.fromT0(onClick);

        return this;
    }

    public EUIButton setOnRightClick(ActionT1<EUIButton> onClick) {
        this.onRightClick = GenericCallback.fromT1(onClick);

        return this;
    }

    public <T> EUIButton setOnRightClick(T state, ActionT2<T, EUIButton> onClick) {
        this.onRightClick = GenericCallback.fromT2(onClick, state);

        return this;
    }

    public EUIButton setShaderMode(EUIRenderHelpers.ShaderMode shaderMode) {
        this.background.setShaderMode(shaderMode);
        return this;
    }

    public EUIButton setSmartText(boolean isSmartText) {
        this.isSmartText = isSmartText;

        return this;
    }

    public EUIButton setSmartText(boolean isSmartText, boolean smartTextResize) {
        this.isSmartText = isSmartText;
        this.smartTextResize = smartTextResize;

        return this;
    }

    public EUIButton setTargetColor(Color buttonColor) {
        background.setTargetColor(buttonColor);

        return this;
    }

    public EUIButton setText(String text) {
        this.text = text;

        return this;
    }

    public EUIButton setTextColor(Color textColor) {
        this.textColor = textColor.cpy();

        return this;
    }

    public EUIButton showTooltip(boolean show) {
        if (tooltip != null) {
            this.tooltip.canRender = show;
        }

        return this;
    }

    public boolean tryRenderCentered(SpriteBatch sb) {
        if (isActive) {
            this.hb.render(sb);
            renderCentered(sb);
        }

        return isActive;
    }

    public void renderCentered(SpriteBatch sb) {
        background.color.a = this.textColor.a = currentAlpha;
        if (currentAlpha <= 0) {
            return;
        }

        final boolean interactable = isInteractable();
        if (StringUtils.isNotEmpty(text) && showText) {
            this.renderButtonCentered(sb, interactable);

            font.getData().setScale(fontScale);
            final Color color = interactable ? textColor : TEXT_DISABLED_COLOR;
            if (isSmartText) {
                EUISmartText.write(sb, font, text, hb.cX - (hb.width * 0.4f), hb.y + (hb.height * 0.65f), hb.width, font.getLineHeight(), color, smartTextResize);
            }
            else if (FontHelper.getSmartWidth(font, text, Integer.MAX_VALUE, 0f) > (hb.width * 0.7)) {
                EUIRenderHelpers.writeCentered(sb, font, text, hb, color, 0.8f);
            }
            else {
                EUIRenderHelpers.writeCentered(sb, font, text, hb, color);
            }
            EUIRenderHelpers.resetFont(font);
        }
        else {
            if (interactable) {
                this.renderButtonCentered(sb, interactable);
            }
            else {
                EUIRenderHelpers.drawGrayscale(sb, (s) -> this.renderButtonCentered(s, interactable));
            }

        }

        this.hb.render(sb);
    }

    protected void renderButtonCentered(SpriteBatch sb, boolean interactable) {
        background.renderCentered(sb, hb);

        if (border != null) {
            border.renderCentered(sb);
        }

        if (interactable && this.hb.hovered && !this.hb.clickStarted) {
            background.renderCentered(sb, EUIRenderHelpers.ShaderMode.Bright, hb, hoverBlendColor != null ? hoverBlendColor : HOVER_BLEND_COLOR);
        }
    }

    @Override
    public void updateImpl() {
        if (currentClickDelay > 0) {
            this.currentClickDelay -= EUI.delta();
        }

        this.currentAlpha = MathHelper.fadeLerpSnap(currentAlpha, targetAlpha);
        if ((currentAlpha <= 0)) {
            return;
        }

        super.updateImpl();
        background.updateColor();

        if (isInteractable()) {
            if (this.hb.justHovered) {
                onJustHovered();
            }

            if (this.hb.hovered) {
                if (currentClickDelay <= 0) {
                    if (EUIInputManager.rightClick.isJustPressed() && EUI.tryClick(this.hb)) {
                        onRightClick();
                    }
                    else if (InputHelper.justClickedLeft) {
                        onClickStart();
                    }
                }
            }

            if (this.hb.clicked && EUI.tryClick(this.hb)) {
                onLeftClick();
            }
        }
    }

    public EUIButton setDimensions(float width, float height) {
        this.hb.resize(width, height);

        return this;
    }

    public EUIButton setPosition(float cX, float cY) {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIButton setTooltip(String title, String description) {
        return setTooltip(new EUITooltip(title, description));
    }

    public EUIButton setTooltip(EUITooltip tooltip) {
        super.setTooltip(tooltip);

        return this;
    }

    public boolean isInteractable() {
        return interactable && onLeftClick != null;
    }

    public EUIButton setInteractable(boolean interactable) {
        this.interactable = interactable;

        return this;
    }

    protected void onJustHovered() {
        CardCrawlGame.sound.play("UI_HOVER");
    }

    protected void onRightClick() {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onRightClick != null) {
            this.onRightClick.complete(this);
        }
    }

    protected void onClickStart() {
        this.hb.clickStarted = true;
        CardCrawlGame.sound.play("UI_CLICK_1");
    }

    protected void onLeftClick() {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onLeftClick != null) {
            this.onLeftClick.complete(this);
        }
    }
}