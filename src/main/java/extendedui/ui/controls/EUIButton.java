package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.text.EUITextHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.TupleT2;

public class EUIButton extends EUIHoverable {
    private ActionT1<EUIButton> onLeftClick;
    private ActionT1<EUIButton> onLeftPreClick;
    private ActionT1<EUIButton> onRightClick;
    protected float currentClickDelay = 0f;
    public EUIImage background;
    public EUIImage border;
    public EUILabel label;
    public float clickDelay = 0f;
    public boolean interactable = true;
    public boolean showText = true;
    public Color hoverBlendColor;

    public EUIButton(Texture buttonTexture, float x, float y) {
        this(buttonTexture, new EUIHitbox(x, y, scale(buttonTexture.getWidth()), scale(buttonTexture.getHeight())));
    }

    public EUIButton(Texture buttonTexture, EUIHitbox hitbox) {
        super(hitbox);
        this.background = new EUIImage(buttonTexture, hitbox, Color.WHITE);
    }

    public static EUIButton createHexagonalButton(float x, float y, float width, float height) {
        final Texture buttonTexture = EUIRM.images.hexagonalButton.texture();
        final Texture buttonBorderTexture = EUIRM.images.hexagonalButtonBorder.texture();
        return new EUIButton(buttonTexture, x, y)
                .setBorder(buttonBorderTexture, Color.WHITE)
                .setClickDelay(0.25f)
                .setDimensions(width, height);
    }

    protected boolean didClick() {
        return this.hb.clicked && EUI.tryClick(this.hb);
    }

    public boolean isInteractable() {
        return interactable && (onLeftClick != null || onLeftPreClick != null);
    }

    public EUIButton makeCopy() {
        EUIButton copy = new EUIButton(this.background.texture, this.hb.makeCopy())
                .setOnClick(onLeftClick)
                .setOnPreClick(onLeftPreClick)
                .setOnRightClick(onRightClick);
        if (border != null) {
            copy.setBorder(border.texture, border.color);
        }
        if (label != null) {
            copy.setLabel(label.font, label.fontScale, label.text);
        }
        copy.setTooltip(this.tooltip);
        return copy;
    }

    public EUITourTooltip makeTour(boolean canDismiss) {
        if (tooltip != null) {
            EUITourTooltip tip = new EUITourTooltip(hb, tooltip.title, tooltip.description);
            tip.setFlash(background);
            tip.setCanDismiss(canDismiss);
            return tip;
        }
        return null;
    }

    protected void onClickStart() {
        this.hb.clickStarted = true;
        CardCrawlGame.sound.play("UI_CLICK_1");

        if (onLeftPreClick != null) {
            this.onLeftPreClick.invoke(this);
        }
    }

    protected void onJustHovered() {
        CardCrawlGame.sound.play("UI_HOVER");
    }

