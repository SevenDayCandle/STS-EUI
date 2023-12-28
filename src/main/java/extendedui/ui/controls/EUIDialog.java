package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

import static extendedui.EUIRenderHelpers.DARKENED_SCREEN;

public abstract class EUIDialog<T> extends EUIHoverable {
    protected final static String[] TEXT = CardCrawlGame.languagePack.getUIString("ConfirmPopup").TEXT;
    protected final static String[] CHOICE_TEXT = CardCrawlGame.languagePack.getUIString("SeedPanel").TEXT;
    protected boolean setShowDark = true;
    protected EUIButton confirm;
    protected EUIButton cancel;
    protected EUILabel header;
    protected EUILabel description;
    protected EUIImage backgroundImage;
    protected ActionT1<T> onComplete;

    public EUIDialog(String headerText) {
        this(headerText, "");
    }

    public EUIDialog(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(300), scale(390));
    }

    public EUIDialog(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox((Settings.WIDTH - w) / 2f, (Settings.HEIGHT - h ) / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        this(hb, new EUIImage(backgroundTexture, hb), headerText, descriptionText);
    }

    public EUIDialog(EUIHitbox hb, EUIImage backgroundImage, String headerText, String descriptionText) {
        super(hb);
        this.backgroundImage = backgroundImage;
        this.header = getHeader(headerText);
        this.description = getDescription(descriptionText);
        this.confirm = getConfirmButton();
        this.cancel = getCancelButton();
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, scale(152), scale(80), hb.width * 0.85f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 1f, getCancelText())
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
    }

    protected String getCancelText() {
        return TEXT[3];
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, scale(152), scale(80), hb.width * 0.15f, hb.height * 0.15f))
                .setLabel(EUIFontHelper.cardTitleFontNormal, 1f, getConfirmText())
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    protected String getConfirmText() {
        return TEXT[2];
    }

    protected EUILabel getDescription(String descriptionText) {
        return new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(hb, hb.width * 0.75f, hb.height, hb.width * 0.15f, hb.height * 0.7f))
                .setAlignment(0.5f, 0.5f, true)
                .setSmartText(true, false)
                .setLabel(descriptionText);
    }

    protected EUILabel getHeader(String headerText) {
        return new EUILabel(EUIFontHelper.buttonFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.8f))
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(headerText);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (setShowDark) {
            sb.setColor(DARKENED_SCREEN);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        }
        sb.setColor(Color.WHITE);
        this.backgroundImage.tryRender(sb);

        this.header.tryRender(sb);
        this.description.tryRender(sb);
        this.confirm.tryRender(sb);
        this.cancel.tryRender(sb);

        if (Settings.isControllerMode) {
            sb.draw(CInputActionSet.proceed.getKeyImg(), this.confirm.hb.cX, this.confirm.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            sb.draw(CInputActionSet.cancel.getKeyImg(), this.cancel.hb.cX, this.cancel.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
    }

    public EUIDialog<T> setCancelText(String text) {
        confirm.setText(text);
        return this;
    }

    public EUIDialog<T> setConfirmText(String text) {
        cancel.setText(text);
        return this;
    }

    public EUIDialog<T> setDescriptionProperties(BitmapFont font, float fontScale, Color textColor) {
        return this.setDescriptionProperties(font, fontScale, textColor, false);
    }

    public EUIDialog<T> setDescriptionProperties(BitmapFont font, float fontScale, Color textColor, boolean smartText) {
        this.description.setFont(font, fontScale).setColor(textColor).setSmartText(smartText);
        return this;
    }

    public EUIDialog<T> setDescriptionText(String text) {
        description.setLabel(text);
        return this;
    }

    public EUIDialog<T> setEnableCancel(boolean val) {
        this.cancel.setActive(val);
        return this;
    }

    public EUIDialog<T> setEnableConfirm(boolean val) {
        this.confirm.setActive(val);
        return this;
    }

    public EUIDialog<T> setHeaderProperties(BitmapFont font, float fontScale, Color textColor) {
        return this.setHeaderProperties(font, fontScale, textColor, false);
    }

    public EUIDialog<T> setHeaderProperties(BitmapFont font, float fontScale, Color textColor, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setSmartText(smartText);
        return this;
    }

    public EUIDialog<T> setHeaderText(String text) {
        header.setLabel(text);
        return this;
    }

    public EUIDialog<T> setOnComplete(ActionT1<T> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public EUIDialog<T> setShowDark(boolean val) {
        this.setShowDark = val;
        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.header.tryUpdate();
        this.description.tryUpdate();
        this.confirm.tryUpdate();
        this.cancel.tryUpdate();

        if (CInputActionSet.proceed.isJustPressed()) {
            CInputActionSet.proceed.unpress();
            if (onComplete != null) {
                onComplete.invoke(getConfirmValue());
            }
        }

        if (CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            if (onComplete != null) {
                onComplete.invoke(getCancelValue());
            }
        }
    }

    public abstract T getConfirmValue();

    public abstract T getCancelValue();
}
