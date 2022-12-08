package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

import static extendedui.EUIRenderHelpers.DARKENED_SCREEN;

public abstract class EUIDialog<T> extends EUIHoverable
{
    protected final static String[] TEXT = CardCrawlGame.languagePack.getUIString("ConfirmPopup").TEXT;
    protected EUIButton confirm;
    protected EUIButton cancel;
    protected EUILabel header;
    protected EUILabel description;
    protected EUIImage backgroundImage;
    protected ActionT1<T> onComplete;

    public EUIDialog(String headerText)
    {
        this(headerText, "");
    }

    public EUIDialog(String headerText, String descriptionText)
    {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialog(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText)
    {
        super(hb);
        this.backgroundImage = new EUIImage(backgroundTexture, hb);
        this.header = new EUILabel(FontHelper.buttonLabelFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.9f))
                .setAlignment(0.5f,0.5f,false)
                .setLabel(headerText);
        this.description = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.1f, hb.height * 0.7f))
                .setAlignment(0.5f,0.5f,true)
                .setSmartText(true, false)
                .setLabel(descriptionText);
        this.confirm = getConfirmButton();
        this.cancel = getCancelButton();
    }

    protected EUIButton getConfirmButton() {
        return new EUIButton(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, 173.0F, 74.0F, hb.width * 0.1f, hb.height * 0.05f))
                .setFont(EUIFontHelper.cardtitlefontNormal, 1f)
                .setText(TEXT[2])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getConfirmValue());
                    }
                });
    }

    protected EUIButton getCancelButton() {
        return new EUIButton(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, 173.0F, 74.0F, hb.width * 0.9f, hb.height * 0.05f))
                .setFont(EUIFontHelper.cardtitlefontNormal, 1f)
                .setText(TEXT[3])
                .setOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.invoke(getCancelValue());
                    }
                });
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

    public EUIDialog<T> setHeaderProperties(BitmapFont font, float fontScale, Color textColor) {
        return this.setHeaderProperties(font, fontScale, textColor, false);
    }

    public EUIDialog<T> setHeaderProperties(BitmapFont font, float fontScale, Color textColor, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setSmartText(smartText);
        return this;
    }

    public EUIDialog<T> setCancelText(String text) {
        confirm.setText(text);
        return this;
    }

    public EUIDialog<T> setConfirmText(String text) {
        cancel.setText(text);
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

    @Override
    public void updateImpl()
    {
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

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        sb.setColor(DARKENED_SCREEN);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float)Settings.HEIGHT);
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

    public abstract T getConfirmValue();
    public abstract T getCancelValue();
}