    protected void onLeftClick() {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onLeftClick != null) {
            this.onLeftClick.invoke(this);
        }
    }

    protected void onRightClick() {
        this.hb.clicked = false;
        this.currentClickDelay = clickDelay;

        if (onRightClick != null) {
            this.onRightClick.invoke(this);
        }
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

    protected void renderButtonCentered(SpriteBatch sb, boolean interactable) {
        background.renderCentered(sb, hb);

        if (border != null) {
            border.renderCentered(sb);
        }

        if (interactable && this.hb.hovered && !this.hb.clickStarted) {
            background.renderCentered(sb, EUIRenderHelpers.ShaderMode.Bright, hb, hoverBlendColor != null ? hoverBlendColor : HOVER_BLEND_COLOR);
        }
    }

    public void renderCentered(SpriteBatch sb) {
        final boolean interactable = isInteractable();
        if (label != null && showText) {
            this.renderButtonCentered(sb, interactable);
            label.renderImpl(sb, hb, interactable ? label.textColor : TEXT_DISABLED_COLOR);
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

    @Override
    public void renderImpl(SpriteBatch sb) {
        final boolean interactable = isInteractable();

        if (label != null && showText) {
            this.renderButton(sb, interactable);
            label.renderImpl(sb, hb, interactable ? label.textColor : TEXT_DISABLED_COLOR);
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

    public EUIButton setBackground(Texture borderTexture) {
        this.background = new EUIImage(borderTexture, Color.WHITE);

        return this;
    }

    public EUIButton setBackground(EUIImage background) {
        this.background = background;

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

    public EUIButton setBorder(EUIImage border) {
        this.border = border;
        if (this.border != null) {
            this.border.setHitbox(hb);
        }

        return this;
    }

    public EUIButton setButtonFlip(boolean flipX, boolean flipY) {
        this.background.setFlipping(flipX, flipY);

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

    public EUIButton setDimensions(float width, float height) {
        this.hb.resize(width, height);

        return this;
    }

    public EUIButton setHoverBlendColor(Color color) {
        this.hoverBlendColor = color;
        return this;
    }

    public EUIButton setInteractable(boolean interactable) {
        this.interactable = interactable;

        return this;
    }

    public EUIButton setLabel(EUILabel label) {
        this.label = label;
        return this;
    }

    public EUIButton setLabel(String text) {
        return setLabel(EUIFontHelper.buttonFont, 1, text);
    }

    public EUIButton setLabel(BitmapFont font, float fontScale, String text) {
        this.label = new EUILabel(font, hb, fontScale, 0.5f, 0.5f, false);
        setText(text);
        return this;
    }

    public EUIButton setOnClick(ActionT0 onClick) {
        this.onLeftClick = (__) -> onClick.invoke();

        return this;
    }

    public EUIButton setOnClick(ActionT1<EUIButton> onClick) {
        this.onLeftClick = onClick;

        return this;
    }

    public <S> EUIButton setOnClick(S item, ActionT1<S> onClick) {
        this.onLeftClick = (__) -> onClick.invoke(item);

        return this;
    }

    public <S> EUIButton setOnClick(S item, ActionT2<S, EUIButton> onClick) {
        this.onLeftClick = (s) -> onClick.invoke(item, s);

        return this;
    }

    public EUIButton setOnPreClick(ActionT0 onClick) {
        this.onLeftPreClick = (__) -> onClick.invoke();

        return this;
    }

    public EUIButton setOnPreClick(ActionT1<EUIButton> onClick) {
        this.onLeftPreClick = onClick;

        return this;
    }

    public <S> EUIButton setOnPreClick(S item, ActionT1<S> onClick) {
        this.onLeftPreClick = (__) -> onClick.invoke(item);

        return this;
    }

    public <S> EUIButton setOnPreClick(S item, ActionT2<S, EUIButton> onClick) {
        this.onLeftPreClick = (s) -> onClick.invoke(item, s);

        return this;
    }

    public EUIButton setOnRightClick(ActionT0 onClick) {
        this.onRightClick = (__) -> onClick.invoke();

        return this;
    }

    public EUIButton setOnRightClick(ActionT1<EUIButton> onClick) {
        this.onRightClick = onClick;

        return this;
    }

    public <S> EUIButton setOnRightClick(S item, ActionT1<S> onClick) {
        this.onRightClick = (__) -> onClick.invoke(item);

        return this;
    }

    public <S> EUIButton setOnRightClick(S item, ActionT2<S, EUIButton> onClick) {
        this.onRightClick = (s) -> onClick.invoke(item, s);

        return this;
    }

    public EUIButton setPosition(float cX, float cY) {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIButton setShaderMode(EUIRenderHelpers.ShaderMode shaderMode) {
        this.background.setShaderMode(shaderMode);
        return this;
    }

    public EUIButton setTargetColor(Color buttonColor) {
        background.setTargetColor(buttonColor);

        return this;
    }

    public EUIButton setText(String text) {
        verifyLabel();
        if (this.label.smartText) {
            return setTextAndAlign(text);
        }
        else {
            return setTextAndResize(text);
        }
    }

    public EUIButton setTextAndAlign(String text) {
        return setTextAndAlign(text, 0.5f);
    }

    public EUIButton setTextAndAlign(String text, float baseW) {
        return setTextAndAlign(text, baseW, baseW);
    }

    public EUIButton setTextAndAlign(String text, float baseW, float rateW) {
        this.label.setLabel(text);
        label.font.getData().setScale(label.fontScale);
        float smartWidth = EUITextHelper.getSmartWidth(label.font, text, hb.width, 0f);
        this.label.horizontalRatio = Math.max(0, baseW - (smartWidth / hb.width) * rateW);
        EUIRenderHelpers.resetFont(label.font);
        return this;
    }

    public EUIButton setTextAndAlign2D(String text) {
        return setTextAndAlign2D(text, 0.7f, 0.5f);
    }

    public EUIButton setTextAndAlign2D(String text, float rateH, float rateW) {
        return setTextAndAlign2D(text, rateH, rateW, rateH, rateW);
    }

    public EUIButton setTextAndAlign2D(String text, float baseH, float baseW, float rateH, float rateW) {
        this.label.setLabel(text);
        label.font.getData().setScale(label.fontScale);
        TupleT2<Float, Float> smartSize = EUITextHelper.getSmartSize(label.font, text, hb.width, 0f);
        this.label.horizontalRatio = Math.max(0, baseW - (smartSize.v1 / hb.width) * rateW);
        this.label.verticalRatio = Math.max(0, baseH - (smartSize.v2 / hb.height) * rateH);
        EUIRenderHelpers.resetFont(label.font);
        return this;
    }

    public EUIButton setTextAndResize(String text) {
        return setTextAndResize(text, 0.8f, 0.84f);
    }

    public EUIButton setTextAndResize(String text, float targetShrink) {
        return setTextAndResize(text, targetShrink, 0.84f);
    }

    public EUIButton setTextAndResize(String text, float targetShrink, float threshold) {
        this.label.setLabel(text);
        if (EUITextHelper.getSmartWidth(label.font, text, Integer.MAX_VALUE, 0f) * label.fontScale > hb.width * threshold) {
            this.label.setFontScaleRelative(targetShrink);
        }
        else {
            this.label.setFontScaleRelative(1f);
        }
        return this;
    }

    public EUIButton setTooltip(String title, String description) {
        return setTooltip(new EUITooltip(title, description));
    }

    public EUIButton setTooltip(EUITooltip tooltip) {
        super.setTooltip(tooltip);

        return this;
    }

    public boolean tryRenderCentered(SpriteBatch sb) {
        if (isActive) {
            this.hb.render(sb);
            renderCentered(sb);
        }

        return isActive;
    }

    @Override
    public void updateImpl() {
        if (currentClickDelay > 0) {
            this.currentClickDelay -= EUI.delta();
        }

        super.updateImpl();
        background.updateColor();

        if (isInteractable() && !EUITourTooltip.shouldBlockInteract(this.hb)) {
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

            if (didClick()) {
                onLeftClick();
            }
        }
    }

    protected void verifyLabel() {
        if (label == null) {
            label = new EUILabel(EUIFontHelper.buttonFont, hb, 1, 0.5f, 0.5f, false);
        }
    }
}