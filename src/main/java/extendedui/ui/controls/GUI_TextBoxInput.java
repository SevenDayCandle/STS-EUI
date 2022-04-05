package extendedui.ui.controls;

import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

public class GUI_TextBoxInput extends GUI_TextBox implements TextReceiver {
    protected boolean isEditing;
    protected ActionT1<String> onUpdate;
    protected ActionT1<String> onComplete;
    protected Color originalTextColor;
    protected Color editTextColor;
    protected GUI_Label header;
    private String originalValue;

    public GUI_TextBoxInput(Texture backgroundTexture, AdvancedHitbox hb) {
        super(backgroundTexture, hb);
        this.header = new GUI_Label(EUIFontHelper.CardTitleFont_Small,
                new AdvancedHitbox(hb.x, hb.y + hb.height * 0.6f, hb.width, hb.height)).SetAlignment(0.5f,0.0f,false);
        this.header.SetActive(false);
        editTextColor = EUIColors.Green(1).cpy();
        originalTextColor = this.label.textColor.cpy();
    }

    public GUI_TextBoxInput SetHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return SetHeader(font,fontScale,textColor,text,false);
    }

    public GUI_TextBoxInput SetHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.SetFont(font, fontScale).SetColor(textColor).SetText(text).SetSmartText(smartText).SetActive(true);

        return this;
    }

    public GUI_TextBoxInput SetOnComplete(ActionT1<String> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public GUI_TextBoxInput SetOnUpdate(ActionT1<String> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public GUI_TextBox SetColors(Color backgroundColor, Color textColor)
    {
        this.image.SetColor(backgroundColor);
        this.label.SetColor(textColor);
        originalTextColor = textColor;

        return this;
    }

    public GUI_TextBox SetFontColor(Color textColor)
    {
        this.label.SetColor(textColor);
        originalTextColor = textColor.cpy();

        return this;
    }

    public GUI_TextBox SetFontColor(Color textColor, Color editTextColor)
    {
        SetFontColor(textColor);
        this.editTextColor = editTextColor.cpy();

        return this;
    }


    @Override
    public void Update()
    {
        super.Update();
        if (EUIInputManager.LeftClick.IsJustReleased()) {
            if (!isEditing && (hb.hovered || hb.clicked)) {
                Start();
            }
            else if (isEditing && !hb.hovered && !hb.clicked) {
                End(true);
            }
        }
        else if (isEditing && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            End(false);
        }
        header.TryUpdate();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);
        header.TryRender(sb);
    }

    @Override
    public String getCurrentText() {
        return label.text;
    }

    @Override
    public void setText(String s) {
        label.text = s;
        if (onUpdate != null) {
            onUpdate.Invoke(label.text);
        }
    }

    @Override
    public boolean isDone() {
        return !isEditing;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c);
    }

    @Override
    public boolean onPushEnter() {
        End(true);
        return true;
    }

    public void Start() {
        isEditing = true;
        TextInput.startTextReceiver(this);
        label.SetColor(editTextColor);
        originalValue = label.text;
    }

    public void End(boolean commit) {
        isEditing = false;
        TextInput.stopTextReceiver(this);
        label.SetColor(originalTextColor);
        if (commit) {
            if (onComplete != null) {
                onComplete.Invoke(label.text);
            }
        }
        else {
            label.text = originalValue;
        }
    }
}
