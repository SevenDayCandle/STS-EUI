package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.GUI_Base;
import extendedui.ui.GUI_Hoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.FieldInfo;

public abstract class GUI_Dialog<T> extends GUI_Hoverable
{
    private final static FieldInfo<String[]> _text = JavaUtils.GetField("TEXT", ConfirmPopup.class);

    protected Color screenColor = new Color(0.0F, 0.0F, 0.0F, 0.5F);
    protected GUI_Button confirm;
    protected GUI_Button cancel;
    protected GUI_Label header;
    protected GUI_Label description;
    protected ActionT1<T> onComplete;

    public GUI_Dialog(String headerText)
    {
        this(headerText, "");
    }

    public GUI_Dialog(String headerText, String descriptionText)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 180.0F, Settings.OPTION_Y - 207.0F, 360.0F, 414.0F), headerText, descriptionText);
    }

    public GUI_Dialog(AdvancedHitbox hb, String headerText, String descriptionText)
    {
        super(hb);
        this.header = new GUI_Label(FontHelper.buttonLabelFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.9f, false))
                .SetAlignment(0.5f,0.5f,false)
                .SetText(headerText);
        this.description = new GUI_Label(FontHelper.tipBodyFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.1f, hb.height * 0.7f, false))
                .SetAlignment(0.5f,0.5f,true)
                .SetSmartText(true, false)
                .SetText(descriptionText);
        this.confirm = new GUI_Button(ImageMaster.OPTION_YES,
                new RelativeHitbox(hb, 173.0F, 74.0F, hb.width * 0.1f, hb.height * 0.05f, false))
                .SetFont(FontHelper.cardTitleFont, 1f)
                .SetText(_text.Get(null)[2])
                .SetOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.Invoke(GetConfirmValue());
                    }
                });
        this.cancel = new GUI_Button(ImageMaster.OPTION_NO,
                new RelativeHitbox(hb, 173.0F, 74.0F, hb.width * 0.9f, hb.height * 0.05f, false))
                .SetFont(FontHelper.cardTitleFont, 1f)
                .SetText(_text.Get(null)[3])
                .SetOnClick(() -> {
                    if (onComplete != null) {
                        onComplete.Invoke(GetCancelValue());
                    }
                });
    }

    public GUI_Dialog<T> SetDescriptionProperties(BitmapFont font, float fontScale, Color textColor) {
        return this.SetDescriptionProperties(font, fontScale, textColor, false);
    }

    public GUI_Dialog<T> SetDescriptionProperties(BitmapFont font, float fontScale, Color textColor, boolean smartText) {
        this.description.SetFont(font, fontScale).SetColor(textColor).SetSmartText(smartText);
        return this;
    }

    public GUI_Dialog<T> SetDescriptionText(String text) {
        description.SetText(text);
        return this;
    }

    public GUI_Dialog<T> SetHeaderProperties(BitmapFont font, float fontScale, Color textColor) {
        return this.SetHeaderProperties(font, fontScale, textColor, false);
    }

    public GUI_Dialog<T> SetHeaderProperties(BitmapFont font, float fontScale, Color textColor, boolean smartText) {
        this.header.SetFont(font, fontScale).SetColor(textColor).SetSmartText(smartText);
        return this;
    }

    public GUI_Dialog<T> SetHeaderText(String text) {
        header.SetText(text);
        return this;
    }

    public GUI_Dialog<T> SetOnComplete(ActionT1<T> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    @Override
    public void Update()
    {
        super.Update();
        this.header.TryUpdate();
        this.description.TryUpdate();
        this.confirm.TryUpdate();
        this.cancel.TryUpdate();

        if (CInputActionSet.proceed.isJustPressed()) {
            CInputActionSet.proceed.unpress();
            if (onComplete != null) {
                onComplete.Invoke(GetConfirmValue());
            }
        }

        if (CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            if (onComplete != null) {
                onComplete.Invoke(GetCancelValue());
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.OPTION_CONFIRM, hb.x, hb.y, hb.width / 2, hb.height / 2, hb.width, hb.height, Settings.scale, Settings.scale, 0.0F, 0, 0, 360, 414, false, false);

        this.header.TryRender(sb);
        this.description.TryRender(sb);
        this.confirm.TryRender(sb);
        this.cancel.TryRender(sb);

        if (Settings.isControllerMode) {
            sb.draw(CInputActionSet.proceed.getKeyImg(), this.confirm.hb.cX, this.confirm.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            sb.draw(CInputActionSet.cancel.getKeyImg(), this.cancel.hb.cX, this.cancel.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
    }

    public abstract T GetConfirmValue();
    public abstract T GetCancelValue();
}
