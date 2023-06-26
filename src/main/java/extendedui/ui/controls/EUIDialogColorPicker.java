package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

public class EUIDialogColorPicker extends EUIDialog<Color> {
    protected static final int HUE_CONSTANT = 255;
    protected EUIColorPicker picker;
    protected EUITextBoxNumericalInput inputHue;
    protected EUITextBoxNumericalInput inputSaturation;
    protected EUITextBoxNumericalInput inputValue;
    protected EUITextBoxNumericalInput inputAlpha;
    protected EUILabel sampleColorHeader;
    protected EUIImage sampleColor;

    public EUIDialogColorPicker(String headerText) {
        this(headerText, "");
    }

    public EUIDialogColorPicker(String headerText, String descriptionText) {
        this(headerText, descriptionText, scale(460), scale(800));
    }

    public EUIDialogColorPicker(String headerText, String descriptionText, float w, float h) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - w / 2f, Settings.HEIGHT / 2.0F - h / 2f, w, h), ImageMaster.OPTION_CONFIRM, headerText, descriptionText);
    }

    public EUIDialogColorPicker(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
        this.picker = new EUIColorPicker(new RelativeHitbox(hb, hb.width / 2.5f, hb.width / 2.5f, hb.width / 2.3f, hb.height / 1.4f));
        this.inputHue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width * 0.3f, hb.y + hb.height / 2.3f, hb.width * 0.125f, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.misc_hue)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.inputSaturation = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width * 0.3f, inputHue.hb.y - inputHue.hb.height * 1.2f, hb.width * 0.125f, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.misc_saturation)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.inputValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width * 0.3f, inputSaturation.hb.y - inputSaturation.hb.height * 1.2f, hb.width * 0.125f, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.misc_value)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.inputAlpha = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(hb.x + hb.width * 0.3f, inputValue.hb.y - inputValue.hb.height * 1.2f, hb.width * 0.125f, scale(54)))
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.misc_opacity)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
        this.sampleColorHeader = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(hb.x + hb.width * 0.6f, inputSaturation.header.hb.y, scale(50), scale(50)))
                .setFontScale(0.8f)
                .setColor(Settings.BLUE_TEXT_COLOR)
                .setLabel(EUIRM.strings.misc_current)
                .setAlignment(0.5f, 0.0f, false);
        this.sampleColor = new EUIImage(EUIRM.images.squaredButton2.texture(), new EUIHitbox(hb.x + hb.width * 0.6f, sampleColorHeader.hb.y - scale(40), scale(50), scale(50)));
        this.inputHue
                .setOnComplete(i -> picker.setHue((float) i / HUE_CONSTANT));
        this.inputSaturation
                .setOnComplete(i -> picker.setSat((float) i / HUE_CONSTANT));
        this.inputValue
                .setOnComplete(i -> picker.setVal((float) i / HUE_CONSTANT));
        this.inputAlpha
                .setOnComplete(i -> picker.setAlpha((float) i / HUE_CONSTANT));
        this.picker.setOnChange((__) -> updateInputs());
    }

    protected EUILabel getHeader(String headerText) {
        return new EUILabel(EUIFontHelper.buttonFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.88f))
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(headerText);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        this.picker.renderImpl(sb);
        this.inputHue.renderImpl(sb);
        this.inputSaturation.renderImpl(sb);
        this.inputValue.renderImpl(sb);
        this.inputAlpha.renderImpl(sb);
        this.sampleColor.renderImpl(sb);
        this.sampleColorHeader.renderImpl(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.picker.updateImpl();
        this.inputHue.updateImpl();
        this.inputSaturation.updateImpl();
        this.inputValue.updateImpl();
        this.inputAlpha.updateImpl();
        this.sampleColor.updateImpl();
    }

    protected String getCancelText() {
        return CHOICE_TEXT[3];
    }

    protected String getConfirmText() {
        return CHOICE_TEXT[2];
    }

    @Override
    public Color getConfirmValue() {
        return picker.getReturnColor();
    }

    @Override
    public Color getCancelValue() {
        return null;
    }

    public void open(Color initial) {
        picker.setColor(initial);
        updateInputs();
    }

    protected void updateInputs() {
        this.inputHue
                .forceSetValue((int) (picker.getHue() * HUE_CONSTANT), false);
        this.inputSaturation
                .forceSetValue((int) (picker.getSat() * HUE_CONSTANT), false);
        this.inputValue
                .forceSetValue((int) (picker.getVal() * HUE_CONSTANT), false);
        this.inputAlpha
                .forceSetValue((int) (picker.getAlpha() * HUE_CONSTANT), false);
        this.sampleColor.setColor(picker.getReturnColor());
    }
}
